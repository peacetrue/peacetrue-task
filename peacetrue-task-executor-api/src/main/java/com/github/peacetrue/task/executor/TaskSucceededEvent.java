package com.github.peacetrue.task.executor;

import javax.annotation.Nullable;
import java.util.EventObject;

/**
 * @author xiayx
 */
public class TaskSucceededEvent extends EventObject {

    private Object output;

    public TaskSucceededEvent(Task source, @Nullable Object output) {
        super(source);
        this.output = output;
    }

    @Override
    public Task getSource() {
        return (Task) super.getSource();
    }

    @Nullable
    public Object getOutput() {
        return output;
    }
}
