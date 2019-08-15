package com.github.peacetrue.task.serialize;

import com.github.peacetrue.task.executor.Question;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xiayx
 */
public class StringSerializeServiceTest {

    @Test
    public void deserialize() {
        Question question = new Question("你好");
        String target = StringSerializeService.DEFAULT.serialize(question);
        Assert.assertNotNull(target);

        Object question2 = StringSerializeService.DEFAULT.deserialize(target);
        Assert.assertEquals(question, question2);

    }
}