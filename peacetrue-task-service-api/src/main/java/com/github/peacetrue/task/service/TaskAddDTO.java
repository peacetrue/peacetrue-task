package com.github.peacetrue.task.service;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author xiayx
 */
@Data
public class TaskAddDTO<Id, OperatorId> extends OperatorCapableImpl<OperatorId> {

    private List<Id> dependentIds;
    private String groupId;
    private String name;
    private String body;
    private String input;
    private List<TaskAddDTO<Id, OperatorId>> dependOn;
    /** 新增后立刻执行 */
    private Boolean execute;

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