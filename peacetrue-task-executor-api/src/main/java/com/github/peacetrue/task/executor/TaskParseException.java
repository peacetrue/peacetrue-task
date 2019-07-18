package com.github.peacetrue.task.executor;

/**
 * 任务解析异常
 *
 * @author xiayx
 */
public class TaskParseException extends TaskExecuteException {

    public TaskParseException() {
    }

    public TaskParseException(String message) {
        super(message);
    }

    public TaskParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskParseException(Throwable cause) {
        super(cause);
    }

    public TaskParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
