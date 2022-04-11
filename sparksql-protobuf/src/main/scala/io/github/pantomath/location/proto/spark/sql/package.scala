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
