package com.courrier.consumerdelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AwsConfig {
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1) // We can change this code to use properties and system variables.
                .build();
    }
}
