package com.github.peacetrue.task.mybatis;

import org.apache.ibatis.annotations.*;
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

import static com.github.peacetrue.task.mybatis.TaskDependencyDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface TaskDependencyMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    long count(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    int insert(InsertStatementProvider<TaskDependency> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "TaskDependencyResult", value = {
            @Result(column = "task_id", property = "taskId", id = true),
            @Result(column = "dependent_task_id", property = "dependentTaskId", id = true)
    })
    List<TaskDependency> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>> countByExample() {
        return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())
                .from(taskDependency);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default DeleteDSL<MyBatis3DeleteModelAdapter<Integer>> deleteByExample() {
        return DeleteDSL.deleteFromWithMapper(this::delete, taskDependency);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Object taskId_, Object dependentTaskId_) {
        return DeleteDSL.deleteFromWithMapper(this::delete, taskDependency)
                .where((SqlColumn<Object>) taskId, isEqualTo(taskId_))
                .and((SqlColumn<Object>) dependentTaskId, isEqualTo((Object) dependentTaskId_))
                .build()
                .execute();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(TaskDependency record) {
        return insert(SqlBuilder.insert(record)
                .into(taskDependency)
                .map(taskId).toProperty("taskId")
                .map(dependentTaskId).toProperty("dependentTaskId")
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(TaskDependency record) {
        return insert(SqlBuilder.insert(record)
                .into(taskDependency)
                .map(taskId).toPropertyWhenPresent("taskId", record::getTaskId)
                .map(dependentTaskId).toPropertyWhenPresent("dependentTaskId", record::getDependentTaskId)
                .build()
                .render(RenderingStrategy.MYBATIS3));
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<TaskDependency>>> selectByExample() {
        return SelectDSL.selectWithMapper(this::selectMany, taskId, dependentTaskId)
                .from(taskDependency);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default QueryExpressionDSL<MyBatis3SelectModelAdapter<List<TaskDependency>>> selectDistinctByExample() {
        return SelectDSL.selectDistinctWithMapper(this::selectMany, taskId, dependentTaskId)
                .from(taskDependency);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExample(TaskDependency record) {
        return UpdateDSL.updateWithMapper(this::update, taskDependency)
                .set((SqlColumn<Object>) taskId).equalTo(record::getTaskId)
                .set((SqlColumn<Object>) dependentTaskId).equalTo(record::getDependentTaskId);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default UpdateDSL<MyBatis3UpdateModelAdapter<Integer>> updateByExampleSelective(TaskDependency record) {
        return UpdateDSL.updateWithMapper(this::update, taskDependency)
                .set(taskId).equalToWhenPresent(record::getTaskId)
                .set(dependentTaskId).equalToWhenPresent(record::getDependentTaskId);
    }

    //append

    @SuppressWarnings("unchecked")
    default <T> List<TaskDependency> selectByTaskId(Collection<T> taskIds) {
        return selectByExample().where((SqlColumn<T>) taskDependency.taskId, SqlBuilder.isIn(new ArrayList<>(taskIds))).build().execute();
    }

    @SuppressWarnings("unchecked")
    default <T> List<TaskDependency> selectByTaskId(T taskId) {
        return selectByExample().where((SqlColumn<T>) taskDependency.taskId, SqlBuilder.isEqualTo(taskId)).build().execute();
    }

    @SuppressWarnings("unchecked")
    default <T> List<TaskDependency> selectByDependentTaskId(T dependentTaskId) {
        return selectByExample().where((SqlColumn<T>) taskDependency.dependentTaskId, SqlBuilder.isEqualTo(dependentTaskId)).build().execute();
    }
}