package com.example.springbootscylladbtestcontainersdemo;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.scylladb.ScyllaDBContainer;

import java.io.IOException;
import java.util.Objects;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    static ScyllaDBContainer container = new ScyllaDBContainer("scylladb/scylla:6.2")
            .withReuse(true)
            .withCommand("--smp", "2")
            .waitingFor(Wait.forListeningPort())
            .withExposedPorts( 9042,19042)
            .withCreateContainerCmdModifier(cmd -> {
                Objects.requireNonNull(cmd.getHostConfig()).withCpuCount(2L);
                cmd.withPortBindings(
                        new PortBinding(Ports.Binding.bindPort(9042), new ExposedPort(9042)),
                        new PortBinding(Ports.Binding.bindPort(19042), new ExposedPort(19042))
                );
            });

    static {
        System.setProperty("advanced.connection.advanced-shard-awareness.enabled", "true");
    }

    @Bean
    public ScyllaDBContainer scyllaContainer() {
        return container;
    }


    @Bean
    public DynamicPropertyRegistrar keyspacePropertyRegistrar(ScyllaDBContainer container) {
        try {
            container.execInContainer("cqlsh", "-e",
                    "CREATE KEYSPACE IF NOT EXISTS demo_key_space WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

            container.execInContainer("cqlsh", "-e",
                    """
                                CREATE TABLE IF NOT EXISTS demo_key_space.users (
                                user_id UUID,
                                email TEXT,
                                name TEXT,
                                PRIMARY KEY (user_id)
                            );
                            """);
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
        return (registry) -> {
            registry.add("spring.cassandra.keyspace-name", () -> "demo_key_space");
            registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
            registry.add("spring.cassandra.contact-points", () -> container.getShardAwareContactPoint().getAddress());
        };
    }

}
