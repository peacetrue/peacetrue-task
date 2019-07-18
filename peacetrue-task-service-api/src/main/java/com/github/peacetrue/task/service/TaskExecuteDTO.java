package com.github.peacetrue.task.service;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecuteDTO<T, OperatorId> extends OperatorCapableImpl<OperatorId> {
    private T id;
}
