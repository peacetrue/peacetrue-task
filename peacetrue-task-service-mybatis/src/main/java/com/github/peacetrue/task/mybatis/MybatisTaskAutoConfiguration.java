package com.github.peacetrue.task.mybatis;

import com.github.peacetrue.associate.AssociatedSourceBuilder;
import com.github.peacetrue.associate.AssociatedSourceBuilderImpl;
import com.github.peacetrue.mybatis.dynamic.MybatisDynamicUtils;
import com.github.peacetrue.task.executor.Task;
import com.github.peacetrue.task.service.TaskExecuteDTO;
import com.github.peacetrue.task.service.TaskService;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author xiayx
 */
@Configuration
@EnableConfigurationProperties(MybatisTaskProperties.class)
@MapperScan(basePackageClasses = MybatisTaskAutoConfiguration.class, annotationClass = Mapper.class)
public class MybatisTaskAutoConfiguration {

    public MybatisTaskAutoConfiguration(MybatisTaskProperties properties) {
        Optional.ofNullable(properties.getTableNames()).ifPresent(tableNames -> {
            Optional.ofNullable(tableNames.getTask()).ifPresent(value -> {
                MybatisDynamicUtils.setTableName(TaskDynamicSqlSupport.task, value);
                MybatisDynamicUtils.setTableName(TaskDynamicSqlSupport.task2, value);
            });
            Optional.ofNullable(tableNames.getTaskDependency()).ifPresent(value -> MybatisDynamicUtils.setTableName(TaskDependencyDynamicSqlSupport.taskDependency, value));
        });
    }

    @Bean
    @ConditionalOnMissingBean(TaskService.class)
    public TaskService taskService() {
        return new TaskServiceImpl();
    }

    @Bean
    public AssociatedSourceBuilder associatedSourceBuilder() {
        return new AssociatedSourceBuilderImpl();
    }

    @Bean("peacetureTaskId")
    public BiFunction<Task, Integer, String> taskId() {
        return (task, integer) -> ((TaskExecuteDTO) task).getName();
    }
}
