package com.github.peacetrue.task.executor;

import java.util.EventObject;

/**
 * @author xiayx
 */
public class TaskStartedEvent extends EventObject {

    public TaskStartedEvent(Task source) {
        super(source);
    }

    @Override
    public Task getSource() {
        return (Task) super.getSource();
    }
}
