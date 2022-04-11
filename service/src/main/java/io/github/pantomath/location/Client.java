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
package io.github.pantomath.location;

import io.github.pantomath.location.common.CityResponses;
import io.github.pantomath.location.common.IP2LookupClient;

import java.util.Objects;

/**
 * <p>Client class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class Client {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     * @throws java.lang.Exception if any.
     */
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
