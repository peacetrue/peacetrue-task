package com.github.peacetrue.task.mybatis;

import com.github.peacetrue.task.executor.Task;
import com.github.peacetrue.task.service.TaskVO;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiayx
 */
@Getter
public class GroupTask extends TaskVO<Object, Object> implements Task {

    private Set<GroupTask> tasks;

    public GroupTask(Set<GroupTask> tasks) {
        this.tasks = Objects.requireNonNull(tasks);
    }

    @Override
    public Set<Task> getDependent() {
        if (CollectionUtils.isEmpty(getDependentIds())) return Collections.emptySet();
        return getDependentIds().stream().map(this::findById).collect(Collectors.toSet());
    }

    private GroupTask findById(Object id) {
        return tasks.stream().filter(task -> id.equals(task.getId())).findAny().orElseThrow(() -> new IllegalStateException(String.format("the task[%s] not exists in group", id)));
    }

    @Override
    public Set<Task> getDependOn() {
        return tasks.stream().filter(task -> task.getDependentIds() != null && task.getDependentIds().contains(task.getId())).collect(Collectors.toSet());
    }
}
