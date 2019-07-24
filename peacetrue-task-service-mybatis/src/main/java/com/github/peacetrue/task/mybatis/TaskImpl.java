package com.github.peacetrue.task.mybatis;

import com.github.peacetrue.task.executor.Task;
import com.github.peacetrue.task.service.TaskVO;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

/**
 * @author xiayx
 */
@Getter
@Setter
public class TaskImpl extends TaskVO implements Task {

    private Object operatorId;
    private Set<TaskImpl> executed;

    public boolean equals(Object object) {
        if (!(object instanceof TaskImpl)) return false;
        TaskImpl other = (TaskImpl) object;
        return other == this || Objects.equals(getId(), other.getId());
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }
}
