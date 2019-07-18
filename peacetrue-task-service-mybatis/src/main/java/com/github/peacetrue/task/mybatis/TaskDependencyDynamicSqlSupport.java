package com.github.peacetrue.task.mybatis;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import javax.annotation.Generated;

public final class TaskDependencyDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final TaskDependency taskDependency = new TaskDependency();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn taskId = taskDependency.taskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn dependentTaskId = taskDependency.dependentTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class TaskDependency extends SqlTable {
        public final SqlColumn taskId = column("task_id");

        public final SqlColumn dependentTaskId = column("dependent_task_id");

        public TaskDependency() {
            super("task_dependency");
        }
    }
}