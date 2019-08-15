package com.github.peacetrue.task.serialize;

import javax.annotation.Nullable;
import java.io.*;

/**
 * @author xiayx
 */
public class ByteSerializeService implements SerializeService<byte[]> {

    public static final ByteSerializeService DEFAULT = new ByteSerializeService();

    @Override
    public byte[] serialize(@Nullable Object source) {
        if (source == null) return null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(source);
            objectOutputStream.flush();
            objectOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("object[%s] serialize failed", source), e);
        }
    }

    @Override
    public Object deserialize(@Nullable byte[] source) {
        if (source == null) return null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("bytes[%s] serialize failed", source.length), e);
        }
    }


}
