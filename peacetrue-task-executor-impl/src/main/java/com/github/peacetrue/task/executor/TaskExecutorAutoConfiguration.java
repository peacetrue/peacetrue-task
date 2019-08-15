package com.github.peacetrue.task.executor;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

/**
 * @author xiayx
 */
@Configuration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableConfigurationProperties(TaskExecutorProperties.class)
public class TaskExecutorAutoConfiguration {

    public static final String EXECUTOR_SERVICE_TASK = "peacetureTaskExecutorService";
    public static final String EXECUTOR_SERVICE_TRIGGER = "peacetureTriggerExecutorService";
    public static final String TASK_ID = "peacetureTaskId";

    private TaskExecutorProperties properties;

    public TaskExecutorAutoConfiguration(TaskExecutorProperties properties) {
        this.properties = properties;
    }

    /** 本地任务执行器 */
    @Bean
    @ConditionalOnMissingBean(TaskExecutor.class)
    public TaskExecutor taskExecutor() {
        TaskExecutorImpl taskExecutor = new TaskExecutorImpl();
        taskExecutor.setVariableNames(properties.getVariableNames());
        return taskExecutor;
    }

    /** 任务的执行器服务，用于执行任务 */
    @Bean(EXECUTOR_SERVICE_TASK)
    @ConditionalOnMissingBean(name = EXECUTOR_SERVICE_TASK)
    public ExecutorService taskExecutorService() {
        return Executors.newCachedThreadPool();
    }

    /** 持久化的执行器服务，用于持久化任务状态 */
    @Bean(EXECUTOR_SERVICE_TRIGGER)
    @ConditionalOnMissingBean(name = EXECUTOR_SERVICE_TRIGGER)
    public ExecutorService triggerExecutorService() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    @ConditionalOnMissingBean(ExpressionParser.class)
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }

    @Bean(name = TASK_ID)
    @ConditionalOnMissingBean(name = TASK_ID)
    public BiFunction<Task, Integer, String> taskId() {
        return (task, integer) -> integer.toString();
    }

}
