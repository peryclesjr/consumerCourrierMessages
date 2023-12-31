package com.courrier.consumerdelivery.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ServiceMessageConsumerTest {

    private ServiceMessageConsumer serviceMessageConsumer;

    private DynamoDbClient dynamoDbClient;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dynamoDbClient = Mockito.mock(DynamoDbClient.class);
        serviceMessageConsumer = new ServiceMessageConsumer();
        ReflectionTestUtils.setField(serviceMessageConsumer, "dynamoDbClient", dynamoDbClient);
    }

    private void serviceMessageConsumer(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Test
    public void givenInvalidMessage_ThenThrowsJsonProcessingException() {
        // Mock DynamoDbClient
        DynamoDbClient dynamoDbClient = mock(DynamoDbClient.class);

        ServiceMessageConsumer service = new ServiceMessageConsumer();

        String invalidMessage = "{this is not a valid json}";;

        assertThrows(JsonProcessingException.class, () -> {
            service.saveMessageDynamoDB(invalidMessage, "createdDelivery");
        });
    }

    @Test
    public void givenMessageDynamoDB_ThenSuccess() throws JsonProcessingException {

        String validMessage = """
                {
                    "deliveryId": "5b8e27f4-8798-477b-9dec-55ae9ccc18f6",
                    "courierId": "17a3ce0e-9f37-44cf-9083-4b1c8e6c9d07",
                    "createdTimestamp": 1703978522,
                    "value": "10.0"
                }
                """;

        when(dynamoDbClient.putItem((PutItemRequest) any())).thenReturn(PutItemResponse.builder().build());

        serviceMessageConsumer.saveMessageDynamoDB(validMessage, "createdDelivery");

        Mockito.verify(dynamoDbClient).putItem((PutItemRequest) any());
    }

    @Test
    public void testSaveMessageDynamoDBAdjusementModified_Success() throws JsonProcessingException {

        String validMessage = """
                {
                      "adjustmentId": "b2984055-98a8-40fd-87e7-e2cbebc72e84",
                      "deliveryId": "e6783e82-6603-40af-95ba-fc35e5fb0bee",
                      "courierId":"4e5bd70e-6103-4448-8636-b852b85d988c",
                      "createdTimestamp":1703984394,
                      "value":0.000000
                      }
                """;

        when(dynamoDbClient.putItem((PutItemRequest) any())).thenReturn(PutItemResponse.builder().build());

        serviceMessageConsumer.saveMessageDynamoDB(validMessage, "adjustmentModified");

        Mockito.verify(dynamoDbClient).putItem((PutItemRequest) any());
    }

    @Test
    public void testSaveMessageDynamoDBonusModified_Success() throws JsonProcessingException {

        String validMessage = """
                {
                    "bonusId": "b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "deliveryId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "courierId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "createdTimestamp":"1703766503",
                    "value": 7.000000
                }
                """;

        when(dynamoDbClient.putItem((PutItemRequest) any())).thenReturn(PutItemResponse.builder().build());

        serviceMessageConsumer.saveMessageDynamoDB(validMessage, "bonusModified");

        Mockito.verify(dynamoDbClient).putItem((PutItemRequest) any());
    }

    @Test
    public void given_MessageAndWrongTable_ThrowsJsonProcessingException() throws JsonProcessingException {

        String validMessage = """
                {
                    "bonusId": "b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "deliveryId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "courierId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "createdTimestamp":"1703766503",
                    "value": 7.000000
                }
                """;

        //wrong table name
        assertThrows(JsonProcessingException.class, () -> {
            serviceMessageConsumer.saveMessageDynamoDB(validMessage, "createdDelivery");
        });
    }

    @Test
    public void given_WrongMessageAndRightTable_ThrowsJsonProcessingException() throws JsonProcessingException {

        String inValidMessage = """
                {
                    "bonusId": "b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "deliveryId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "courierId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "createdTimestamp":"1703766503",
                    "value": f%
                }
                """;

        assertThrows(JsonProcessingException.class, () -> {
            serviceMessageConsumer.saveMessageDynamoDB(inValidMessage, "bonusModified");
        });
    }

    @Test
    public void given_WrongIDMessageAndRightTable_ThrowsJsonProcessingException()
            throws JsonProcessingException {

        String inValidMessage = """
                {
                    "bonusId": "{",
                    "deliveryId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "courierId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "createdTimestamp":"1703766503",
                    "value": 10
                }
                """;

        assertThrows(JsonProcessingException.class, () -> {
            serviceMessageConsumer.saveMessageDynamoDB(inValidMessage, "bonusModified");
        });
    }

    @Test
    public void given_EmptyIDMessageAndRightTable_ThrowsJsonProcessingException()
            throws JsonProcessingException {

        String inValidMessage = """
                {
                    "bonusId": "",
                    "deliveryId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "courierId":"b2984055-98a8-40fd-87e7-e2cbebc72e84",
                    "createdTimestamp":"1703766503",
                    "value": 10
                }
                """;

        assertThrows(NullPointerException.class, () -> {
            serviceMessageConsumer.saveMessageDynamoDB(inValidMessage, "bonusModified");
        });
    }


}
