package com.poc;

import com.poc.kafkamock.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.execution.streaming.MemoryStream;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

class SparkStreamingTest {

    private SparkStreaming sparkStreaming;

    private SparkSession sparkSession;

    @BeforeEach
    void setUp() {
        sparkStreaming = new SparkStreaming();
        sparkSession = SparkSession.builder()
                .appName("test_streaming")
                .master("local[*]")
                .getOrCreate();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void mockProducer() throws ExecutionException, InterruptedException {
        MockProducer mockProducer
                = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        KafkaProducer kafkaProducer = new KafkaProducer(mockProducer);
        Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send("soccer",
                "{\"site\" : \"baeldung\"}");
        recordMetadataFuture.get();

    }

    @Test
    public void mainTest() throws TimeoutException, StreamingQueryException, InterruptedException {

        // Create a MemoryStream of strings
        MemoryStream<TestData> memStream = new MemoryStream<>(1,sparkSession.sqlContext(), Option.apply(1), Encoders.bean(TestData.class));
        List<TestData> list= Arrays.asList(
                new TestData("1", "Val1"),
                new TestData("2", "Val2")
        );
        memStream.addData(convertListToSeq(list));

        // Convert the MemoryStream to a Dataset<Row>
        Dataset<Row> input = memStream.toDS().selectExpr("CAST(key AS STRING)", "CAST(value1 AS STRING)");
//        Dataset<Row> inputStream = sparkSession.readStream()
//                .format("memory")
//                .load();
        Dataset<Row> transformedInput = input.selectExpr("cast(key as STRING) as Cust_Key",
                "cast(value1 as STRING) as val");
        StreamingQuery query = transformedInput
                .writeStream()
                .outputMode("append")
                .format("console")
                .start();
        query.awaitTermination();

        Thread.sleep(10*1000);
        System.exit(0);

//        SparkStreaming.main(new String[]{});
    }

    public Seq<TestData> convertListToSeq(List<TestData> inputList) {
        return JavaConverters.asScalaBufferConverter(inputList).asScala().toSeq();
//        return inputList.asSc
    }
}