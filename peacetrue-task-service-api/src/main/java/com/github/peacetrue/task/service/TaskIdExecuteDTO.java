package com.github.peacetrue.task.service;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskIdExecuteDTO<T, OperatorId> extends OperatorCapableImpl<OperatorId> {

    private T id;

    @Override
    public String toString() {
        return String.valueOf(getId());
    }
}
