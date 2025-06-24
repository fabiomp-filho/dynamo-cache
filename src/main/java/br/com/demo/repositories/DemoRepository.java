package br.com.demo.repositories;

import br.com.demo.domain.entities.Demo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.util.UUID;

@Repository
public class DemoRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<Demo> table;

    public DemoRepository(@Qualifier("mainEnhancedDynamoClient") DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @PostConstruct
    public void init() {
        table = enhancedClient.table("Demo", TableSchema.fromBean(Demo.class));

        try {
            table.createTable();
        } catch (ResourceInUseException e) {
            System.out.println("******************** Tabela 'Demo' já existe. Ignorando criação. ********************");
        }
    }

    public void save(Demo demo) {
        table.putItem(demo);
    }

    public Demo getById(UUID id) {
        return table.getItem(r -> r.key(k -> k.partitionValue(id.toString())));
    }
}
