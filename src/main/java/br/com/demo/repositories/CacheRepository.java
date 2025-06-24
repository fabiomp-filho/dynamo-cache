package br.com.demo.repositories;

import br.com.demo.domain.entities.DemoCache;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.util.UUID;

@Repository
public class CacheRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<DemoCache> table;

    public CacheRepository(@Qualifier("cacheEnhancedDynamoClient") DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @PostConstruct
    public void init() {
        table = enhancedClient.table("DemoCache", TableSchema.fromBean(DemoCache.class));

        try {
            table.createTable();
        } catch (ResourceInUseException e) {
            System.out.println("******************** Tabela 'DemoCache' já existe. Ignorando criação. ********************");
        }
    }

    public void save(DemoCache demo) {
        table.putItem(demo);
    }

    public DemoCache getById(UUID id) {
        return table.getItem(r -> r.key(k -> k.partitionValue(id.toString())));
    }
}
