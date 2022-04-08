# IP 2 Location Service
GRPC based GEOIP lookup service

#Modules
## Common
  This module protobuf rpc spec and will auto generate pojo and service code
## Service 
  this wil be used to start lookup server and sample client example
## sparksql-protobuf
   Helper module to get schema from protobuf java object and convert into spark `Row` object
## spark 
  Provide UDF for ip3location

#How to build
```shell
mvn clean install
```

