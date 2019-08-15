package com.github.peacetrue.task.executor;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.EventObject;

/**
 * @author xiayx
 */
@Getter
public class TaskCompletedEvent extends EventObject {

    private Object output;
    private Throwable exception;

    public TaskCompletedEvent(Task source, @Nullable Object output, @Nullable Throwable exception) {
        super(source);
        this.output = output;
        this.exception = exception;
    }

    @Override
    public Task getSource() {
        return (Task) super.getSource();
    }


}
