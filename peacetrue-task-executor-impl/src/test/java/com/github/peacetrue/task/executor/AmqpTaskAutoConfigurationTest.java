package com.github.peacetrue.task.executor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xiayx
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        TaskExecutorAmqpAutoConfiguration.class,
        AmqpTaskAutoConfigurationTest.Custom.class
})
public class AmqpTaskAutoConfigurationTest {

    public static class MyBean {
        @RabbitListener(queues = "task.started")
        public void processMessage(Object content) {
            System.out.println("convert:" + content);
        }
    }

    @Configuration
    public static class Custom {
        @Bean
        public MyBean myBean() {
            return new MyBean();
        }
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void send() throws Exception {
        amqpTemplate.convertAndSend("hello world!");
        Thread.sleep(1000 * 2);
    }

    @Test
    public void receive() throws InterruptedException {
//        Object convert = amqpTemplate.receiveAndConvert("task.started");
//        System.out.println("convert:" + convert);
        Thread.sleep(1000 * 10000);
    }
}