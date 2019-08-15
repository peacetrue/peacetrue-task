package com.github.peacetrue.task.executor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.Future;

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

        @Bean
        public TaskExecutorImpl taskExecutor() {
            return new TaskExecutorImpl() {
                @Override
                protected Map<String, Object> getDependentVariables(Task task) {
                    return super.getDependentVariables(task);
                }
            };
        }
    }

    //tag::class[]
    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    public void execute() throws Exception {
        //首先执行【发现问题】，【发现问题】执行完成后执行【思考计划】，【思考计划】执行完成后执行【执行计划】
        //发现问题
        TaskImpl findQuestion = new TaskImpl("@solveQuestion.findQuestion('安宁!',1000)");

        //思考计划，#outputs引用所有依赖任务的输出结果，#outputs[0]引用findQuestion的输出结果，output的顺序和依赖任务的顺序相同
        TaskImpl thinkPlan = new TaskImpl("@solveQuestion.thinkPlan(#outputs[0],#root)", 1000);
        findQuestion.addDependOn(thinkPlan);//thinkPlan依赖于findQuestion

        //执行计划
        TaskImpl execute = new TaskImpl("@solveQuestion.execute(#outputs[0],#root)", 1000);
        thinkPlan.addDependOn(execute);//execute依赖于thinkPlan

        Future<Question> future = taskExecutor.execute(findQuestion);
        Question result = future.get();
        System.out.println("findQuestion execute finished and return " + result);
    }
    //end::class[]

}