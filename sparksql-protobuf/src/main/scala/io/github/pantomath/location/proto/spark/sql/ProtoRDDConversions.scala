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
package io.github.pantomath.location.proto.spark.sql

import com.google.protobuf.Descriptors.FieldDescriptor.JavaType._
import com.google.protobuf.Descriptors.{EnumValueDescriptor, FieldDescriptor}
import com.google.protobuf.{AbstractMessage, ByteString}
import org.apache.spark.sql.Row

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

/**
 * Support for converting protobuf message object to row
 */
object ProtoRDDConversions {
  def messageToRow[A <: AbstractMessage](message: A): Row = {

    def toRowData(fd: FieldDescriptor, obj: AnyRef) = {
      fd.getJavaType match {
        case BYTE_STRING => obj.asInstanceOf[ByteString].toByteArray
        case ENUM => obj.asInstanceOf[EnumValueDescriptor].getName
        case MESSAGE => messageToRow(obj.asInstanceOf[AbstractMessage])
        case _ => obj
      }
    }

    val fieldDescriptors = message.getDescriptorForType.getFields
    val fields = message.getAllFields
    Row(
      fieldDescriptors.map {
        fd =>
          if (fields.containsKey(fd)) {
            val obj = fields.get(fd)
            if (fd.isRepeated) {
              obj.asInstanceOf[java.util.List[Object]].map(toRowData(fd, _)).toSeq
            } else {
              toRowData(fd, obj)
            }
          } else if (fd.isRepeated) {
            Seq()
          } else null
      }.toSeq: _*
    )
  }
}
