package com.github.peacetrue.task.service;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @param <Id>
 * @param <OperatorId>
 */
@Data
public class TaskVO<Id, OperatorId> implements Serializable {

    private static final long serialVersionUID = 0L;

    private Id id;
    private String groupId;
    private String name;
    private String input;
    private String body;
    private String stateCode;
    private String output;
    private String exception;
    private Long duration;
    private OperatorId creatorId;
    private Date createdTime;
    private OperatorId modifierId;
    private Date modifiedTime;
    private List<Id> dependentIds;


}