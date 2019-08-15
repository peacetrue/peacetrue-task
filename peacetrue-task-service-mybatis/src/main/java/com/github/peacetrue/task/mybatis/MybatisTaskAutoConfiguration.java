package com.github.peacetrue.task.mybatis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.peacetrue.associate.AssociatedSourceBuilder;
import com.github.peacetrue.associate.AssociatedSourceBuilderImpl;
import com.github.peacetrue.jackson.ObjectMapperWrapper;
import com.github.peacetrue.task.executor.Task;
import com.github.peacetrue.task.serialize.JacksonSerializeService;
import com.github.peacetrue.task.serialize.SerializeService;
import com.github.peacetrue.task.service.TaskExecuteDTO;
import com.github.peacetrue.task.service.TaskService;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

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
                setFieldValue(TaskDynamicSqlSupport.task, value);
                setFieldValue(TaskDynamicSqlSupport.task2, value);
            });
            Optional.ofNullable(tableNames.getTaskDependency()).ifPresent(value -> setFieldValue(TaskDependencyDynamicSqlSupport.taskDependency, value));
        });
    }

    private void setFieldValue(Object object, String value) {
        Field field = ReflectionUtils.findField(object.getClass(), "nameSupplier", Supplier.class);
        field.setAccessible(true);
        ReflectionUtils.setField(field, object, (Supplier) () -> value);
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

    @Bean
    @ConditionalOnMissingBean(SerializeService.class)
    public SerializeService serializeService(ObjectMapperWrapper wrapper) {
        return new JacksonSerializeService(wrapper);
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapperWrapper.class)
    public ObjectMapperWrapper objectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }


    @Bean("peacetureTaskId")
    public BiFunction<Task, Integer, String> taskId() {
        return (task, integer) -> ((TaskExecuteDTO) task).getName();
    }
}
