package com.github.peacetrue.task.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiayx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskSuccessDTO<Id, OperatorId> extends TaskDoingDTO<Id, OperatorId> {

    private static final long serialVersionUID = 0L;

    private Object output;
    private Long duration;

}
