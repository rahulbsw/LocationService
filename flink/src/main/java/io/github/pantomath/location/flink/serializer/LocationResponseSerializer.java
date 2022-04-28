package io.github.pantomath.location.flink.serializer;

import io.findify.flinkpb.Codec;
import io.findify.flinkpb.ProtobufSerializer;
import io.github.pantomath.location.common.LocationResponse;

public class LocationResponseSerializer extends ProtobufSerializer<LocationResponse> {

    public LocationResponseSerializer() {
        super(new Codec.JavaCodec<LocationResponse>(LocationResponse.getDefaultInstance(), LocationResponse.class));
    }
}
