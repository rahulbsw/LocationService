package io.github.pantomath.location;

import io.github.pantomath.location.common.IP2LocationServer;
import io.github.pantomath.location.services.FinderService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Application {

    public static void main(String[] args) throws Exception {
        // Create a new server to listen on port 8080
        IP2LocationServer server = new IP2LocationServer(8080, new FinderService());
        server.start();
        server.blockUntilShutdown();
    }
}

