package com.github.peacetrue.task.mybatis;

import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.Task;
import com.github.peacetrue.task.executor.TaskDependencyService;
import com.github.peacetrue.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiayx
 */
public class TaskDependencyServiceImpl implements TaskDependencyService {

    @Autowired
    private TaskService taskService;

    @Override
    public List<Task> getDependent(Task task) {
        TaskImpl taskImpl = (TaskImpl) task;
        return taskService.getDependent(taskImpl.getId())
                .stream().map(vo -> {
                    TaskImpl dto = BeanUtils.map(vo, TaskImpl.class);
                    taskImpl.getExecuted().stream().filter(item -> item.equals(dto)).findAny()
                            .ifPresent(item -> {
                                dto.setStateCode(item.getStateCode());
                                dto.setOutput(item.getOutput());
                                dto.setDuration(item.getDuration());
                            });
                    dto.setOperatorId(taskImpl.getOperatorId());
                    dto.setExecuted(taskImpl.getExecuted());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<Task> getDependOn(Task task) {
        TaskImpl taskImpl = (TaskImpl) task;
        return taskService.getDependOn(taskImpl.getId()).stream().map(vo -> {
            TaskImpl dto = BeanUtils.map(vo, TaskImpl.class);
            dto.setOperatorId(taskImpl.getOperatorId());
            dto.setExecuted(taskImpl.getExecuted());
            return dto;
        }).collect(Collectors.toList());
    }
}
