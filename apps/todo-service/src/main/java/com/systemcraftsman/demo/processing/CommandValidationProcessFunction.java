package com.systemcraftsman.demo.processing;

import com.systemcraftsman.demo.model.CommandType;
import com.systemcraftsman.demo.model.TaskCommand;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CommandValidationProcessFunction extends KeyedProcessFunction<String, TaskCommand, TaskCommand> implements Serializable {

    @Override
    public void processElement(TaskCommand taskCommand, Context ctx, Collector<TaskCommand> out) {
        if (taskCommand.getCommandType().equals(CommandType.DELETE)
                || !taskCommand.getTaskContent().trim().isEmpty()){
            out.collect(taskCommand);
        }
    }
}