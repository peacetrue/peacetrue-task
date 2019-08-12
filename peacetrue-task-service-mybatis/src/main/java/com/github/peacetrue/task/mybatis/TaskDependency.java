package com.github.peacetrue.task.mybatis;

import lombok.ToString;

import javax.annotation.Generated;
import java.util.Objects;

@ToString
public class TaskDependency<T> {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private T taskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private T dependentTaskId;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TaskDependency<?> that = (TaskDependency<?>) object;
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(dependentTaskId, that.dependentTaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, dependentTaskId);
    }

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