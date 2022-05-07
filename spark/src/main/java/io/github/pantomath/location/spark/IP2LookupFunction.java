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
package io.github.pantomath.location.spark;

import com.google.common.base.Preconditions;
import io.github.pantomath.location.common.*;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;

/**
 * <p>IP2LookupFunction class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class IP2LookupFunction implements Serializable {

    private static ServerInfo serverInfo;
    // ROW<
    // continent STRING,
    // country STRING,
    // country_iso_code STRING,
    // latitude DOUBLE,
    // longitude DOUBLE,
    // region STRING,
    // city STRING,
    // zipcode STRING,
    // timezone STRING,
    // ipaddress STRING,
    // isp STRING,
    // organization STRING,
    // domain STRING>
    private static StructType locationSchema = DataTypes.createStructType(new StructField[] {
            DataTypes.createStructField("continent",  DataTypes.StringType, true),
            DataTypes.createStructField("country", DataTypes.StringType, true),
            DataTypes.createStructField("country_iso_code", DataTypes.StringType, true),
            DataTypes.createStructField("latitude", DataTypes.DoubleType, true),
            DataTypes.createStructField("longitude", DataTypes.DoubleType, true),
            DataTypes.createStructField("region",  DataTypes.StringType, true),
            DataTypes.createStructField("city",  DataTypes.StringType, true),
            DataTypes.createStructField("zipcode",  DataTypes.StringType, true),
            DataTypes.createStructField("timezone",  DataTypes.StringType, true),
            DataTypes.createStructField("ipaddress",  DataTypes.StringType, true),
            DataTypes.createStructField("isp",  DataTypes.StringType, true),
            DataTypes.createStructField("organization",  DataTypes.StringType, true),
            DataTypes.createStructField("domain",  DataTypes.StringType, true)
    });
    /** Constant <code>getLocation</code> */
    public static UserDefinedFunction getLocation = functions.udf((String ip) -> {
        LocationResponse locationResponse=location(ip);
        City city=locationResponse.getLocation().getCity();
        ISP isp=locationResponse.getLocation().getIsp();
        return RowFactory.create(
                city.getContinent(),
                city.getCountry(),
                city.getCountryIsoCode(),
                (city.getLatitude()!=0)?city.getLatitude():null,
                (city.getLongitude()!=0)?city.getLongitude():null,
                city.getRegion(),
                city.getCity(),
                city.getZipcode(),
                city.getTimezone(),
                city.getIpaddress(),
                isp.getIsp(),
                isp.getOrganization(),
                locationResponse.getLocation().getDomain().getDomain()
        );
    }, locationSchema);

    private static StructType citySchema = DataTypes.createStructType(new StructField[] {
            DataTypes.createStructField("continent",  DataTypes.StringType, true),
            DataTypes.createStructField("country", DataTypes.StringType, true),
            DataTypes.createStructField("country_iso_code", DataTypes.StringType, true),
            DataTypes.createStructField("latitude", DataTypes.DoubleType, true),
            DataTypes.createStructField("longitude", DataTypes.DoubleType, true),
            DataTypes.createStructField("region",  DataTypes.StringType, true),
            DataTypes.createStructField("city",  DataTypes.StringType, true),
            DataTypes.createStructField("zipcode",  DataTypes.StringType, true),
            DataTypes.createStructField("timezone",  DataTypes.StringType, true),
            DataTypes.createStructField("ipaddress",  DataTypes.StringType, true),
      });

    public static UserDefinedFunction getCity = functions.udf((String ip) -> {
        CityResponse locationResponse=city(ip);
        City city=locationResponse.getCity();
        return RowFactory.create(
                city.getContinent(),
                city.getCountry(),
                city.getCountryIsoCode(),
                (city.getLatitude()!=0)?city.getLatitude():null,
                (city.getLongitude()!=0)?city.getLongitude():null,
                city.getRegion(),
                city.getCity(),
                city.getZipcode(),
                city.getTimezone(),
                city.getIpaddress()
        );
    }, citySchema);
    /**
     * <p>init.</p>
     *
     * @param hostname a {@link java.lang.String} object
     * @param port a int
     */
    public static void init(String hostname, int port) {
        serverInfo = new ServerInfo(hostname, port);
    }

    private static LocationResponse location(String ip) {
        LocationResponse locationResponse = IP2LookupClient.getOrCreate(serverInfo.hostname, serverInfo.port).location(ip);
        return locationResponse;
    }

    private static CityResponse city(String ip) {
        return IP2LookupClient.getOrCreate(serverInfo.hostname, serverInfo.port).city(ip);
    }

    private static CountryResponse country(String ip) {
        return IP2LookupClient.getOrCreate(serverInfo.hostname, serverInfo.port).country(ip);
    }

    public static class ServerInfo implements Serializable {
        protected String hostname;
        protected int port = 8080;

        public ServerInfo(String hostname, int port) {
            Preconditions.checkNotNull(hostname, "Hostname can't be null");
            Preconditions.checkNotNull(port, "Port can't be null");
            this.hostname = hostname;
            this.port = port;
        }
    }


//    public static <T extends GeneratedMessageV3> StructType schema(T pojo){
//        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : pojo.getAllFields().entrySet()) {
//            entry.getKey().getJavaType().equals(Descriptors.FieldDescriptor.JavaType.BOOLEAN)
//        }
//    }
}
