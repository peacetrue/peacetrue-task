package com.github.peacetrue.task.executor;

import com.github.peacetrue.flow.Tense;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiayx
 */
@Getter
@Setter
public class TaskImpl implements Task {

    private String body;
    private String input;
    private String stateCode = Tense.TODO.getCode();
    private String output;
    private Long duration;
    private Set<Task> dependent;
    private Set<Task> dependOn;

    public TaskImpl() {
    }

    public TaskImpl(String body, String input) {
        this.body = body;
        this.input = input;
    }

    public TaskImpl(String body, String input, TaskImpl... dependent) {
        this.body = body;
        this.input = input;
        if (dependent != null) Arrays.stream(dependent).forEach(this::addDependent);
    }

    public void addDependent(TaskImpl task) {
        this.getDependent().add(task);
        task.getDependOn().add(this);
    }

    public void addDependOn(TaskImpl task) {
        this.getDependOn().add(task);
        task.getDependent().add(this);
    }

    public Set<Task> getDependent() {
        if (dependent == null) dependent = new HashSet<>();
        return dependent;
    }

    public Set<Task> getDependOn() {
        if (dependOn == null) dependOn = new HashSet<>();
        return dependOn;
    }

    @Override
    public String toString() {
        return "{" +
                "body='" + body + '\'' +
                ", input='" + input + '\'' +
                '}';
    }
}
