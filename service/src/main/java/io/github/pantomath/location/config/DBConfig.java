package io.github.pantomath.location.config;

import java.net.URI;


public class DBConfig {
    public TYPE source;
    public URI CITY_DB_URI;
    public URI ISP_DB_URI;
    public URI ASN_DB_URI;
    public DBConfig(TYPE source, URI CITY_DB_URI, URI ISP_DB_URI, URI ASN_DB_URI) {
        this.source = source;
        this.CITY_DB_URI = CITY_DB_URI;
        this.ISP_DB_URI = ISP_DB_URI;
        this.ASN_DB_URI = ASN_DB_URI;
    }

    public enum TYPE {MAXMIND, IP2LOCATION}
}

