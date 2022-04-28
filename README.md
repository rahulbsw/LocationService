# IP To Geo Location Service
GRPC based GEOIP lookup service

# Download below Databases 
  ## Maxmind IP 
     geoip2-city db https://dev.maxmind.com/geoip/geolite2-free-geolocation-data
  ## IP2Locations
     db11 https://www.ip2location.com/database/db11-ip-country-region-city-latitude-longitude-zipcode-timezone


#Modules
## Common
  This module protobuf rpc spec and will auto generate pojo and service code
## Service 
  this wil be used to start lookup server and sample client example
## sparksql-protobuf
   Helper module to get schema from protobuf java object and convert into spark `Row` object
## spark 
  Provide UDF for ip2Geolocation
## flink
Provide UDF for ip2Geolocation

# How to build
```shell
mvn clean install
```

#How to run
## Start Server 
```shell
java io.github.pantomath.location.Application -DPORT=8080 \
                    -DMAXMIND_CITY_DB_PATH=<db path> \
                    -DIP2LOCATION_CITY_DB_PATH=<db path> 
                    
```

Other db types path optional
*. MAXMIND_ISP_DB_PATH
*. MAXMIND_ASN_DB_PATH
*. IP2LOCATION_ISP_DB_PATH
*. IP2LOCATION_DB_PATH