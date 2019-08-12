package com.github.peacetrue.task.mybatis;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.TaskExecutorAutoConfiguration;
import com.github.peacetrue.task.service.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xiayx
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        DataSourceAutoConfiguration.class,
        MybatisAutoConfiguration.class,
        MybatisTaskAutoConfiguration.class,
        PageHelperAutoConfiguration.class,
        TaskExecutorAutoConfiguration.class,
        SolveQuestion.class
}, properties = "logging.level.com.github.peacetrue=debug")
@ActiveProfiles("datasource")
public class TaskServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TaskService taskService;

    @Test
    public void add() throws Exception {
        TaskAddDTO<Long, Long> dto = new TaskAddDTO<>();
        dto.setGroupId("Order-1-uploadVideo");
        dto.setName("1");
        dto.setBody("@solveQuestion.submitQuestion(1000)");
        dto.setInput("[1,2,3]");
        dto.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto1 = new TaskAddDTO<>();
        dto1.setGroupId("Order-1-uploadVideo");
        dto1.setName("2");
        dto1.setBody("@solveQuestion.submitQuestion(1000)");
        dto1.setInput("[1,2,3]");
        dto1.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto2 = new TaskAddDTO<>();
        dto2.setGroupId("Order-1-uploadVideo");
        dto2.setName("3");
        dto2.setBody("@solveQuestion.submitQuestion(1000)");
        dto2.setInput("[1,2,3]");
        dto2.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto3 = new TaskAddDTO<>();
        dto3.setGroupId("Order-1-uploadVideo");
        dto3.setName("4");
        dto3.setBody("@solveQuestion.submitQuestion(1000)");
        dto3.setInput("[1,2,3]");
        dto3.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto4 = new TaskAddDTO<>();
        dto4.setGroupId("Order-1-uploadVideo");
        dto4.setName("4");
        dto4.setBody("@solveQuestion.submitQuestion(1000)");
        dto4.setInput("[1,2,3]");
        dto4.setOperatorId(1L);

//        dto.setExecute(true);
        dto.setDependOn(Arrays.asList(dto1, dto2));
        dto1.setDependOn(Collections.singletonList(dto3));
        dto2.setDependOn(Collections.singletonList(dto4));
        TaskVO add = taskService.add(dto);
        System.out.println(add);
        Thread.sleep(5000L);
    }

    @Test
    public void addList() throws Exception {
        TaskAddDTO<Long, Long> dto = new TaskAddDTO<>();
        dto.setGroupId("Order-1-uploadVideo");
        dto.setName("haha");
        dto.setBody("@solveQuestion.submitQuestion(1000)");
        dto.setInput("[1,2,3]");
        dto.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto1 = new TaskAddDTO<>();
        dto1.setGroupId("Order-1-uploadVideo");
        dto1.setName("haha");
        dto1.setBody("@solveQuestion.submitQuestion(1000)");
        dto1.setInput("[1,2,3]");
        dto1.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto2 = new TaskAddDTO<>();
        dto2.setGroupId("Order-1-uploadVideo");
        dto2.setName("haha");
        dto2.setBody("@solveQuestion.submitQuestion(1000)");
        dto2.setInput("[1,2,3]");
        dto2.setOperatorId(1L);

        dto.setDependOn(Collections.singletonList(dto2));
        dto1.setDependOn(Collections.singletonList(dto2));
//        dto.setExecute(true);
        TaskVO add = taskService.add(dto);
        System.out.println(add);
        Thread.sleep(5000L);
    }

    @Test
    public void query() throws Exception {
        Page<TaskVO> page = taskService.query(new TaskQueryParams(), new PageRequest(0, 10));
        logger.info("page.getContent():{}", page.getContent());
        Assert.assertEquals(2, page.getTotalElements());
    }


    @Test
    public void get() throws Exception {
        TaskVO taskVO = taskService.getById(1L);
        Assert.assertEquals(1L, taskVO.getId());
    }

    @Test
    public void getDependent() throws Exception {
        List<TaskVO> vos = taskService.getDependent(1L);
        Assert.assertEquals(0, vos.size());

        vos = taskService.getDependent(2L);
        Assert.assertEquals(1, vos.size());
    }

    @Test
    public void getDependOn() throws Exception {
        List<TaskVO> vos = taskService.getDependOn(1L);
        Assert.assertEquals(1L, vos.size());
    }

    @Test
    public void updateStateDoing() throws Exception {
        taskService.updateStateDoing(new TaskDoingDTO<>(1L, null));
    }

    @Test
    public void execute() throws Exception {
        TaskVO byId = taskService.getById(1L);
        TaskExecuteDTO executeDTO = BeanUtils.map(byId, TaskExecuteDTO.class);
        taskService.execute(executeDTO);
        Thread.sleep(1000L * 5);
    }

}