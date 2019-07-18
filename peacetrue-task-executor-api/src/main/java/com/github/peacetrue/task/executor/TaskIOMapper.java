package com.github.peacetrue.task.executor;

import javax.annotation.Nullable;

/**
 * 任务输入输出信息转换器
 *
 * @author xiayx
 */
public interface TaskIOMapper {

    /** 写出对象 */
    String writeObject(Task task, @Nullable Object value) throws TaskWriteException;

    /** 读取对象 */
    Object readObject(Task task, @Nullable String value) throws TaskReadException;
}
