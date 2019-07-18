package com.github.peacetrue.task.mybatis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.peacetrue.associate.AssociatedSourceBuilder;
import com.github.peacetrue.associate.AssociatedSourceBuilderImpl;
import com.github.peacetrue.jackson.ObjectMapperWrapper;
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

/**
 * @author xiayx
 */
@Configuration
@EnableConfigurationProperties(MybatisTaskProperties.class)
@MapperScan(basePackageClasses = MybatisTaskAutoConfiguration.class, annotationClass = Mapper.class)
public class MybatisTaskAutoConfiguration {

    public MybatisTaskAutoConfiguration(MybatisTaskProperties properties) {
        Optional.ofNullable(properties.getTableNames()).ifPresent(tableNames -> {
            Optional.ofNullable(tableNames.getTask()).ifPresent(value -> setField(TaskDynamicSqlSupport.task, value));
            Optional.ofNullable(tableNames.getTaskDependency()).ifPresent(value -> setField(TaskDependencyDynamicSqlSupport.taskDependency, value));
        });
    }

    private void setField(Object object, String value) {
        Field field = ReflectionUtils.findField(object.getClass(), "name", String.class);
        field.setAccessible(true);
        ReflectionUtils.setField(field, object, value);
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
    @ConditionalOnMissingBean(ObjectMapperWrapper.class)
    public ObjectMapperWrapper objectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }


}
