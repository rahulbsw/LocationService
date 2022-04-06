package io.github.pantomath.location.store;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import io.github.pantomath.location.exception.LookupException;
import io.github.pantomath.location.common.City;
import io.github.pantomath.location.common.Country;
import io.github.pantomath.location.common.Location;
import io.github.pantomath.location.config.DBConfig;
import io.github.pantomath.location.exception.InitializationException;

import java.io.File;
import java.io.IOException;

public class IP2LocationService extends Store {
    IP2Location reader;

    protected IP2LocationService(DBConfig config) throws InitializationException {
        super(config);
    }

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

    @Override
    public  City city(String ip) throws LookupException {
    return resolve(ip,(ipAddress -> {
            try {
                IPResult response=reader.IPQuery(ipAddress.getIPAddress());
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

    @Override
    public  Country country(String ip) throws LookupException{
    return resolve(ip,(ipAddress -> {
            try {
                IPResult response=reader.IPQuery(ipAddress.getIPAddress());
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

    @Override
    public Location location(String ip, boolean includeISP, boolean includeDomain) throws LookupException {
        return resolve(ip,(ipAddress -> {
            try {
                IPResult response=reader.IPQuery(ipAddress.getIPAddress());
                City city= City.newBuilder()
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
