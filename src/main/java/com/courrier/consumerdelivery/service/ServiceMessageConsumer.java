package com.courrier.consumerdelivery.service;

import com.courrier.consumerdelivery.dto.DeliveryCreatedDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.HashMap;

@Service
public class ServiceMessageConsumer {


        private DynamoDbClient dynamoDbClient;
        public ServiceMessageConsumer() {
        }
        @Autowired
        public ServiceMessageConsumer(DynamoDbClient dynamoDbClient) {
            this.dynamoDbClient = dynamoDbClient;
        }

        @RabbitListener(queues = "createdDelivery")
        public void consumeMessageCreated(String message) throws JsonProcessingException {
            saveMessageDynamoDB(message);
            System.out.println("Mensagem recebida: " + message);
        }

        @RabbitListener(queues = "adjustmentModified")
        public void consumeMessageAdjustment(String message) {

            System.out.println("Mensagem recebida: " + message);

        }

        @RabbitListener(queues = "bonusModified")
        public void consumeMessagBonus(String message) {

            System.out.println("Mensagem recebida: " + message);

        }


    public void saveMessageDynamoDB(String message) throws JsonProcessingException {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        DeliveryCreatedDTO delivery = objectMapper.readValue(message, DeliveryCreatedDTO.class);
        try {

            itemValues.put("delivery_id", AttributeValue.builder().s(delivery.getDeliveryId().toString()).build());
            itemValues.put("courierId", AttributeValue.builder().s(delivery.getCourierId().toString()).build());
            itemValues.put("createdTimestamp", AttributeValue.builder().n(String.valueOf(delivery.getCreatedTimestamp().toEpochMilli())).build());
            itemValues.put("value", AttributeValue.builder().n(delivery.getValue()).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName("Delivery")
                    .item(itemValues)
                    .build();

            PutItemResponse response = dynamoDbClient.putItem(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
