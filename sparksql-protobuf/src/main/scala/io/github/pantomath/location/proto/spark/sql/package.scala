package io.github.pantomath.location.proto.spark

import com.google.protobuf.AbstractMessage
import org.apache.spark.annotation.Experimental
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SQLContext}

import scala.reflect.runtime.universe.TypeTag

package object sql {

  implicit class ProtoSQLContext(sqlContext: SQLContext) {
    /**
     * :: Experimental ::
     * Creates a DataFrame from an RDD of protobuf messages.
     *
     * @group dataframes
     */
    @Experimental
    def createDataFrame[A <: AbstractMessage : TypeTag](rdd: RDD[A]): DataFrame = {
      val schema = ProtoReflection.schemaFor[A].dataType.asInstanceOf[StructType]
      val rowRDD = rdd.map(ProtoRDDConversions.messageToRow)
      sqlContext.createDataFrame(rowRDD, schema)
    }


    /**
     * :: Experimental ::
     * Creates a DataFrame from an RDD of protobuf messages.
     *
     * Have to use a different name because the implicit doesn't trigger
     * due to existing method that works on Java bean.
     *
     * @group dataframes
     */
    @Experimental
    def createDataFrameFromProto(rdd: RDD[_ <: AbstractMessage], clazz: Class[_ <: AbstractMessage]): DataFrame = {
      val schema = ProtoReflection.schemaFor(clazz).dataType.asInstanceOf[StructType]
      val rowRDD = rdd.map(ProtoRDDConversions.messageToRow)
      sqlContext.createDataFrame(rowRDD, schema)
    }
  }
}
