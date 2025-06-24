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
import software.amazon.awssdk.services.dynamodb.model.TimeToLiveSpecification;
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest;

import java.net.URI;
import java.util.List;

@Configuration
public class CacheDynamoConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheDynamoConfig.class);

    @Value("${aws.dynamo.cache.region}")
    private String region;

    @Value("${aws.dynamo.cache.endpoint}")
    private String endpoint;

    @Value("${aws.dynamo.cache.credentials.access-key}")
    private String accessKey;

    @Value("${aws.dynamo.cache.credentials.secret-key}")
    private String secretKey;

    List<String> tablesTTL = List.of("DemoCache");

    @PostConstruct
    public void logConfigurations() {
        log.info("Cache Dynamo Region: {}", region);
        log.info("Cache Endpoint: {}", endpoint);
        log.info("Access Key: {}", accessKey);
        log.info("Secret Key: {}", secretKey);
        DynamoDbClient client = buildClient(endpoint);
        for (String tabela : tablesTTL) {
            configTTL(client, tabela, "ttl");
        }    }

    @Bean("cacheEnhancedDynamoClient")
    public DynamoDbEnhancedClient cacheEnhancedClient() {
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

    private void configTTL(DynamoDbClient client, String tableName, String ttlField) {
        try {
            client.updateTimeToLive(UpdateTimeToLiveRequest.builder()
                    .tableName(tableName)
                    .timeToLiveSpecification(TimeToLiveSpecification.builder()
                            .enabled(true)
                            .attributeName(ttlField)
                            .build())
                    .build());

            log.info("TTL ativado para a tabela '{}', usando o campo '{}'", tableName, ttlField);
        } catch (Exception e) {
            log.error("Erro ao ativar TTL para a tabela '{}': {}", tableName, e.getMessage());
        }
    }
}
