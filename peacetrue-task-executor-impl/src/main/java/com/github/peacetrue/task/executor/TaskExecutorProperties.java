package com.github.peacetrue.task.executor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiayx
 */
@Data
@ConfigurationProperties(prefix = "peacetrue.task")
public class TaskExecutorProperties {

    private VariableNames variableNames = new VariableNames();

    @Data
    public static class VariableNames {
        private String tasks = "tasks";
        private String taskPrefix = "task_";
        private String outputs = "outputs";
        private String outputPrefix = "output_";
    }
}
