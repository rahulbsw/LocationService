package io.github.pantomath.location.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.types.Row;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        tableEnv.registerFunction("location", new IP2GeoLocationTableFunction("localhost"));
        DataStream<String> dataStream =env.fromCollection(
                Arrays.asList("128.107.241.164",
                                "98.143.133.154",
                                "107.6.171.130", "45.33.66.232", "69.175.97.170", "173.255.213.43"));
        Table inputTable = tableEnv.fromDataStream(dataStream);
        tableEnv.createTemporaryView("InputTable", inputTable);
        Table resultTable = tableEnv.sqlQuery("SELECT UPPER(f0) FROM InputTable");

// interpret the insert-only Table as a DataStream again
        //resultTable.leftOuterJoin(new Table(tableEnv, "split(a) as (word, length)"))
        DataStream<Row> resultStream = tableEnv.toDataStream(resultTable);
// add a printing sink and execute in DataStream API
        resultTable.printSchema();
        resultStream.print();
        env.execute();
    }
}
