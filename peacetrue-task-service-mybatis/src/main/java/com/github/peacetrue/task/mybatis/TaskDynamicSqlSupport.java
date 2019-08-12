package com.github.peacetrue.task.mybatis;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import javax.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;

public final class TaskDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final Task task = new Task();
    public static final Task task2 = new Task();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn id = task.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> groupId = task.groupId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = task.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> input = task.input;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> body = task.body;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> stateCode = task.stateCode;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> output = task.output;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> exception = task.exception;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn creatorId = task.creatorId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> createdTime = task.createdTime;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn modifierId = task.modifierId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> modifiedTime = task.modifiedTime;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> duration = task.duration;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class Task extends SqlTable {
        public final SqlColumn id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> groupId = column("group_id", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> input = column("input", JDBCType.VARCHAR);

        public final SqlColumn<String> body = column("body", JDBCType.VARCHAR);

        public final SqlColumn<String> stateCode = column("state_code", JDBCType.BIT);

        public final SqlColumn<String> output = column("output", JDBCType.VARCHAR);

        public final SqlColumn<String> exception = column("exception", JDBCType.VARCHAR);

        public final SqlColumn creatorId = column("creator_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> createdTime = column("created_time", JDBCType.TIMESTAMP);

        public final SqlColumn modifierId = column("modifier_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> modifiedTime = column("modified_time", JDBCType.TIMESTAMP);

        public final SqlColumn<Long> duration = column("duration", JDBCType.LONGVARCHAR);

        public Task() {
            super("task");
        }
    }
}