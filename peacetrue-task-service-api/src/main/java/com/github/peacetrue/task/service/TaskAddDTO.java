package com.github.peacetrue.task.service;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
public class TaskAddDTO<Id, OperatorId> extends OperatorCapableImpl<OperatorId> {

    private String groupId;
    private String name;
    private String body;
    private String input;
    private List<Id> dependentIds;
    private List<TaskAddDTO<Id, OperatorId>> dependOn;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof TaskAddDTO)) return false;
        TaskAddDTO<?, ?> that = (TaskAddDTO<?, ?>) object;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(body, that.body) &&
                Objects.equals(input, that.input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, name, body, input);
    }
}