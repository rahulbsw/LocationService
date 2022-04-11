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

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import io.github.pantomath.location.common.City;
import io.github.pantomath.location.common.Country;
import io.github.pantomath.location.common.Location;
import io.github.pantomath.location.config.DBConfig;
import io.github.pantomath.location.exception.InitializationException;
import io.github.pantomath.location.exception.LookupException;

import java.io.File;
import java.io.IOException;

/**
 * <p>IP2LocationService class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class IP2LocationService extends Store {
    IP2Location reader;

    /**
     * <p>Constructor for IP2LocationService.</p>
     *
     * @param config a {@link io.github.pantomath.location.config.DBConfig} object
     * @throws io.github.pantomath.location.exception.InitializationException if any.
     */
    protected IP2LocationService(DBConfig config) throws InitializationException {
        super(config);
    }

    /** {@inheritDoc} */
    @Override
    public void load() throws InitializationException {
        File database = new File(config.CITY_DB_URI);
        try {
            reader = new IP2Location();
            reader.Open(database.getAbsolutePath(), true);
        } catch (IOException e) {
            throw new InitializationException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public City city(String ip) throws LookupException {
        return resolve(ip, (ipAddress -> {
            try {
                IPResult response = reader.IPQuery(ipAddress.getIPAddress());
                return City.newBuilder()
                        .setCity(response.getCity())
                        .setCountryIsoCode(response.getCountryShort())
                        .setCountry(response.getCountryLong())
                        .setContinent(response.getAreaCode())
                        .setRegion(response.getRegion())
                        .setZipcode(response.getZipCode())
                        .setLatitude(response.getLatitude())
                        .setLongitude(response.getLongitude())
                        .setTimezone(response.getTimeZone())
                        .build();
            } catch (IOException e) {
                return null;
            }
        }));

    }

    /** {@inheritDoc} */
    @Override
    public Country country(String ip) throws LookupException {
        return resolve(ip, (ipAddress -> {
            try {
                IPResult response = reader.IPQuery(ipAddress.getIPAddress());
                return Country.newBuilder()
                        .setCountryIsoCode(response.getCountryShort())
                        .setCountry(response.getCountryLong())
                        .setContinent(response.getAreaCode())
                        .build();
            } catch (IOException e) {
                return Country.getDefaultInstance();
            }
        }));
    }

    /** {@inheritDoc} */
    @Override
    public Location location(String ip, boolean includeISP, boolean includeDomain) throws LookupException {
        return resolve(ip, (ipAddress -> {
            try {
                IPResult response = reader.IPQuery(ipAddress.getIPAddress());
                City city = City.newBuilder()
                        .setCity(response.getCity())
                        .setCountryIsoCode(response.getCountryShort())
                        .setCountry(response.getCountryLong())
                        .setContinent(response.getAreaCode())
                        .setRegion(response.getRegion())
                        .setZipcode(response.getZipCode())
                        .setLatitude(response.getLatitude())
                        .setLongitude(response.getLongitude())
                        .setTimezone(response.getTimeZone())
                        .build();
                return Location.newBuilder().setCity(city).build();
            } catch (IOException e) {
                return Location.getDefaultInstance();
            }
        }));
    }


}
