package com.github.peacetrue.task.mybatis;

import org.springframework.stereotype.Component;

/**
 * @author xiayx
 */
@Component
public class SolveQuestion {

    public Question submitQuestion(long time) throws InterruptedException {
        Thread.sleep(time);
        return new Question("如何快乐的生活？");
    }

    public Question submitQuestion(long time, String name) throws InterruptedException {
        Thread.sleep(time);
        return new Question(name + "如何快乐的生活？");
    }

    public String[] thinkPlan(Question question, long time) throws InterruptedException {
        Thread.sleep(time);
        return new String[]{"保持身体健康", "保持家庭和睦", "赚钱自给自足", "发展兴趣和创造"};
    }

    public boolean execute(String[] steps, long time) throws InterruptedException {
        Thread.sleep(time);
        for (int i = 0; i < steps.length; i++) {
            System.out.println((i + 1) + ":" + steps[i]);
        }
        return true;
    }
}
