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

public class Test {


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
