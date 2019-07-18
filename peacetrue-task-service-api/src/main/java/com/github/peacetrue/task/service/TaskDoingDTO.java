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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskDoingDTO<Id, OperatorId> extends OperatorCapableImpl<OperatorId> {
    private Id id;
    private String remark;
}
