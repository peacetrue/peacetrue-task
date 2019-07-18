package com.github.peacetrue.task.executor;

import lombok.Getter;

import java.util.Objects;

/**
 * @author xiayx
 */
@Getter
public class TaskWriteException extends TaskIOException {

    private Task task;
    private Object value;

    public TaskWriteException(Throwable cause, Task task, Object value) {
        super(String.format("the task[%s].output[%s] serialize exception", task, value), cause);
        this.task = Objects.requireNonNull(task);
        this.value = Objects.requireNonNull(value);
    }

}
