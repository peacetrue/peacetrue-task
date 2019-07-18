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

    private String groupId;
    private String name;
    private String input;
    private String body;
    private List<Id> dependentIds;

}