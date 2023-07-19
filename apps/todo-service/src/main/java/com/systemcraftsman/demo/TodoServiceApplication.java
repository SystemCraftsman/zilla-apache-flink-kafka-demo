/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */
package com.systemcraftsman.demo;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings({"HideUtilityClassConstructor"})
public class TodoServiceApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TodoServiceApplication.class, args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<TaskCommand> kafkaSource = KafkaSource.<TaskCommand>builder()
                .setBootstrapServers("localhost:9092")
                .setGroupId("task-consumer-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setTopics("task-commands")
                .setDeserializer(new CommandDeserializer())
                .build();

        // Add Kafka consumer as a source to the execution environment
        DataStream<TaskCommand> kafkaStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source");

        // Process the Kafka stream
        DataStream<TaskCommand> validatedCommandStream = kafkaStream
                .keyBy(TaskCommand::getTaskId)
                .process(new CommandValidationProcessFunction());

        DataStream<TaskCommand> checkedContentStream = validatedCommandStream
                .filter(taskCommand -> !taskCommand.getCommandType().equals(CommandType.DELETE))
                .keyBy(TaskCommand::getTaskId)
                .process(new ProfanityCheckProcessFunction());

        DataStream<Task> taskStream = checkedContentStream
                .keyBy(TaskCommand::getTaskId)
                .process(new TaskTransformerProcessFunction());

        KafkaSink<Task> taskSnapshotsSink = KafkaSink.<Task>builder()
                .setBootstrapServers("localhost:9092")
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setRecordSerializer(new TaskSerializer("task-snapshots"))
                .build();

        // Add Kafka producers as sinks to the processed stream
        taskStream.sinkTo(taskSnapshotsSink);

        // Execute the job
        env.execute("Todo App");
    }
}
