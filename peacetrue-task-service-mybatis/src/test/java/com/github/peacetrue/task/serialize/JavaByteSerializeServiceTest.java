package com.github.peacetrue.task.serialize;

import com.github.peacetrue.task.executor.Question;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xiayx
 */
public class JavaByteSerializeServiceTest {

    @Test
    public void serialize() {
        Question question = new Question("你好");
        byte[] bytes = ByteSerializeService.DEFAULT.serialize(question);
        Assert.assertNotNull(bytes);
    }

    @Test
    public void deserialize() {
        Question question = new Question("你好");
        byte[] bytes = ByteSerializeService.DEFAULT.serialize(question);
        Assert.assertNotNull(bytes);

        Object target = ByteSerializeService.DEFAULT.deserialize(bytes);
        Assert.assertEquals(question, target);
    }
}