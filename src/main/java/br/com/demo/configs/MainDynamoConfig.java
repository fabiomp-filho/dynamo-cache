package br.com.demo.configs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class MainDynamoConfig {

    private static final Logger log = LoggerFactory.getLogger(MainDynamoConfig.class);

    @Value("${aws.dynamo.main.region}")
    private String region;

    @Value("${aws.dynamo.main.endpoint}")
    private String endpoint;

    @Value("${aws.dynamo.main.credentials.access-key}")
    private String accessKey;

    @Value("${aws.dynamo.main.credentials.secret-key}")
    private String secretKey;

    @PostConstruct
    public void logConfigurations() {
        log.info("Main Dynamo Region: {}", region);
        log.info("Main Endpoint: {}", endpoint);
    }

    @Bean("mainEnhancedDynamoClient")
    public DynamoDbEnhancedClient mainEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(buildClient(endpoint))
                .build();
    }

    private DynamoDbClient buildClient(String endpoint) {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                accessKey,
                                secretKey
                        )))
                .build();
    }
}
