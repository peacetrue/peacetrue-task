package com.github.peacetrue.task.service;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskGroupIdExecuteDTO<OperatorId> extends OperatorCapableImpl<OperatorId> {

    private static final long serialVersionUID = 0L;

    private String groupId;

    @Override
    public String toString() {
        return String.valueOf(groupId);
    }
}
