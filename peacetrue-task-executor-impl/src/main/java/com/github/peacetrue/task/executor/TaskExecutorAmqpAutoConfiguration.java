package com.github.peacetrue.task.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author xiayx
 */
@Configuration
@ConditionalOnProperty(prefix = "peacetrue.task.amqp", name = "enabled", havingValue = "true")
@ConditionalOnClass({RabbitAutoConfiguration.class, AmqpTemplate.class})
@ImportAutoConfiguration(RabbitAutoConfiguration.class)
@EnableConfigurationProperties(TaskExecutorAmqpProperties.class)
public class TaskExecutorAmqpAutoConfiguration {

    private TaskExecutorAmqpProperties properties;

    public TaskExecutorAmqpAutoConfiguration(TaskExecutorAmqpProperties properties) {
        this.properties = properties;
    }

    @Bean
    public FanoutExchange taskStartedFanoutExchange() {
        return new FanoutExchange(properties.getStartedName(), true, true);
    }

    @Bean
    public Queue taskStartedQueue() {
        return new Queue(properties.getStartedName(), true, false, true);
    }

    @Bean
    public Binding taskStartedBinding() {
        return BindingBuilder.bind(taskStartedQueue()).to(taskStartedFanoutExchange());
    }

    @Bean
    public FanoutExchange taskSucceededFanoutExchange() {
        return new FanoutExchange(properties.getSucceededName(), true, true);
    }

    @Bean
    public Queue taskSucceededQueue() {
        return new Queue(properties.getSucceededName(), true, false, true);
    }

    @Bean
    public Binding taskSucceededBinding() {
        return BindingBuilder.bind(taskSucceededQueue()).to(taskSucceededFanoutExchange());
    }


    @Bean
    public FanoutExchange taskFailedFanoutExchange() {
        return new FanoutExchange(properties.getFailedName(), true, true);
    }

    @Bean
    public Queue taskFailedQueue() {
        return new Queue(properties.getFailedName(), true, false, true);
    }

    @Bean
    public Binding taskFailedBinding() {
        return BindingBuilder.bind(taskFailedQueue()).to(taskFailedFanoutExchange());
    }


    @Bean
    public Object taskHandler() {
        return new Object() {
            private Logger logger = LoggerFactory.getLogger(getClass());
            @Autowired
            private AmqpTemplate amqpTemplate;

            @Autowired
            private TaskExecutorAmqpProperties properties;

            @EventListener
            @Order(Ordered.HIGHEST_PRECEDENCE + 10)
            public void handleStarted(TaskStartedEvent event) {
                logger.info("任务[{}]已开始，推送消息至交换机[{}]", event.getSource(), properties.getStartedName());
                amqpTemplate.convertAndSend(properties.getStartedName(), null, event.getSource());
            }

            @EventListener
            @Order(Ordered.HIGHEST_PRECEDENCE + 10)
            public void handleSucceeded(TaskSucceededEvent event) {
                logger.info("任务[{}]已执行成功，推送消息至交换机[{}]", event.getSource(), properties.getSucceededName());
                amqpTemplate.convertAndSend(properties.getSucceededName(), null, event.getSource());
            }

            @EventListener
            @Order(Ordered.HIGHEST_PRECEDENCE + 10)
            public void handleFailed(TaskFailedEvent event) {
                logger.info("任务[{}]已执行开始，推送消息至交换机[{}]", event.getSource(), properties.getFailedName());
                amqpTemplate.convertAndSend(properties.getFailedName(), null, event.getSource());
            }
        };
    }

}
