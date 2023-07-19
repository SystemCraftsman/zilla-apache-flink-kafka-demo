package com.systemcraftsman.demo.serde;

import com.systemcraftsman.demo.model.CommandType;
import com.systemcraftsman.demo.model.TaskCommand;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CommandDeserializer implements KafkaRecordDeserializationSchema<TaskCommand> {
    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<TaskCommand> collector) {
        try {
            Map<String, String> headers = StreamSupport
                    .stream(consumerRecord.headers().spliterator(), false)
                    .collect(Collectors.toMap(h-> h.key(), h -> new String(h.value())));

            CommandType commandType = CommandType.valueOf(headers.get("command-type"));

            TaskCommand taskCommand = null;

            if(commandType.equals(CommandType.CREATE) || commandType.equals(CommandType.UPDATE)){
                Serde<TaskCommand> taskCommandSerde = SerdeFactory.jsonSerdeFor(TaskCommand.class, false);
                taskCommand = taskCommandSerde.deserializer().deserialize(consumerRecord.topic(), consumerRecord.value());

                taskCommand.setTaskId(new String(consumerRecord.key()));
                taskCommand.setCommandType(commandType);
            } else if (commandType.equals(CommandType.DELETE)) {
                taskCommand = new TaskCommand(new String(consumerRecord.key()), null, commandType);
            }

            collector.collect(taskCommand);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TypeInformation<TaskCommand> getProducedType() {
        return TypeInformation.of(new TypeHint<>() {
        });
    }
}
