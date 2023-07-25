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

    @Autowired
    private CommandValidationProcessFunction commandValidationProcessFunction;

    @Autowired
    private ProfanityCheckProcessFunction profanityCheckProcessFunction;

    @Autowired
    private TaskTransformerProcessFunction taskTransformerProcessFunction;

    // Injects the Kafka bootstrap servers configuration value
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @PostConstruct
    public void process() throws Exception {

        // Initializes the execution environment for Flink stream processing
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Initializes the Kafka source, which consumes task commands from the related topic
        KafkaSource<TaskCommand> taskCommandsSource = KafkaSource.<TaskCommand>builder()
                .setBootstrapServers(bootstrapServers)
                .setGroupId("task-consumer-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setTopics("task-commands")
                .setDeserializer(new CommandDeserializer())
                .build();

        // Uses the Kafka source to create a data stream
        DataStream<TaskCommand> kafkaStream = env.fromSource(taskCommandsSource, WatermarkStrategy.noWatermarks(), "Kafka Source");

        // Processes the stream for command validation
        DataStream<TaskCommand> validatedCommandStream = kafkaStream
                .keyBy(TaskCommand::getTaskId)
                .process(commandValidationProcessFunction);

        // Processes the stream for profanity check
        DataStream<TaskCommand> checkedContentStream = validatedCommandStream
                .keyBy(TaskCommand::getTaskId)
                .process(profanityCheckProcessFunction);

        // Processes the stream for command to task transformation
        DataStream<Task> taskStream = checkedContentStream
                .keyBy(TaskCommand::getTaskId)
                .process(taskTransformerProcessFunction);

        // Initializes the Kafka sink, which produces transformed tasks to the related topic
        KafkaSink<Task> taskSnapshotsSink = KafkaSink.<Task>builder()
                .setBootstrapServers(bootstrapServers)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setRecordSerializer(new TaskSerializer("task-snapshots"))
                .build();

        // Adds the Kafka sink to the last processed stream; task stream
        taskStream.sinkTo(taskSnapshotsSink);

        // Executes the job
        env.execute("Todo App");
    }
}
