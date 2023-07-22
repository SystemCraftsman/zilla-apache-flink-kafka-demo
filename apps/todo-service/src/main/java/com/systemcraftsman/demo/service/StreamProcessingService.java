package com.systemcraftsman.demo.service;

import com.systemcraftsman.demo.model.CommandType;
import com.systemcraftsman.demo.model.Task;
import com.systemcraftsman.demo.model.TaskCommand;
import com.systemcraftsman.demo.processing.CommandValidationProcessFunction;
import com.systemcraftsman.demo.processing.ProfanityCheckProcessFunction;
import com.systemcraftsman.demo.processing.TaskTransformerProcessFunction;
import com.systemcraftsman.demo.serde.CommandDeserializer;
import com.systemcraftsman.demo.serde.TaskSerializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StreamProcessingService {

    //TODO: Inject the required process functions here by using the "@Autowired" annotation
    // to be able to use them for the stream processing

    // Injects the Kafka bootstrap servers configuration value
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @PostConstruct
    public void process() throws Exception {

        // Initializes the execution environment for Flink stream processing
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //TODO: Initialize the Kafka source, which consumes task commands from the "task-commands" topic
        KafkaSource<TaskCommand> taskCommandsSource = null;

        // Uses the Kafka source to create a data stream
        DataStream<TaskCommand> kafkaStream = env.fromSource(taskCommandsSource, WatermarkStrategy.noWatermarks(), "Kafka Source");

        //TODO: Process the stream for command validation by using the processor function for command validation
        DataStream<TaskCommand> validatedCommandStream = null;

        //TODO: Process the stream for profanity check by using the processor function for profanity check
        DataStream<TaskCommand> checkedContentStream = null;

        //TODO Process the stream for command to task transformation by using the processor function for task transformation
        DataStream<Task> taskStream = null;

        //TODO: Initialize the Kafka sink, which produces transformed tasks to the "task-snapshots" topic
        KafkaSink<Task> taskSnapshotsSink = null;

        // Adds the Kafka sink to the last processed stream; task stream
        taskStream.sinkTo(taskSnapshotsSink);

        // Executes the job
        env.execute("Todo App");
    }
}
