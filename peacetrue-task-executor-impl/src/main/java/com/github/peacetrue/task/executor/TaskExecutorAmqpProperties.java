package com.github.peacetrue.task.executor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiayx
 */
@Data
@ConfigurationProperties(prefix = "peacetrue.task.amqp")
public class TaskExecutorAmqpProperties {
    private String startedName = "task.started";
    private String succeededName = "task.succeeded";
    private String failedName = "task.failed";
}
