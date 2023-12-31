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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ServiceMessageConsumerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

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
    public void testSaveMessageDynamoDB_ThrowsJsonProcessingException() {
        // Mock DynamoDbClient
        DynamoDbClient dynamoDbClient = mock(DynamoDbClient.class);

        ServiceMessageConsumer service = new ServiceMessageConsumer();

        String invalidMessage = "invalid json";

        assertThrows(JsonProcessingException.class, () -> {
            service.saveMessageDynamoDB(invalidMessage);
        });
    }

    @Test
    public void testSaveMessageDynamoDB_Success() throws JsonProcessingException {

        // Substitua com uma mensagem válida
        String validMessage = """
                {
                    "deliveryId": "5b8e27f4-8798-477b-9dec-55ae9ccc18f6",
                    "courierId": "17a3ce0e-9f37-44cf-9083-4b1c8e6c9d07",
                    "createdTimestamp": 1703978522,
                    "value": "10.0"
                }
                """; // JSON válido correspondente a DeliveryCreatedDTO

        // Configuração do mock para simular uma resposta bem-sucedida
        when(dynamoDbClient.putItem((PutItemRequest) any())).thenReturn(PutItemResponse.builder().build());

        // Executa o método com a mensagem válida
        serviceMessageConsumer.saveMessageDynamoDB(validMessage);

        // Verifica se o método putItem foi chamado
        Mockito.verify(dynamoDbClient).putItem((PutItemRequest) any());
    }


//
}
