package com.github.peacetrue.task.mybatis;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
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
})
@ActiveProfiles("datasource")
public class TaskServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TaskService taskService;

    @Test
    public void add() throws Exception {
        TaskAddDTO<Long, Long> dto = new TaskAddDTO<>();
        dto.setGroupId("Order-1-uploadVideo");
        dto.setBody("@");
        dto.setInput("[1,2,3]");
        dto.setOperatorId(1L);
        System.out.println(taskService.add(dto));
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

}