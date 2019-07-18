package com.github.peacetrue.task.executor;

import lombok.Getter;

import java.util.Objects;

/**
 * @author xiayx
 */
@Getter
public class TaskReadException extends TaskIOException {

    private Task task;
    private String value;

    public TaskReadException(Throwable cause, Task task, String value) {
        super(String.format("the task[%s].(input or output)[%s] deserialize exception", task, value), cause);
        this.task = Objects.requireNonNull(task);
        this.value = Objects.requireNonNull(value);
    }

}
