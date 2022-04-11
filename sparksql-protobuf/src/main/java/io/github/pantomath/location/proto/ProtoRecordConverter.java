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
package io.github.pantomath.location.proto;


import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import org.apache.parquet.proto.ProtoSchemaConverter;
import org.apache.parquet.schema.MessageType;

/**
 * Converts data content of root message from Protocol Buffer message to parquet message.
 * It delegates conversion of inner fields to {@link ProtoMessageConverter} class using inheritance.
 * Schema is converted in {@link ProtoSchemaConverter} class.
 */
public class ProtoRecordConverter<T extends MessageOrBuilder> extends ProtoMessageConverter {

    private final Message.Builder reusedBuilder;
    private boolean buildBefore;

    public ProtoRecordConverter(Class<? extends Message> protoclass, MessageType parquetSchema) {
        super(new SkipParentValueContainer(), protoclass, parquetSchema);
        reusedBuilder = getBuilder();
    }


    public ProtoRecordConverter(Message.Builder builder, MessageType parquetSchema) {
        super(new SkipParentValueContainer(), builder, parquetSchema);
        reusedBuilder = getBuilder();
    }

    @Override
    public void start() {
        reusedBuilder.clear();
        super.start();
    }

    @Override
    public void end() {
        // do nothing, dont call ParentValueContainer at top level.
    }

    public T getCurrentRecord() {
        if (buildBefore) {
            return (T) this.reusedBuilder.build();
        } else {
            return (T) this.reusedBuilder;
        }
    }

    /***
     * if buildBefore is true, Protocol Buffer builder is build to message before returning record.
     */
    public void setBuildBefore(boolean buildBefore) {
        this.buildBefore = buildBefore;
    }

    /**
     * We dont need to write message value at top level.
     */
    private static class SkipParentValueContainer extends ParentValueContainer {
        @Override
        public void add(Object a) {
            throw new RuntimeException("Should never happen");
        }
    }
}