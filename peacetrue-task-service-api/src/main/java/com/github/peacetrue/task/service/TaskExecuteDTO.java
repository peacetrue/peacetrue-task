package com.github.peacetrue.task.service;

import com.github.peacetrue.task.executor.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

/**
 * 任务执行DTO
 *
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskExecuteDTO<Id, OperatorId> extends TaskIdExecuteDTO<Id, OperatorId> implements Task {

    private String name;
    private String input;
    private String body;
    private String stateCode;
    private String output;
    private Long duration;
    private List<Task> dependent;
    private List<Task> dependOn;

    @Override
    public String toString() {
        return String.valueOf(getId());
    }

    public void addDependent(TaskExecuteDTO<Id, OperatorId> task) {
        this.getDependent().add(task);
        task.getDependOn().add(this);
    }

    public void addDependOn(TaskExecuteDTO<Id, OperatorId> task) {
        this.getDependOn().add(task);
        task.getDependent().add(this);
    }

    public List<Task> getDependent() {
        if (dependent == null) dependent = new LinkedList<>();
        return dependent;
    }

    public List<Task> getDependOn() {
        if (dependOn == null) dependOn = new LinkedList<>();
        return dependOn;
    }
}
