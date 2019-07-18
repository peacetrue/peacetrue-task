package com.github.peacetrue.task.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Base64;

/**
 * @author xiayx
 */
public class JavaSerializeTaskIOMapper implements TaskIOMapper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String writeObject(Task task, @Nullable Object value) {
        logger.info("写出任务[{}]的输出信息[{}]", task, value);
        if (value == null) return null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            objectOutputStream.flush();
            objectOutputStream.close();
            return new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            throw new TaskWriteException(e, task, value);
        }
    }

    @Override
    public Object readObject(Task task, String value) {
        logger.info("读取任务[{}]的输入或输出信息[{}]", task, value);
        if (value == null) return null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(value));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new TaskReadException(e, task, value);
        }
    }


}
