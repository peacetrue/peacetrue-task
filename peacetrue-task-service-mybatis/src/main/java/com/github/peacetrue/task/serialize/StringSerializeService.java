package com.github.peacetrue.task.serialize;

import javax.annotation.Nullable;
import java.util.Base64;
import java.util.Objects;

/**
 * @author xiayx
 */
public class StringSerializeService implements SerializeService<String> {

    public static final StringSerializeService DEFAULT = new StringSerializeService();

    private SerializeService<byte[]> byteSerializeService;

    public StringSerializeService() {
        this(ByteSerializeService.DEFAULT);
    }

    public StringSerializeService(SerializeService<byte[]> byteSerializeService) {
        this.byteSerializeService = Objects.requireNonNull(byteSerializeService);
    }

    @Nullable
    @Override
    public String serialize(@Nullable Object source) {
        byte[] bytes = byteSerializeService.serialize(source);
        if (bytes == null) return null;
        return new String(Base64.getEncoder().encode(bytes));
    }

    @Nullable
    @Override
    public Object deserialize(@Nullable String source) {
        if (source == null) return null;
        return byteSerializeService.deserialize(Base64.getDecoder().decode(source));
    }
}
