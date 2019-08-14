package com.github.peacetrue.task.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author xiayx
 */
public class JavaSerializeTaskIOMapperTest {

    private JavaSerializeTaskIOMapper mapper = new JavaSerializeTaskIOMapper();

    @Test
    public void writeObject() {
        String value = mapper.writeObject(new TaskImpl(), new Question());
        System.out.println(value);
    }


    @Test
    public void readObject() {
        Question question = new Question();
        String value = mapper.writeObject(new TaskImpl(), question);
        Object object = mapper.readObject(new TaskImpl(), value);
        assertEquals(question, object);
    }

    @Test
    public void testDeserialize() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        BigInteger bi = new BigInteger("0");
        oos.writeObject(bi);
        byte[] str = baos.toByteArray();
        String string = new String(Base64.getEncoder().encode(str));
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(string))));
        Object obj = ois.readObject();
        assertNotNull(obj);
        assertEquals(obj.getClass().getName(), "java.math.BigInteger");
        assertEquals(((BigInteger) obj).intValue(), 0);
    }


    @Test
    public void name() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString("aa");
        Object o = objectMapper.readValue(s, Object.class);
        o = objectMapper.readValue("[]", Object.class);
        o = objectMapper.readValue("[\"1\"]", Object.class);
        o = objectMapper.readValue("[1]", Object.class);
        o = objectMapper.readValue("[100000000]", Object.class);
        o = objectMapper.readValue("[{}]", Object.class);
        System.out.println(o);
    }

    @Test
    public void spel() {
        ExpressionParser parser = new SpelExpressionParser();
        HashMap<Object, Object> rootObject = new HashMap<>();
        rootObject.put("a", 1);
        StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
        Object value = parser.parseExpression("#root.size()+get('a')").getValue(context);
        System.out.println("value:" + value);
    }
}