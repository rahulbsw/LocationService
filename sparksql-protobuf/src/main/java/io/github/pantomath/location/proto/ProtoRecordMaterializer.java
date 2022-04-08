package io.github.pantomath.location.proto;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;

/**
 * Custom record materialize that support nested parquet written by SparkSQL
 */
class ProtoRecordMaterializer<T extends MessageOrBuilder> extends RecordMaterializer<T> {

    private final ProtoRecordConverter<T> root;

    public ProtoRecordMaterializer(MessageType requestedSchema, Class<? extends Message> protobufClass) {
        this.root = new ProtoRecordConverter<T>(protobufClass, requestedSchema);
    }

    @Override
    public T getCurrentRecord() {
        return root.getCurrentRecord();
    }

    @Override
    public GroupConverter getRootConverter() {
        return root;
    }
}