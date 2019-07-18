package com.github.peacetrue.task.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskFailureDTO<Id, OperatorId> extends TaskDoingDTO<Id, OperatorId> {
    private String exception;
    private Long duration;

    public TaskFailureDTO() {
    }

    public TaskFailureDTO(Id id, String remark, String exception, Long duration) {
        super(id, remark);
        this.exception = exception;
        this.duration = duration;
    }
}
