package com.example.springbootscylladbtestcontainersdemo;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.scylladb.ScyllaDBContainer;

import java.io.IOException;

class TestcontainersConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static ScyllaDBContainer scyllaDB = new ScyllaDBContainer("scylladb/scylla:6.2")
            .withReuse(true)
            .waitingFor(Wait.forListeningPort())
            .withExposedPorts(19042)
            .withCreateContainerCmdModifier(cmd -> {
                cmd.getHostConfig().withPortBindings(
                        new PortBinding(Ports.Binding.bindPort(29042), new ExposedPort(19042))
                );
            });


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        scyllaDB.start();
        try {
            scyllaDB.execInContainer("cqlsh", "-e",
                    "CREATE KEYSPACE IF NOT EXISTS demo_key_space WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

            scyllaDB.execInContainer("cqlsh", "-e",
                    """
                                CREATE TABLE IF NOT EXISTS demo_key_space.users (
                                user_id UUID,
                                email TEXT,
                                name TEXT,
                                PRIMARY KEY (user_id)
                            );
                            """);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        TestPropertyValues.of(
                "spring.cassandra.contact-points=" + scyllaDB.getHost(),
                "spring.cassandra.port=" + scyllaDB.getFirstMappedPort(),
                "spring.cassandra.local-datacenter=datacenter1",
                "spring.cassandra.keyspace-name=demo_key_space"
        ).applyTo(applicationContext.getEnvironment());

    }
}
