package com.systemcraftsman.demo.processing;

import com.systemcraftsman.demo.model.Task;
import com.systemcraftsman.demo.model.TaskCommand;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class TaskTransformerProcessFunction extends KeyedProcessFunction<String, TaskCommand, Task> implements Serializable {

    @Override
    public void processElement(TaskCommand taskCommand, Context ctx, Collector<Task> out) {
        Task task = new Task();
        task.setContent(taskCommand.getTaskContent());
        task.setId(taskCommand.getTaskId());

        out.collect(task);
    }
}