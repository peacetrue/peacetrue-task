package com.github.peacetrue.task.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiayx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskGetDTO<T> implements Serializable {
    private T id;
}
