package com.github.peacetrue.task.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author xiayx
 */
@Configuration
@EnableConfigurationProperties(ControllerTaskProperties.class)
@PropertySource("classpath:peacetrue-task-controller.properties")
public class ControllerTaskAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TaskController.class)
    public TaskController taskController() {
        return new TaskController();
    }

    @Bean
    public WebMvcConfigurer taskWebMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true)
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods("*");
            }
        };
    }

}
