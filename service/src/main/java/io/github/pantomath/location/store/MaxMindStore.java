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

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.model.DomainResponse;
import com.maxmind.geoip2.model.IspResponse;
import io.github.pantomath.location.common.City;
import io.github.pantomath.location.common.Country;
import io.github.pantomath.location.common.ISP;
import io.github.pantomath.location.common.Location;
import io.github.pantomath.location.config.DBConfig;
import io.github.pantomath.location.exception.InitializationException;
import io.github.pantomath.location.exception.LookupException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * <p>MaxMindStore class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class MaxMindStore extends Store {
    final boolean isCityReaderReady;
    final boolean isISPReaderReady;
    final boolean isASNReaderReady;
    DatabaseReader cityReader;
    DatabaseReader ispReader;
    DatabaseReader asnReader;

    /**
     * <p>Constructor for MaxMindStore.</p>
     *
     * @param config a {@link io.github.pantomath.location.config.DBConfig} object
     * @throws io.github.pantomath.location.exception.InitializationException if any.
     */
    protected MaxMindStore(DBConfig config) throws InitializationException {
        super(config);
        this.isCityReaderReady = Objects.nonNull(this.cityReader);
        this.isISPReaderReady = Objects.nonNull(this.ispReader);
        this.isASNReaderReady = Objects.nonNull(this.asnReader);
    }

    /** {@inheritDoc} */
    @Override
    public void load() throws InitializationException {
        try {
            if (Objects.nonNull(this.config.CITY_DB_URI)) {
                File cityDB = new File(this.config.CITY_DB_URI);
                this.cityReader = new DatabaseReader.Builder(cityDB).withCache(new CHMCache()).build();
            }

            if (Objects.nonNull(this.config.ISP_DB_URI)) {
                File ispDB = new File(this.config.ISP_DB_URI);
                this.ispReader = new DatabaseReader.Builder(ispDB).withCache(new CHMCache()).build();
            }

            if (Objects.nonNull(this.config.ASN_DB_URI)) {
                File asnDB = new File(this.config.ASN_DB_URI);
                this.asnReader = new DatabaseReader.Builder(asnDB).withCache(new CHMCache()).build();
            }
        } catch (IOException e) {
            throw new InitializationException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public City city(String ip) throws LookupException {
        if (!isCityReaderReady)
            throw new LookupException("City DB not enabled");
        return resolve(ip, (ipAddress -> {
            try {
                CityResponse response = cityReader.city(ipAddress.getInetAddress());
                return City.newBuilder()
                        .setCity(response.getCity().getName())
                        .setCountryIsoCode(response.getCountry().getIsoCode())
                        .setCountry(response.getCountry().getName())
                        .setContinent(response.getContinent().getCode())
                        .setRegion(response.getMostSpecificSubdivision().getName())
                        .setZipcode(response.getPostal().getCode())
                        .setLatitude(response.getLocation().getLatitude())
                        .setLongitude(response.getLocation().getLongitude())
                        .setTimezone(response.getLocation().getTimeZone())
                        .build();
            } catch (IOException | GeoIp2Exception | NullPointerException e) {
                return City.getDefaultInstance();
            }
        }));

    }

    /** {@inheritDoc} */
    @Override
    public Country country(String ip) throws LookupException {
        if (!isCityReaderReady)
            throw new LookupException("City DB not enabled");
        return resolve(ip, (ipAddress -> {
            try {
                CountryResponse response = cityReader.country(ipAddress.getInetAddress());
                return Country.newBuilder()
                        .setCountryIsoCode(response.getCountry().getIsoCode())
                        .setCountry(response.getCountry().getName())
                        .setContinent(response.getContinent().getCode()).build();
            } catch (IOException | GeoIp2Exception e) {
                return Country.getDefaultInstance();
            }
        }));
    }

    /** {@inheritDoc} */
    @Override
    public Location location(String ip, boolean includeISP, boolean includeDomain) throws LookupException {
        if (!isCityReaderReady)
            throw new LookupException("City DB not enabled");
        if (!isISPReaderReady && includeISP)
            throw new LookupException("ISP DB not enabled");

        return resolve(ip, (ipAddress -> {
            try {
                Location.Builder builder = Location.newBuilder();
                builder.setCity(city(ip));
                if (includeISP) {
                    IspResponse response = cityReader.isp(ipAddress.getInetAddress());
                    builder.setIsp(ISP.newBuilder().setIsp(response.getIsp()).setOrganization(response.getOrganization()).build());
                }
                if (includeDomain) {
                    DomainResponse response = cityReader.domain(ipAddress.getInetAddress());
                    //Domain.newBuilder()response.getDomain()
                }
                return builder.build();
            } catch (IOException | GeoIp2Exception | LookupException e) {
                return Location.getDefaultInstance();
            }
        }));
    }

}
