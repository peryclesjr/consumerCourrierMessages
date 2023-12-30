package com.courrier.consumerdelivery.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServiceMessageConsumerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ServiceMessageConsumer serviceMessageConsumer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveMessageDynamoDB_ThrowsJsonProcessingException() {
        // Mock DynamoDbClient
        DynamoDbClient dynamoDbClient = mock(DynamoDbClient.class);

        ServiceMessageConsumer service = new ServiceMessageConsumer();

        String invalidMessage = "invalid json";

        assertThrows(JsonProcessingException.class, () -> {
            service.saveMessageDynamoDB(invalidMessage);
        });
    }


//
}
