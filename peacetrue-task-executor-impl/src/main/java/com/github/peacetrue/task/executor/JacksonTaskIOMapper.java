package com.github.peacetrue.task.executor;

import com.github.peacetrue.jackson.ObjectMapperWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;

/**
 * @author xiayx
 */
public class JacksonTaskIOMapper implements TaskIOMapper {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapperWrapper wrapper;

    @Override
    public String writeObject(Task task, @Nullable Object value) {
        logger.info("写出任务[{}]的信息[{}]", task, value);
        return wrapper.writeAppendType(value);
    }

    @Override
    public Object readObject(Task task, String value) {
        logger.info("读取任务[{}]的信息[{}]", task, value);
        return wrapper.readDetectType(value);
    }
}
