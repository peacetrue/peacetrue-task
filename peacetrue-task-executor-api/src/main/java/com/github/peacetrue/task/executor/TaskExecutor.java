package com.github.peacetrue.task.executor;

import com.github.peacetrue.flow.Tense;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author xiayx
 */
public interface TaskExecutor {

    /** 是否任务都已成功 */
    static boolean isAllSuccess(Collection<? extends Task> tasks) {
        return tasks.stream().allMatch(item -> item.getStateCode().equals(Tense.SUCCESS.getCode()));
    }

    /**
     * 执行任务
     *
     * @param task 任务信息
     * @return the future of execute task
     * @throws TaskExecuteException any exception occur in execute
     */
    <T> Future<T> execute(Task task) throws TaskExecuteException;
}
