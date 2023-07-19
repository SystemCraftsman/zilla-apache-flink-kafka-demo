package com.systemcraftsman.demo.serde;

import com.systemcraftsman.demo.model.Task;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;

import javax.annotation.Nullable;

public class TaskSerializer implements KafkaRecordSerializationSchema<Task> {

    private String topic;

    public TaskSerializer(String topic) {
        this.topic = topic;
    }

    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(Task task, KafkaSinkContext context, Long timestamp) {
        Serde<Task> taskSerde = SerdeFactory.jsonSerdeFor(Task.class, false);
        Serde<String> taskSerdeForKey = SerdeFactory.jsonSerdeFor(String.class, true);

        ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic,
                taskSerdeForKey.serializer().serialize(topic, task.getId()),
                taskSerde.serializer().serialize(topic, task));

        record.headers().add("etag", "1".getBytes());

        return record;
    }

}
