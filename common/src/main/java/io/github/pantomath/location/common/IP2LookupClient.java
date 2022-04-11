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

import com.google.common.base.Strings;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>IP2LookupClient class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class IP2LookupClient {
    static IP2LookupClient INSTANCE;
    final ManagedChannel channel;
    FindGrpc.FindBlockingStub stub;

    /**
     * <p>Constructor for IP2LookupClient.</p>
     *
     * @param hostname a {@link java.lang.String} object
     * @param port a int
     * @param callCredentials a {@link io.grpc.CallCredentials} object
     */
    public IP2LookupClient(String hostname, int port, CallCredentials callCredentials) {
        this.channel = NettyChannelBuilder.forTarget(hostname + ":" + port).usePlaintext().build();
        if (Objects.nonNull(callCredentials))
            this.stub = FindGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
        else
            this.stub = FindGrpc.newBlockingStub(channel);
    }

    /**
     * <p>Constructor for IP2LookupClient.</p>
     *
     * @param hostname a {@link java.lang.String} object
     * @param port a int
     */
    public IP2LookupClient(String hostname, int port) {
        this(hostname, port, null);
    }

    /**
     * <p>Constructor for IP2LookupClient.</p>
     *
     * @param hostname a {@link java.lang.String} object
     */
    public IP2LookupClient(String hostname) {
        this(hostname, 8080);
    }

    /**
     * <p>getOrCreate.</p>
     *
     * @param hostname a {@link java.lang.String} object
     * @param port a int
     * @return a {@link io.github.pantomath.location.common.IP2LookupClient} object
     */
    public static IP2LookupClient getOrCreate(String hostname, int port) {
        if (Objects.isNull(INSTANCE) || INSTANCE.channel.isShutdown() || INSTANCE.channel.isTerminated()) {
            INSTANCE = new IP2LookupClient(hostname, port);
        }
        return INSTANCE;
    }

    /**
     * <p>getOrCreate.</p>
     *
     * @param hostname a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.IP2LookupClient} object
     */
    public static IP2LookupClient getOrCreate(String hostname) {
        return getOrCreate(hostname, 8080);
    }

    /**
     * <p>location.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.LocationResponse} object
     */
    public LocationResponse location(String ip) {
        if (Strings.isNullOrEmpty(ip))
            return LocationResponse.getDefaultInstance();

        return stub.location(IPAddressExt.newBuilder().setIp(ip).build());
    }

    /**
     * <p>city.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.CityResponse} object
     */
    public CityResponse city(String ip) {
        if (Strings.isNullOrEmpty(ip))
            return CityResponse.getDefaultInstance();
        return stub.city(IPAddress.newBuilder().setIp(ip).build());
    }

    /**
     * <p>country.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.CountryResponse} object
     */
    public CountryResponse country(String ip) {
        if (Strings.isNullOrEmpty(ip))
            return CountryResponse.getDefaultInstance();
        return stub.country(IPAddress.newBuilder().setIp(ip).build());
    }

    /**
     * <p>locations.</p>
     *
     * @param ips a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.LocationResponses} object
     */
    public LocationResponses locations(String... ips) {
        if (Objects.isNull(ips) && ips.length < 0)
            return LocationResponses.getDefaultInstance();
        return stub.locations(IPAddressesExt.newBuilder().addAllIp(Arrays.asList(ips)).build());
    }

    /**
     * <p>cities.</p>
     *
     * @param ips a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.CityResponses} object
     */
    public CityResponses cities(String... ips) {
        if (Objects.isNull(ips) && ips.length < 0)
            return CityResponses.getDefaultInstance();
        return stub.cities(IPAddresses.newBuilder().addAllIp(Arrays.asList(ips)).build());
    }

    /**
     * <p>countries.</p>
     *
     * @param ips a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.CountryResponses} object
     */
    public CountryResponses countries(String... ips) {
        if (Objects.isNull(ips) && ips.length < 0)
            return CountryResponses.getDefaultInstance();
        return stub.countries(IPAddresses.newBuilder().addAllIp(Arrays.asList(ips)).build());
    }

    /**
     * <p>shutdown.</p>
     *
     * @throws java.lang.InterruptedException if any.
     */
    public void shutdown() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}
