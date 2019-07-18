package com.github.peacetrue.task.executor;

/**
 * 任务执行异常
 *
 * @author xiayx
 */
public class TaskExecuteException extends RuntimeException {

    public TaskExecuteException() {
    }

    public TaskExecuteException(String message) {
        super(message);
    }

    public TaskExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskExecuteException(Throwable cause) {
        super(cause);
    }

    public TaskExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
