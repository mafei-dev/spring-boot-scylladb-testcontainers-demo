package com.example.springbootscylladbtestcontainersdemo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.Metadata;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ContextConfiguration(initializers = TestcontainersConfiguration.class)
@SpringBootTest
@Testcontainers
class SpringBootScylladbTestcontainersDemoApplicationTests {

    @Autowired
    private CqlSession cqlSession;

    @Test
    void test() {
        KeyspaceMetadata keyspaceMetadata = assertDoesNotThrow(() -> cqlSession.getMetadata()
                .getKeyspace("demo_key_space").orElseThrow());
        TableMetadata usersTable = assertDoesNotThrow(() -> keyspaceMetadata.getTable("users").orElseThrow());
        SimpleStatement statement = QueryBuilder
                .insertInto(usersTable.getName().toString())
                .value("user_id", QueryBuilder.literal(Uuids.timeBased()))
                .value("email", QueryBuilder.literal("mafei.dev@gmail.com"))
                .value("name", QueryBuilder.literal("Mafei"))
                .build();
        ResultSet execute = cqlSession.execute(statement);
        assertTrue(execute.wasApplied());
    }

}
