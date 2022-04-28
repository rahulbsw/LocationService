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
package io.github.pantomath.location.flink;


import com.google.common.base.Preconditions;
import io.github.pantomath.location.common.*;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

import java.io.Serializable;


public  class IP2GeoLocationFunction extends ScalarFunction {
    private static ServerInfo serverInfo;

    public IP2GeoLocationFunction(String hostname, int port) {
        super();
        serverInfo=new ServerInfo(hostname,port);
    }

    public IP2GeoLocationFunction(String hostname) {
        this(hostname,8080);
    }

    /**
     * <p>init.</p>
     *
     * @param hostname a {@link String} object
     * @param port a int
     */
    public static void init(String hostname, int port) {
        serverInfo = new ServerInfo(hostname, port);
    }

    public  @DataTypeHint("ROW<continent STRING,country STRING,country_iso_code STRING, latitude DOUBLE, longitude DOUBLE,region STRING, city STRING, zipcode STRING, timezone STRING, ipaddress STRING,isp STRING,organization STRING,domain STRING>") Row eval(String str) {
        LocationResponse locationResponse=location(str);
        City city=locationResponse.getLocation().getCity();
        ISP isp=locationResponse.getLocation().getIsp();
        Row row=Row.of(city.getContinent(),
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
        return row;
    }

    private static LocationResponse location(String ip) {
        LocationResponse locationResponse = IP2LookupClient.getOrCreate(serverInfo.hostname, serverInfo.port).location(ip);
        return locationResponse;
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



}
