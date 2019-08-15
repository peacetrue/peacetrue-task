package com.github.peacetrue.task.executor;

import java.util.EventObject;
import java.util.Objects;

/**
 * @author xiayx
 */
public class TaskFailedEvent extends EventObject {

    private Throwable exception;

    public TaskFailedEvent(Task source, Throwable exception) {
        super(source);
        this.exception = Objects.requireNonNull(exception);
    }

    @Override
    public Task getSource() {
        return (Task) super.getSource();
    }

    public Throwable getException() {
        return exception;
    }
}
