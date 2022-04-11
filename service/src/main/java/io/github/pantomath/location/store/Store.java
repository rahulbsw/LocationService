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
package io.github.pantomath.location.store;

import io.github.pantomath.location.common.City;
import io.github.pantomath.location.common.Country;
import io.github.pantomath.location.common.Location;
import io.github.pantomath.location.config.DBConfig;
import io.github.pantomath.location.exception.InitializationException;
import io.github.pantomath.location.exception.LookupException;
import io.github.pantomath.location.utils.IPAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Function;

/**
 * <p>Abstract Store class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public abstract class Store {
    public final InetAddress LOCALHOST;
    protected final DBConfig config;

    /**
     * <p>Constructor for Store.</p>
     *
     * @param config a {@link io.github.pantomath.location.config.DBConfig} object
     * @throws io.github.pantomath.location.exception.InitializationException if any.
     */
    protected Store(DBConfig config) throws InitializationException {
        try {
            this.config = config;
            this.LOCALHOST = InetAddress.getLocalHost();
            load();
        } catch (UnknownHostException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link io.github.pantomath.location.config.DBConfig} object
     * @return a {@link io.github.pantomath.location.store.Store} object
     * @throws io.github.pantomath.location.exception.InitializationException if any.
     */
    public static Store create(DBConfig config) throws InitializationException {
        if (DBConfig.TYPE.MAXMIND.equals(config.source))
            return new MaxMindStore(config);
        else if (DBConfig.TYPE.IP2LOCATION.equals(config.source))
            return new IP2LocationService(config);
        else
            return null;
    }

    /**
     * <p>resolve.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @param resolver a {@link java.util.function.Function} object
     * @param <R> a R class
     * @return a R object
     */
    protected <R> R resolve(String ip, Function<IPAddress, R> resolver) {
        Optional<IPAddress> ipAddress = IPAddress.create(ip);
        if (ipAddress.isPresent() && !ipAddress.get().isPrivate())
            return resolver.apply(ipAddress.get());
        else
            return null;
    }

    /**
     * <p>load.</p>
     *
     * @throws io.github.pantomath.location.exception.InitializationException if any.
     */
    protected abstract void load() throws InitializationException;

    /**
     * <p>city.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.City} object
     * @throws io.github.pantomath.location.exception.LookupException if any.
     */
    public abstract City city(String ip) throws LookupException;

    /**
     * <p>country.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @return a {@link io.github.pantomath.location.common.Country} object
     * @throws io.github.pantomath.location.exception.LookupException if any.
     */
    public abstract Country country(String ip) throws LookupException;

    /**
     * <p>location.</p>
     *
     * @param ip a {@link java.lang.String} object
     * @param includeISP a boolean
     * @param includeDomain a boolean
     * @return a {@link io.github.pantomath.location.common.Location} object
     * @throws io.github.pantomath.location.exception.LookupException if any.
     */
    public abstract Location location(String ip, boolean includeISP, boolean includeDomain) throws LookupException;

}
