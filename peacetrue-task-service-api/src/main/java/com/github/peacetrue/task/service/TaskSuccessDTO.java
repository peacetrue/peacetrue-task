package com.github.peacetrue.task.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskSuccessDTO<Id, OperatorId> extends TaskDoingDTO<Id, OperatorId> {
    private String output;
    private Long duration;

    public TaskSuccessDTO() {
    }

    public TaskSuccessDTO(Id id, String remark, String output, Long duration) {
        super(id, remark);
        this.output = output;
        this.duration = duration;
    }
}
