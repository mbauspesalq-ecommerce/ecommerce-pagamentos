package com.mbauspesalq.ecommerce.pagamentos.repository

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
class DynamoDbConfig {
    @Value("\${aws.dynamodb.endpoint}")
    private lateinit var dynamoDbEndpoint: String

    @Value("\${aws.dynamodb.region}")
    private lateinit var dynamoDbRegion: String

    @Value("\${aws.dynamodb.access-key}")
    private lateinit var accessKey: String

    @Value("\${aws.dynamodb.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun dynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(dynamoDbEndpoint))
            .region(Region.of(dynamoDbRegion))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .build()
    }
}