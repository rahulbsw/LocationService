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
import io.findify.flinkpb.FlinkProtobuf;
import io.github.pantomath.location.common.CityResponse;
import io.github.pantomath.location.common.CountryResponse;
import io.github.pantomath.location.common.IP2LookupClient;
import io.github.pantomath.location.common.LocationResponse;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.types.Row;

import java.io.Serializable;

@FunctionHint(output = @DataTypeHint(bridgedTo = LocationResponse.class))
public  class IP2GeoLocationTableFunction extends TableFunction<LocationResponse> {
    private static ServerInfo serverInfo;

    public IP2GeoLocationTableFunction(String hostname,int port) {
        super();
        serverInfo=new ServerInfo(hostname,port);
    }

    public IP2GeoLocationTableFunction(String hostname) {
        this(hostname,8080);
    }

//    @Override
//    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
//
//        TypeInference.newBuilder().typedArguments()
//         typeFactory.createRawDataType(FlinkProtobuf.generateJava(LocationResponse.class, LocationResponse.getDefaultInstance()))
//         return typeFactory;
//    }

    @Override
    public TypeInformation<LocationResponse> getResultType() {
        return FlinkProtobuf.generateJava(LocationResponse.class, LocationResponse.getDefaultInstance());
    }

    /**
     * <p>init.</p>
     *
     * @param hostname a {@link java.lang.String} object
     * @param port a int
     */
    public static void init(String hostname, int port) {
        serverInfo = new ServerInfo(hostname, port);
    }

    public void eval(String str) {
        collect(location(str));
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



}
