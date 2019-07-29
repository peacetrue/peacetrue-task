package com.github.peacetrue.task.executor;

import com.github.peacetrue.task.mybatis.SolveQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xiayx
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        JacksonAutoConfiguration.class,
        TaskExecutorAutoConfiguration.class,
        TaskExecutorAmqpAutoConfiguration.class,
        TaskExecutorImplTest.CustomConfiguration.class
}, properties = {
        "logging.level.com.github.peacetrue=trace"
})
public class TaskExecutorImplTest {

    public static class CustomConfiguration {
        @Bean
        public SolveQuestion solveQuestion() {
            return new SolveQuestion();
        }
    }

    @Autowired
    private TaskIOMapper taskIOMapper;
    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    public void execute() throws Exception {
        TaskImpl task = new TaskImpl("@solveQuestion.submitQuestion(1000,#root)", "安宁!");
        task.setInput(taskIOMapper.writeObject(task, "安宁!"));
        task.addDependOn(new TaskImpl("@solveQuestion.thinkPlan(#task_0,#root)", "1000"));
        taskExecutor.execute(task);
        Thread.sleep(5000L);
    }

}