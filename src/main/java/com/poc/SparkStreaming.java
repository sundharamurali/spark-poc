package com.poc;

import com.sun.media.jfxmedia.logging.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;



public class SparkStreaming {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        Logger.setLevel(Logger.ERROR);
        SparkSession sparkSession = getSparkSession();
        Dataset<Row> kafkaStreamDataset = readFromKafka(sparkSession);
        // Print the data to the console
        StreamingQuery query = kafkaStreamDataset.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .writeStream()
                .outputMode("append")
                .format("console")
                .start();
        query.awaitTermination();
    }

    public static SparkSession getSparkSession() {
        // Create a Spark session
        SparkSession spark = SparkSession.builder()
                .appName("KafkaSparkStreaming")
                .master("local[*]")
                .getOrCreate();
        return spark;
    }

    public static Dataset<Row> readFromKafka(SparkSession sparkSession) {
        // Read data from Kafka using Spark's streaming API
        Dataset<Row> kafkaStreamDataSet = sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "test")
                .option("startingOffsets", "earliest")
                .load();
        return kafkaStreamDataSet;
    }
}
