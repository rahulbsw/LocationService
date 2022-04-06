package io.github.pantomath.location.store;

import io.github.pantomath.location.config.DBConfig;
import io.github.pantomath.location.exception.LookupException;
import io.github.pantomath.location.utils.IPAddress;
import io.github.pantomath.location.common.City;
import io.github.pantomath.location.common.Country;
import io.github.pantomath.location.common.Location;
import io.github.pantomath.location.exception.InitializationException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Function;

public abstract class Store {
    public final InetAddress LOCALHOST;
    protected final DBConfig config;

    protected Store(DBConfig config) throws InitializationException{
        try {
            this.config=config;
            this.LOCALHOST= InetAddress.getLocalHost();
            load();
        } catch (UnknownHostException e) {
            throw new InitializationException(e);
        }
    }

    protected <R> R resolve(String ip, Function<IPAddress,R> resolver){
         Optional<IPAddress> ipAddress=IPAddress.create(ip);
         if(ipAddress.isPresent() && !ipAddress.get().isPrivate())
             return resolver.apply(ipAddress.get());
         else
             return null;
    }


    protected abstract void load() throws InitializationException;

    public abstract City city(String ip) throws LookupException;
    public abstract Country country(String ip) throws LookupException;
    public abstract Location location(String ip,boolean includeISP, boolean includeDomain) throws LookupException ;

    public static Store create(DBConfig config) throws InitializationException {
        if(DBConfig.TYPE.MAXMIND.equals(config.source))
            return new MaxMindStore(config);
        else if(DBConfig.TYPE.IP2LOCATION.equals(config.source))
            return new IP2LocationService(config);
        else
            return null;
    }

}
