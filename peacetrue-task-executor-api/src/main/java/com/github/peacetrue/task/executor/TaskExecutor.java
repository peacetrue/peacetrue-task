package com.github.peacetrue.task.executor;

import java.util.concurrent.Future;

/**
 * @author xiayx
 */
public interface TaskExecutor {

    /**
     * 执行任务
     *
     * @param task 任务信息
     * @return the future of execute task
     * @throws TaskExecuteException any exception occur in execute
     */
    Future execute(Task task) throws TaskExecuteException;
}
