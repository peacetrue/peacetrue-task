package com.github.peacetrue.task.serialize;

import javax.annotation.Nullable;

/**
 * 序列化服务
 *
 * @author xiayx
 */
public interface SerializeService<T> {

    /** 写出对象 */
    @Nullable
    T serialize(@Nullable Object source);

    /** 读取对象 */
    @Nullable
    Object deserialize(@Nullable T source);
}
