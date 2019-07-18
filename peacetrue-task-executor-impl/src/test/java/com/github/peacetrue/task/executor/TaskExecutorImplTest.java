package com.github.peacetrue.task.executor;

import com.github.peacetrue.task.mybatis.SolveQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Function;

/**
 * @author xiayx
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        JacksonAutoConfiguration.class,
        ExecutorTaskAutoConfiguration.class,
        TaskExecutorImplTest.CustomConfiguration.class
}, properties = {
        "logging.level.com.github.peacetrue=trace"
})
public class TaskExecutorImplTest {

    public static class CustomConfiguration {
        @Bean(name = ExecutorTaskAutoConfiguration.TASK_ID)
        public Function<Task, String> taskId() {
            return task -> task.getBody().contains("thinkPlan") ? "task_2" : "task_1";
        }

        @Bean
        public SolveQuestion solveQuestion() {
            return new SolveQuestion();
        }
    }

    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    public void execute() throws Exception {
        TaskImpl task = new TaskImpl("@solveQuestion.submitQuestion(#root)", "1000");
        task.addDependOn(new TaskImpl("@solveQuestion.thinkPlan(#task_1,#root)", "1000", task));
        taskExecutor.execute(task);
        Thread.sleep(5000L);
    }

    @Test
    public void execute1() {
    }
}