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

import io.findify.flinkpb.FlinkProtobuf;
import io.github.pantomath.location.common.CityResponse;
import io.github.pantomath.location.common.CountryResponse;
import io.github.pantomath.location.common.LocationResponse;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class Schemas {
    public class LocationResponseSchema implements DeserializationSchema<LocationResponse>, SerializationSchema<LocationResponse> {

        @Override
        public LocationResponse deserialize(byte[] bytes) throws IOException {
            return LocationResponse.parseFrom(bytes);
        }

        @Override
        public byte[] serialize(LocationResponse response) {
            return response.toByteArray();
        }

        @Override
        public TypeInformation<LocationResponse> getProducedType() {
            return FlinkProtobuf.generateJava(LocationResponse.class, LocationResponse.getDefaultInstance());
        }

        // Method to decide whether the element signals the end of the stream.
        // If true is returned the element won't be emitted.
        @Override
        public boolean isEndOfStream(LocationResponse response) {
            return false;
        }
    }

    public class CityResponseSchema implements DeserializationSchema<CityResponse>, SerializationSchema<CityResponse> {

        @Override
        public CityResponse deserialize(byte[] bytes) throws IOException {
            return CityResponse.parseFrom(bytes);
        }

        @Override
        public byte[] serialize(CityResponse response) {
            return response.toByteArray();
        }

        @Override
        public TypeInformation<CityResponse> getProducedType() {
            return FlinkProtobuf.generateJava(CityResponse.class, CityResponse.getDefaultInstance());
        }

        // Method to decide whether the element signals the end of the stream.
        // If true is returned the element won't be emitted.
        @Override
        public boolean isEndOfStream(CityResponse response) {
            return false;
        }
    }

    public class CountryResponseSchema implements DeserializationSchema<CountryResponse>, SerializationSchema<CountryResponse> {

        @Override
        public CountryResponse deserialize(byte[] bytes) throws IOException {
            return CountryResponse.parseFrom(bytes);
        }

        @Override
        public byte[] serialize(CountryResponse response) {
            return response.toByteArray();
        }

        @Override
        public TypeInformation<CountryResponse> getProducedType() {
            return FlinkProtobuf.generateJava(CountryResponse.class, CountryResponse.getDefaultInstance());
        }

        // Method to decide whether the element signals the end of the stream.
        // If true is returned the element won't be emitted.
        @Override
        public boolean isEndOfStream(CountryResponse response) {
            return false;
        }
    }


}
