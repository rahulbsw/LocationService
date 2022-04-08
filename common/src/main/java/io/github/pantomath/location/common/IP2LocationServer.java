package io.github.pantomath.location.common;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Log4j2
public class IP2LocationServer {
    private final Server server;

    public <S extends BindableService> IP2LocationServer(ServerBuilder<?> serverBuilder, S... services) throws URISyntaxException {
        Arrays.stream(services).forEach(s -> serverBuilder.addService(s));
        this.server = serverBuilder.build();
    }

    public <S extends BindableService> IP2LocationServer(int port, S... services) throws URISyntaxException {
        this(ServerBuilder.forPort(port), services);
    }

    /**
     * Start serving requests.
     */
    public void start() throws IOException {
        server.start();
        log.info("Server started, listening on {}", server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    IP2LocationServer.this.stop();
                } catch (InterruptedException e) {
                    log.error("Server shutting down", e);
                    e.printStackTrace(System.err);
                }
                log.info("*** server shut down");
            }
        });
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


}

