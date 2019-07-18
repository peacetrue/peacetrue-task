package com.github.peacetrue.task.mybatis;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @param <Id>
 * @param <OperatorId>
 */
@Data
public class Task<Id, OperatorId> implements Serializable {

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

}