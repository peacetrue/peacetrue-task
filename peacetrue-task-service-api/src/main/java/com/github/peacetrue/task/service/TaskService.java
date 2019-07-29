package com.github.peacetrue.task.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 任务服务
 *
 * @author xiayx
 */
public interface TaskService {

    /** 添加任务 */
    TaskVO add(TaskAddDTO task);

    /** 分页查询任务信息 */
    Page<TaskVO> query(TaskQueryParams queryParams, Pageable pageable);

    /** 获取单个任务 */
    @Nullable
    TaskVO get(TaskGetDTO dto);

    /** 获取必须的单个任务 */
    TaskVO getRequired(TaskGetDTO dto);

    /** 获取单个任务根据主键 */
    default TaskVO getById(Object id) {
        return get(new TaskGetDTO<>(id));
    }

    /** 获取必须的单个任务根据主键 */
    default TaskVO getRequiredById(Object id) {
        return getRequired(new TaskGetDTO<>(id));
    }

    /** 获取任务集合根据主键集合 */
    List<TaskVO> getById(List<?> id);

    /** 获取当前任务依赖的其他任务 */
    List<TaskVO> getDependent(Object id);

    /** 获取依赖于当前任务的其他任务 */
    List<TaskVO> getDependOn(Object id);

    /** 更新任务状态为进行中 */
    void updateStateDoing(TaskDoingDTO dto);

    /** 更新任务状态为执行成功 */
    void updateStateSuccess(TaskSuccessDTO dto);

    /** 更新任务状态为执行失败 */
    void updateStateFailure(TaskFailureDTO dto);

    /** 执行任务 */
    void execute(TaskIdExecuteDTO dto);

    /** 执行任务 */
    void execute(TaskExecuteDTO dto);

}
