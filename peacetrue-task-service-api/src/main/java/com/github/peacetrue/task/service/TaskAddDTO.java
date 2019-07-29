package com.github.peacetrue.task.service;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskAddDTO<Id, OperatorId> extends OperatorCapableImpl<OperatorId> {

    private List<Id> dependentIds;
    private String groupId;
    private String name;
    private String body;
    private String input;
    private List<TaskAddDTO<Id, OperatorId>> dependOn;
    /** 新增后立刻执行 */
    private Boolean execute;
}