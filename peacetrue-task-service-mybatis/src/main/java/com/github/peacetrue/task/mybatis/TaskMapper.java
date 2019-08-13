package com.github.peacetrue.task.mybatis;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.delete.MyBatis3DeleteModelAdapter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.MyBatis3UpdateModelAdapter;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.peacetrue.task.mybatis.TaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface TaskMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    long count(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "record.id")
    int insert(InsertStatementProvider<Task> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("TaskResult")
    Task selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "TaskResult", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "group_id", property = "groupId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
            @Result(column = "input", property = "input", jdbcType = JdbcType.VARCHAR),
            @Result(column = "body", property = "body", jdbcType = JdbcType.VARCHAR),
            @Result(column = "state_code", property = "stateCode", jdbcType = JdbcType.BIT),
            @Result(column = "output", property = "output", jdbcType = JdbcType.VARCHAR),
            @Result(column = "exception", property = "exception", jdbcType = JdbcType.VARCHAR),
            @Result(column = "duration", property = "duration", jdbcType = JdbcType.LONGVARCHAR),
            @Result(column = "creator_id", property = "creatorId"),
            @Result(column = "created_time", property = "createdTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modifier_id", property = "modifierId"),
            @Result(column = "modified_time", property = "modifiedTime", jdbcType = JdbcType.TIMESTAMP),
    })
    List<Task> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(task);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, task);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Object id_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, task)
                .where((SqlColumn<Object>) id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(Task record) {
        return insert(SqlBuilder.insert(record)
                .into(task)
                .map(id).toProperty("id")
                .map(groupId).toProperty("groupId")
                .map(name).toProperty("name")
                .map(input).toProperty("input")
                .map(body).toProperty("body")
                .map(stateCode).toProperty("stateCode")
                .map(output).toProperty("output")
                .map(exception).toProperty("exception")
                .map(creatorId).toProperty("creatorId")
                .map(createdTime).toProperty("createdTime")
                .map(modifierId).toProperty("modifierId")
                .map(modifiedTime).toProperty("modifiedTime")
                .map(duration).toProperty("duration")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(Task record) {
        return insert(SqlBuilder.insert(record)
                .into(task)
                .map((SqlColumn<Object>) id).toPropertyWhenPresent("id", record::getId)
                .map(groupId).toPropertyWhenPresent("groupId", record::getGroupId)
                .map(name).toPropertyWhenPresent("name", record::getName)
                .map(input).toPropertyWhenPresent("input", record::getInput)
                .map(body).toPropertyWhenPresent("body", record::getBody)
                .map(stateCode).toPropertyWhenPresent("stateCode", record::getStateCode)
                .map(output).toPropertyWhenPresent("output", record::getOutput)
                .map(exception).toPropertyWhenPresent("exception", record::getException)
                .map((SqlColumn<Object>) creatorId).toPropertyWhenPresent("creatorId", record::getCreatorId)
                .map(createdTime).toPropertyWhenPresent("createdTime", record::getCreatedTime)
                .map((SqlColumn<Object>) modifierId).toPropertyWhenPresent("modifierId", record::getModifierId)
                .map(modifiedTime).toPropertyWhenPresent("modifiedTime", record::getModifiedTime)
                .map(duration).toPropertyWhenPresent("duration", record::getDuration)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Task>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, id, groupId, name, input, body, stateCode, output, exception, creatorId, createdTime, modifierId, modifiedTime, duration)
                .from(task);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<Task>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, id, groupId, name, input, body, stateCode, output, exception, creatorId, createdTime, modifierId, modifiedTime, duration)
                .from(task);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Task selectByPrimaryKey(Object id_) {
        return SelectDSL.selectWithMapper(this::selectOne, id, groupId, name, input, body, stateCode, output, exception, creatorId, createdTime, modifierId, modifiedTime, duration)
                .from(task)
                .where((SqlColumn<Object>) id, isEqualTo(id_))
                .build()
                .execute();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(Task record) {
        return UpdateDSL.updateWithMapper(this::update, task)
                .set((SqlColumn<Object>) id).equalTo(record::getId)
                .set(groupId).equalTo(record::getGroupId)
                .set(name).equalTo(record::getName)
                .set(input).equalTo(record::getInput)
                .set(body).equalTo(record::getBody)
                .set(stateCode).equalTo(record::getStateCode)
                .set(output).equalTo(record::getOutput)
                .set(exception).equalTo(record::getException)
                .set((SqlColumn<Object>) creatorId).equalTo(record::getCreatorId)
                .set(createdTime).equalTo(record::getCreatedTime)
                .set((SqlColumn<Object>) modifierId).equalTo(record::getModifierId)
                .set(modifiedTime).equalTo(record::getModifiedTime)
                .set(duration).equalTo(record::getDuration);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(Task record) {
        return UpdateDSL.updateWithMapper(this::update, task)
                .set((SqlColumn<Object>) id).equalToWhenPresent(record::getId)
                .set(groupId).equalToWhenPresent(record::getGroupId)
                .set(name).equalToWhenPresent(record::getName)
                .set(input).equalToWhenPresent(record::getInput)
                .set(body).equalToWhenPresent(record::getBody)
                .set(stateCode).equalToWhenPresent(record::getStateCode)
                .set(output).equalToWhenPresent(record::getOutput)
                .set(exception).equalToWhenPresent(record::getException)
                .set((SqlColumn<Object>) creatorId).equalToWhenPresent(record::getCreatorId)
                .set(createdTime).equalToWhenPresent(record::getCreatedTime)
                .set((SqlColumn<Object>) modifierId).equalToWhenPresent(record::getModifierId)
                .set(modifiedTime).equalToWhenPresent(record::getModifiedTime)
                .set(duration).equalToWhenPresent(record::getDuration);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(Task record) {
        return UpdateDSL.updateWithMapper(this::update, task)
                .set(groupId).equalTo(record::getGroupId)
                .set(name).equalTo(record::getName)
                .set(input).equalTo(record::getInput)
                .set(body).equalTo(record::getBody)
                .set(stateCode).equalTo(record::getStateCode)
                .set(output).equalTo(record::getOutput)
                .set(exception).equalTo(record::getException)
                .set((SqlColumn<Object>) creatorId).equalTo(record::getCreatorId)
                .set(createdTime).equalTo(record::getCreatedTime)
                .set((SqlColumn<Object>) modifierId).equalTo(record::getModifierId)
                .set(modifiedTime).equalTo(record::getModifiedTime)
                .set(duration).equalTo(record::getDuration)
                .where((SqlColumn<Object>) id, isEqualTo(record::getId))
                .build()
                .execute();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(Task record) {
        return UpdateDSL.updateWithMapper(this::update, task)
                .set(groupId).equalToWhenPresent(record::getGroupId)
                .set(name).equalToWhenPresent(record::getName)
                .set(input).equalToWhenPresent(record::getInput)
                .set(body).equalToWhenPresent(record::getBody)
                .set(stateCode).equalToWhenPresent(record::getStateCode)
                .set(output).equalToWhenPresent(record::getOutput)
                .set(exception).equalToWhenPresent(record::getException)
                .set((SqlColumn<Object>) creatorId).equalToWhenPresent(record::getCreatorId)
                .set(createdTime).equalToWhenPresent(record::getCreatedTime)
                .set((SqlColumn<Object>) modifierId).equalToWhenPresent(record::getModifierId)
                .set(modifiedTime).equalToWhenPresent(record::getModifiedTime)
                .set(duration).equalToWhenPresent(record::getDuration)
                .where((SqlColumn<Object>) id, isEqualTo(record::getId))
                .build()
                .execute();
    }


    //append
    @SuppressWarnings("unchecked")
    default <T> List<Task> selectById(Collection<T> ids) {
        return selectByExample().where((SqlColumn<T>) task.id, SqlBuilder.isIn(new ArrayList<>(ids))).build().execute();
    }

    @SuppressWarnings("unchecked")
    default <T> List<Task> selectGroupById(T id) {
        return selectByExample()
                .join(task2, "task2").on(task2.groupId, SqlBuilder.equalTo(task.groupId))
                .where((SqlColumn<T>) task2.id, SqlBuilder.isEqualTo(id))
                .orderBy(createdTime)
                .build().execute();
    }

    default List<Task> selectByGroupId(String groupId_) {
        return selectByExample()
                .where(groupId, SqlBuilder.isEqualTo(groupId_))
                .orderBy(createdTime)
                .build().execute();
    }

}