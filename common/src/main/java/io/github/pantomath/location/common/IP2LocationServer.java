/**
 * The MIT License
 * Copyright © 2022 Project Location Service using GRPC and IP lookup
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.pantomath.location.common;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * <p>IP2LocationServer class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
@Log4j2
public class IP2LocationServer {
    private final Server server;

    /**
     * <p>Constructor for IP2LocationServer.</p>
     *
     * @param serverBuilder a {@link io.grpc.ServerBuilder} object
     * @param services a S object
     * @param <S> a S class
     * @throws java.net.URISyntaxException if any.
     */
    public <S extends BindableService> IP2LocationServer(ServerBuilder<?> serverBuilder, S... services) throws URISyntaxException {
        Arrays.stream(services).forEach(s -> serverBuilder.addService(s));
        this.server = serverBuilder.build();
    }

    /**
     * <p>Constructor for IP2LocationServer.</p>
     *
     * @param port a int
     * @param services a S object
     * @param <S> a S class
     * @throws java.net.URISyntaxException if any.
     */
    public <S extends BindableService> IP2LocationServer(int port, S... services) throws URISyntaxException {
        this(ServerBuilder.forPort(port), services);
    }

    /**
     * Start serving requests.
     *
     * @throws java.io.IOException if any.
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
     *
     * @throws java.lang.InterruptedException if any.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     *
     * @throws java.lang.InterruptedException if any.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


}

