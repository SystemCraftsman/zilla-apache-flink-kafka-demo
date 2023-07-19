package com.systemcraftsman.demo.processing;

import com.systemcraftsman.demo.model.TaskCommand;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class ProfanityCheckProcessFunction extends KeyedProcessFunction<String, TaskCommand, TaskCommand> {

    @Override
    public void processElement(TaskCommand taskCommand, Context ctx, Collector<TaskCommand> out) {
        String badWords = "shit";
        if (!taskCommand.getTaskContent().contains(badWords)){
            out.collect(taskCommand);
        }
    }
}