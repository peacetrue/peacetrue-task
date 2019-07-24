package com.github.peacetrue.task.executor;

import java.util.List;

/**
 * 任务依赖服务
 *
 * @author xiayx
 */
public interface TaskDependencyService {

    /** 获取任务依赖的其他任务 */
    List<Task> getDependent(Task task);

    /** 获取依赖于任务的其他任务 */
    List<Task> getDependOn(Task task);
}
