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

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;
import java.util.List;

import static org.apache.spark.sql.functions.col;

/**
 * <p>Test class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class Test {


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().master("local[*]").appName("Location Service Test").getOrCreate();
        StructType schema = new StructType().add("ip", DataTypes.StringType);
        IP2LookupFunction.init("localhost", 8080);
        List<Row> data = Arrays.asList(RowFactory.create("128.107.241.164"),
                RowFactory.create("98.143.133.154"),
                RowFactory.create("107.6.171.130"),
                RowFactory.create("45.33.66.232"),
                RowFactory.create("69.175.97.170"),
                RowFactory.create("173.255.213.43"));
        Dataset<Row> dataset = spark.createDataFrame(data, schema).withColumn("location", IP2LookupFunction.getLocation.apply(col("ip")));
        dataset.printSchema();
        for (Row row : dataset.takeAsList(10)) {
            System.out.println(row);
        }

    }
}
