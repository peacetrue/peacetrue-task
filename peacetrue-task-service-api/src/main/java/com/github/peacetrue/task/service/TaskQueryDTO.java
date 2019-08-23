package com.github.peacetrue.task.service;

import com.github.peacetrue.core.Range;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiayx
 */
@Data
public class TaskQueryDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    private String groupId;
    private String name;
    private String body;
    private String input;
    private String stateCode;
    private String output;
    private Range.Date createdTime = new Range.Date();
}
