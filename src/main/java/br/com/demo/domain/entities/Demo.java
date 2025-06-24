package br.com.demo.domain.entities;

import br.com.demo.utils.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@DynamoDbBean
public class Demo {

    private UUID id;
    private String nome;

    @DynamoDbConvertedBy(UUIDConverter.class)
    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }
}
