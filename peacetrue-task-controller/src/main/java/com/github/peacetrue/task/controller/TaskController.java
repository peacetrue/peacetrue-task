package com.github.peacetrue.task.controller;

import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author xiayx
 */
@RequestMapping
public class TaskController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TaskService taskService;

    @ResponseBody
    @PostMapping(value = "${peacetrue.task.add-url}")
    public TaskVO add(TaskAddDTO dto, boolean execute) {
        logger.info("新增任务[{}]", dto);
        return taskService.add(BeanUtils.map(dto, TaskAddDTO.class), execute);
    }

    @ResponseBody
    @GetMapping(value = "${peacetrue.task.query-url}", params = "page")
    public Page<TaskVO> query(TaskQueryParams params,
                              @PageableDefault(sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("分页查询任务信息[{}]", params);
        return taskService.query(params, pageable);
    }

    @ResponseBody
    @GetMapping(value = "${peacetrue.task.get-url}", params = {"!page"})
    public TaskVO get(TaskGetDTO dto) {
        logger.info("获取任务[{}]的详情", dto);
        return taskService.get(BeanUtils.map(dto, TaskGetDTO.class));
    }

    @ResponseBody
    @PostMapping(value = "${peacetrue.task.execute-url}", params = "id")
    public void execute(TaskIdExecuteDTO dto) {
        logger.info("执行单个任务[{}]", dto);
        taskService.execute(BeanUtils.map(dto, TaskIdExecuteDTO.class));
    }

    @ResponseBody
    @PostMapping(value = "${peacetrue.task.execute-url}", params = "groupId")
    public void execute(TaskGroupIdExecuteDTO dto) {
        logger.info("执行一组任务[{}]", dto);
        taskService.execute(BeanUtils.map(dto, TaskGroupIdExecuteDTO.class));
    }

}
