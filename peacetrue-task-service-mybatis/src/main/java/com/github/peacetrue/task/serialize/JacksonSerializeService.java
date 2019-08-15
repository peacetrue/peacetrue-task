package com.github.peacetrue.task.serialize;

import com.github.peacetrue.jackson.ObjectMapperWrapper;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author xiayx
 */
public class JacksonSerializeService implements SerializeService<String> {

    private ObjectMapperWrapper wrapper;

    public JacksonSerializeService(ObjectMapperWrapper wrapper) {
        this.wrapper = Objects.requireNonNull(wrapper);
    }

    @Override
    public String serialize(@Nullable Object source) {
        return wrapper.writeAppendType(source);
    }

    @Override
    public Object deserialize(@Nullable String value) {
        return wrapper.readDetectType(value);
    }
}
