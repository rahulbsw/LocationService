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
