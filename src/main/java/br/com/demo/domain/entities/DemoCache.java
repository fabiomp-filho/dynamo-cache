package br.com.demo.domain.entities;

import br.com.demo.utils.UUIDConverter;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.UUID;

@Data
@DynamoDbBean
public class DemoCache {

    private UUID id;
    private String nome;
    private Long expirationTime;

    @DynamoDbConvertedBy(UUIDConverter.class)
    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    @DynamoDbAttribute("ttl")
    public Long getExpirationTime() {

        return expirationTime;
    }

}
