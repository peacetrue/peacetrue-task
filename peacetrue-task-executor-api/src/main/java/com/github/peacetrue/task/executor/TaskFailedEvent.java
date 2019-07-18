package com.github.peacetrue.task.executor;

import java.util.EventObject;
import java.util.Objects;

/**
 * @author xiayx
 */
public class TaskFailedEvent extends EventObject {

    private Throwable throwable;

    public TaskFailedEvent(Task source, Throwable throwable) {
        super(source);
        this.throwable = Objects.requireNonNull(throwable);
    }

    @Override
    public Task getSource() {
        return (Task) super.getSource();
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
