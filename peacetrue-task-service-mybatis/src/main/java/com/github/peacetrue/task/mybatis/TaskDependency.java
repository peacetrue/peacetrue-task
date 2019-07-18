package com.github.peacetrue.task.mybatis;

import lombok.ToString;

import javax.annotation.Generated;

@ToString
public class TaskDependency<T> {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private T taskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private T dependentTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public T getTaskId() {
        return taskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setTaskId(T taskId) {
        this.taskId = taskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public T getDependentTaskId() {
        return dependentTaskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setDependentTaskId(T dependentTaskId) {
        this.dependentTaskId = dependentTaskId;
    }
}