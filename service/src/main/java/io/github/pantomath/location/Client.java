package io.github.pantomath.location;

import io.github.pantomath.location.common.CityResponses;
import io.github.pantomath.location.common.IP2LookupClient;

import java.util.Objects;

public class Client {
    public static void main(String[] args) throws Exception {
        // Channel is the abstraction to connect to a service endpoint
        // Let's use plaintext communication because we don't have certs
        IP2LookupClient ip2Lookup = null;
        try {
            ip2Lookup = new IP2LookupClient("localhost", 8080);
            // Finally, make the call using the stub
            CityResponses response = ip2Lookup.cities("128.107.241.164", "98.143.133.154", "107.6.171.130", "45.33.66.232", "69.175.97.170", "173.255.213.43");

            System.out.println(response.getCitiesList());
        } finally {
            if (Objects.nonNull(ip2Lookup))
                ip2Lookup.shutdown();
        }

    }
}
