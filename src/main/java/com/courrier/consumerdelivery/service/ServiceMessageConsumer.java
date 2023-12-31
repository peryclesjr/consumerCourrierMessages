package com.courrier.consumerdelivery.service;

import com.courrier.consumerdelivery.dto.AdjustementModified;
import com.courrier.consumerdelivery.dto.BonusModified;
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
            saveMessageDynamoDB(message, "createdDelivery");
        }

        @RabbitListener(queues = "adjustmentModified")
        public void consumeMessageAdjustment(String message) throws JsonProcessingException {
            saveMessageDynamoDB(message, "adjustmentModified");
        }

        @RabbitListener(queues = "bonusModified")
        public void consumeMessagBonus(String message) throws JsonProcessingException {
            saveMessageDynamoDB(message, "bonusModified");
        }


    public void saveMessageDynamoDB(String message, String typeName) throws JsonProcessingException {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();




            itemValues=  extracted( message, typeName);
            String nameTable = "";
            switch (typeName)
            {
                case "createdDelivery":
                    nameTable = "Delivery";
                    break;
                case "adjustmentModified":
                    nameTable = "Adjustment";
                    break;
                case "bonusModified":
                    nameTable = "Bonus";
                    break;
            }
            PutItemRequest request = PutItemRequest.builder()
                    .tableName(nameTable)
                    .item(itemValues)
                    .build();

            PutItemResponse response = dynamoDbClient.putItem(request);

    }

    private HashMap<String, AttributeValue>  extracted(String
                                  message,
                                  String typeName) throws JsonProcessingException{
            HashMap<String, AttributeValue> map = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            switch (typeName) {
                case "createdDelivery":
                    DeliveryCreatedDTO delivery = objectMapper.readValue(message, DeliveryCreatedDTO.class);
                    map.put("delivery_id", AttributeValue.builder().s(
                            delivery.getDeliveryId().toString()).build());
                    map.put("courierId", AttributeValue.builder().s(
                            delivery.getCourierId().toString()).build());
                    map.put("createdTimestamp", AttributeValue.builder().n(
                            String.valueOf(delivery.getCreatedTimestamp().toEpochMilli())).build());
                    map.put("value", AttributeValue.builder().n(delivery.getValue()).build());
                    break;
                case "adjustmentModified":
                    AdjustementModified adjustementModified = objectMapper.readValue(message, AdjustementModified.class);
                    map.put("adjustment_id",AttributeValue.builder().s(
                            adjustementModified.getAdjustmentId().toString()).build());
                    map.put("delivery_id", AttributeValue.builder().s
                            (adjustementModified.getDeliveryId().toString()).build());
                    map.put("courierId", AttributeValue.builder().s(
                            adjustementModified.getCourierId().toString()).build());
                    map.put("createdTimestamp", AttributeValue.builder().n(
                            String.valueOf(adjustementModified.getCreatedTimestamp().toEpochMilli())).build());
                    map.put("value", AttributeValue.builder().n(adjustementModified.getValue()).build());
                    break;
                case "bonusModified":
                    BonusModified bonusModified = objectMapper.readValue(message, BonusModified.class);
                    map.put("bonus_id", AttributeValue.builder().
                            s(bonusModified.getBonusId().toString()).build());
                    map.put("delivery_id", AttributeValue.builder().
                            s(bonusModified.getDeliveryId().toString()).build());
                    map.put("courierId",
                            AttributeValue.builder().s(bonusModified.getCourierId().toString()).build());
                    map.put("createdTimestamp", AttributeValue.builder().n(
                            String.valueOf(bonusModified.getCreatedTimestamp().toEpochMilli())).build());
                    map.put("value", AttributeValue.builder().n(bonusModified.getValue()).build());
                    break;
            }
            return map;

    }
}
