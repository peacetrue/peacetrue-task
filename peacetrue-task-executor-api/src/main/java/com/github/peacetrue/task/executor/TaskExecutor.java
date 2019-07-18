package com.github.peacetrue.task.executor;

/**
 * @author xiayx
 */
public interface TaskExecutor {

    /**
     * 执行任务
     *
     * @param task 任务信息
     */
    void execute(Task task) throws TaskExecuteException;
}
