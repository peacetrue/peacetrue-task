package com.github.peacetrue.task.mybatis;

import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.Task;
import com.github.peacetrue.task.service.TaskService;
import com.github.peacetrue.task.service.TaskVO;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiayx
 */
@Getter
@Setter
public class RealTimeTask extends TaskVO implements Task {

    private TaskService taskService;
    private Object operatorId;
    private Set<Task> dependent;
    private Set<Task> dependOn;
    private Set<RealTimeTask> executed;

    /** 获取当前任务依赖的其他任务 */
    public Set<Task> getDependent() {
        if (dependent == null) {
            dependent = taskService.getDependent(getId()).stream().map(vo -> {
                RealTimeTask dto = BeanUtils.map(vo, RealTimeTask.class);
                executed.stream().filter(task -> task.equals(dto)).findAny()
                        .ifPresent(task -> {
                            dto.setStateCode(task.getStateCode());
                            dto.setOutput(task.getOutput());
                            dto.setDuration(task.getDuration());
                        });
                dto.setTaskService(taskService);
                dto.setOperatorId(operatorId);
                dto.setExecuted(executed);
                return dto;
            }).collect(Collectors.toSet());
        }
        return dependent;
    }

    /** 获取依赖于当前任务的其他任务 */
    public Set<Task> getDependOn() {
        if (dependOn == null) {
            dependOn = taskService.getDependOn(getId()).stream().map(vo -> {
                RealTimeTask dto = BeanUtils.map(vo, RealTimeTask.class);
                dto.setTaskService(taskService);
                dto.setOperatorId(operatorId);
                dto.setExecuted(executed);
                return dto;
            }).collect(Collectors.toSet());
        }
        return dependOn;
    }

    public boolean equals(Object object) {
        if (!(object instanceof RealTimeTask)) return false;
        RealTimeTask other = (RealTimeTask) object;
        return other == this || Objects.equals(getId(), other.getId());
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }
}
