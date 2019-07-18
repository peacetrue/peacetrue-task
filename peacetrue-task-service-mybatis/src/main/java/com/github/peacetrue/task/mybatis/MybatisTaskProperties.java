package com.github.peacetrue.task.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiayx
 */
@Data
@ConfigurationProperties(prefix = "peacetrue.task")
public class MybatisTaskProperties {

    private TableNames tableNames = new TableNames();

    @Data
    public static class TableNames {
        private String task = "task";
        private String taskDependency = "task_dependency";
    }
}
