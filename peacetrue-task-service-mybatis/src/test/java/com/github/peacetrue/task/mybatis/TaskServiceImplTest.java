package com.github.peacetrue.task.mybatis;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.SolveQuestion;
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
        TaskAddDTO<Long, Long> findQuestion = new TaskAddDTO<>();
        findQuestion.setGroupId("solveQuestion");
        findQuestion.setName("findQuestion");
        findQuestion.setBody("@solveQuestion.findQuestion('安宁',1000)");
        findQuestion.setOperatorId(1L);

        TaskAddDTO<Long, Long> thinkPlan = new TaskAddDTO<>();
        thinkPlan.setGroupId("solveQuestion");
        thinkPlan.setName("thinkPlan");
        thinkPlan.setBody("@solveQuestion.thinkPlan(#outputs[0],1000)");
        thinkPlan.setOperatorId(1L);
        findQuestion.setDependOn(Collections.singletonList(thinkPlan));

        TaskAddDTO<Long, Long> execute = new TaskAddDTO<>();
        execute.setGroupId("solveQuestion");
        execute.setName("execute");
        execute.setBody("@solveQuestion.execute(#outputs[0],1000)");
        execute.setOperatorId(1L);
        thinkPlan.setDependOn(Collections.singletonList(execute));

        TaskVO add = taskService.add(findQuestion, true);
        System.out.println(add);
        Thread.sleep(5000L);
    }

    @Test
    public void addList() throws Exception {
        TaskAddDTO<Long, Long> dto = new TaskAddDTO<>();
        dto.setGroupId("solveQuestion");
        dto.setName("1");
        dto.setBody("@solveQuestion.findQuestion('安宁',1000)");
        dto.setInput("[1,2,3]");
        dto.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto1 = new TaskAddDTO<>();
        dto1.setGroupId("solveQuestion");
        dto1.setName("2");
        dto1.setBody("@solveQuestion.findQuestion('安宁',1000)");
        dto1.setInput("[1,2,3]");
        dto1.setOperatorId(1L);

        TaskAddDTO<Long, Long> dto2 = new TaskAddDTO<>();
        dto2.setGroupId("solveQuestion");
        dto2.setName("3");
        dto2.setBody("@solveQuestion.findQuestion('安宁',1000)");
        dto2.setInput("[1,2,3]");
        dto2.setOperatorId(1L);

        dto.setDependOn(Collections.singletonList(dto2));
        dto1.setDependOn(Collections.singletonList(dto2));
        List<TaskVO> add = taskService.add(Arrays.asList(dto, dto1));
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