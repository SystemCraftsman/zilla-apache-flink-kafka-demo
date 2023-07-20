package com.systemcraftsman.demo.processing;

import com.systemcraftsman.demo.model.TaskCommand;
import com.systemcraftsman.demo.service.ProfanityService;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ProfanityCheckProcessFunction extends KeyedProcessFunction<String, TaskCommand, TaskCommand> implements Serializable {

    @Autowired
    private ProfanityService profanityService;

    @Override
    public void processElement(TaskCommand taskCommand, Context ctx, Collector<TaskCommand> out) {
        for (String badWord : profanityService.getBadWords()) {
            if (taskCommand.getTaskContent().contains(badWord)){
                String replacedContent = taskCommand.getTaskContent().replaceAll(badWord, "***");
                taskCommand.setTaskContent(replacedContent);
            }
        }
        out.collect(taskCommand);
    }
}