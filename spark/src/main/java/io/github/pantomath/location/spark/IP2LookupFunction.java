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
import io.github.pantomath.location.common.CityResponse;
import io.github.pantomath.location.common.CountryResponse;
import io.github.pantomath.location.common.IP2LookupClient;
import io.github.pantomath.location.common.LocationResponse;
import io.github.pantomath.location.proto.spark.sql.ProtoRDDConversions;
import io.github.pantomath.location.proto.spark.sql.ProtoReflection;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.functions;
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
    private static StructType locationSchema = (StructType) ProtoReflection.schemaFor(LocationResponse.class).dataType();
    /** Constant <code>getLocation</code> */
    public static UserDefinedFunction getLocation = functions.udf((String ip) -> ProtoRDDConversions.messageToRow(location(ip)), locationSchema);
    private static StructType citySchema = (StructType) ProtoReflection.schemaFor(CityResponse.class).dataType();
    /** Constant <code>getCity</code> */
    public static UserDefinedFunction getCity = functions.udf((String ip) -> ProtoRDDConversions.messageToRow(city(ip)), citySchema);
    private static StructType countrySchema = (StructType) ProtoReflection.schemaFor(CountryResponse.class).dataType();
    /** Constant <code>getCountry</code> */
    public static UserDefinedFunction getCountry = functions.udf((String ip) -> ProtoRDDConversions.messageToRow(country(ip)), countrySchema);
    //val schema = ProtoReflection.schemaFor(clazz).dataType.asInstanceOf[StructType]
    //      val rowRDD = rdd.map(ProtoRDDConversions.messageToRow)
    private static ExpressionEncoder.Serializer<LocationResponse> locationSerializer = ((ExpressionEncoder) Encoders.javaSerialization(LocationResponse.class)).createSerializer();
    private static ExpressionEncoder.Serializer<CityResponse> citySerializer = ((ExpressionEncoder) Encoders.javaSerialization(CityResponse.class)).createSerializer();
    private static ExpressionEncoder.Serializer<CountryResponse> countrySerializer = ((ExpressionEncoder) Encoders.javaSerialization(CountryResponse.class)).createSerializer();

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
