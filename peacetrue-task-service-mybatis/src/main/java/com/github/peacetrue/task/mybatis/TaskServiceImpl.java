package com.github.peacetrue.task.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.peacetrue.associate.AssociateUtils;
import com.github.peacetrue.associate.AssociatedSourceBuilder;
import com.github.peacetrue.flow.Tense;
import com.github.peacetrue.mybatis.dynamic.MybatisDynamicUtils;
import com.github.peacetrue.pagehelper.PageHelperUtils;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.*;
import com.github.peacetrue.task.service.*;
import com.github.peacetrue.util.EntityNotFoundException;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.peacetrue.associate.AssociatedSourceBuilderUtils.getPropertyValue;
import static com.github.peacetrue.task.mybatis.TaskDynamicSqlSupport.*;

/**
 * @author xiayx
 */
@SuppressWarnings("unchecked")
public class TaskServiceImpl implements TaskService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskDependencyMapper taskDependencyMapper;
    @Autowired
    private AssociatedSourceBuilder associatedSourceBuilder;

    @Transactional
    public TaskVO add(TaskAddDTO dto) {
        logger.info("新增任务: {}", dto);

        Task task = BeanUtils.map(dto, Task.class);
        task.setStateCode(Tense.TODO.getCode());
        task.setCreatorId(dto.getOperatorId());
        task.setCreatedTime(new Date());
        task.setModifierId(dto.getOperatorId());
        task.setModifiedTime(task.getCreatedTime());
        logger.debug("保存任务信息[{}]", task);
        int count = taskMapper.insert(task);
        logger.debug("共影响{}行记录", count);

        if (dto.getDependentIds() != null) {
            logger.debug("保存任务的依赖信息[{}]", dto.getDependentIds());
            dto.getDependentIds().forEach(dependentId -> {
                TaskDependency record = new TaskDependency();
                record.setTaskId(task.getId());
                record.setDependentTaskId(dependentId);
                taskDependencyMapper.insert(record);
            });
        }

        TaskVO taskVO = BeanUtils.map(task, TaskVO.class);
        taskVO.setDependentIds(dto.getDependentIds());
        return taskVO;
    }

    @Override
    public Page<TaskVO> query(TaskQueryParams params, Pageable pageable) {
        logger.info("分页查询任务[{}]信息", params);

        if (params == null) params = new TaskQueryParams();
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<Task> entities = taskMapper.selectByExample()
                .where(groupId, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(params.getGroupId())))
                .and((SqlColumn<Object>) id, SqlBuilder.isLikeWhenPresent(params.getId()))
                .and(body, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(params.getBody())))
                .and(input, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(params.getInput())))
                .and(stateCode, SqlBuilder.isEqualToWhenPresent(params.getStateCode()))
                .and(output, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(params.getOutput())))
                .and(createdTime, SqlBuilder.isBetweenWhenPresent(params.getCreatedTime().getLowerBound()).and(params.getCreatedTime().getUpperBound()))
                .orderBy(MybatisDynamicUtils.orders(task, pageable.getSort()))
                .build().execute();
        logger.debug("共取得'{}'条记录", entities.size());
        if (entities.isEmpty()) return new PageImpl<>(Collections.emptyList());

        List<TaskVO> vos = BeanUtils.replaceAsList(entities, TaskVO.class);
        this.setDependentIds(vos);
        return new PageImpl<>(vos, pageable, PageHelperUtils.getTotal(entities));
    }

    private void setDependentIds(List<TaskVO> vos) {
        AssociateUtils.setCollectionAssociate(vos, "dependentIds",
                associatedSourceBuilder.buildCollectionAssociatedSource(
                        taskDependencyMapper::selectByTaskId,
                        getPropertyValue("taskId"),
                        getPropertyValue("dependentTaskId")),
                "id");
    }

    private Optional<TaskVO> getTask(Object id) {
        logger.info("获取任务[{}]的详情", id);
        return Optional.ofNullable(taskMapper.selectByPrimaryKey(id))
                .map(task -> BeanUtils.map(task, TaskVO.class));
    }

    @Override
    public TaskVO get(TaskGetDTO dto) {
        return getTask(dto.getId()).orElse(null);
    }

    @Override
    public TaskVO getRequired(TaskGetDTO dto) {
        return getRequiredById(dto.getId());
    }

    public TaskVO getRequiredById(Object id) {
        return getTask(id).orElseThrow(() -> new EntityNotFoundException(TaskVO.class, "id", id));
    }

    @Override
    public List<TaskVO> getById(List<?> ids) {
        logger.info("获取任务[{}]的详情", ids);
        return BeanUtils.replaceAsList(taskMapper.selectById(ids), TaskVO.class);
    }

    @Override
    public List<TaskVO> getDependent(Object id) {
        logger.info("获取当前任务[{}]依赖的其他任务", id);
        List<TaskDependency> dependencies = taskDependencyMapper.selectByTaskId(id);
        logger.info("取得当前任务[{}]依赖的其他任务[{}]", id, dependencies);
        if (dependencies.isEmpty()) return Collections.emptyList();
        return getById(dependencies.stream().map(TaskDependency::getDependentTaskId).collect(Collectors.toList()));
    }

    @Override
    public List<TaskVO> getDependOn(Object id) {
        logger.info("获取依赖于当前任务[{}]的其他任务", id);
        List<TaskDependency> dependencies = taskDependencyMapper.selectByDependentTaskId(id);
        if (dependencies.isEmpty()) return Collections.emptyList();
        logger.info("取得依赖于当前任务[{}]的其他任务", dependencies);
        return getById(dependencies.stream().map(TaskDependency::getTaskId).collect(Collectors.toList()));
    }

    @Autowired(required = false)
    private TaskExecutor taskExecutor;

    @Override
    public void execute(TaskExecuteDTO dto) {
        logger.info("执行任务[{}]", dto);
        if (taskExecutor == null) {
            logger.warn("尚未配置任务执行器，无法执行任务");
            return;
        }
        TaskVO taskVO = getRequiredById(dto.getId());
        if (taskVO.getStateCode().equals(Tense.DOING.getCode())) {
            throw new TaskExecuteException("任务正在执行中，请勿重复执行");
        }
        RealTimeTask impl = BeanUtils.map(taskVO, RealTimeTask.class);
        impl.setTaskService(this);
        impl.setOperatorId(dto.getOperatorId());
        impl.setExecuted(new HashSet<>());
        taskExecutor.execute(impl);
    }

    @EventListener
    public void handleTaskStarted(TaskStartedEvent event) {
        RealTimeTask source = (RealTimeTask) event.getSource();
        TaskDoingDTO dto = new TaskDoingDTO();
        dto.setId(source.getId());
        dto.setOperatorId(source.getOperatorId());
        dto.setRemark("执行任务");
        this.updateStateDoing(dto, source);
    }

    @EventListener
    public void handleTaskSucceeded(TaskSucceededEvent event) {
        RealTimeTask source = (RealTimeTask) event.getSource();
        source.getExecuted().add(source);
        TaskSuccessDTO dto = new TaskSuccessDTO();
        dto.setId(source.getId());
        dto.setOperatorId(source.getOperatorId());
        dto.setOutput(source.getOutput());
        dto.setDuration(source.getDuration());
        dto.setRemark("执行任务");
        this.updateStateSuccess(dto, source);
    }

    @EventListener
    public void handleTaskFailed(TaskFailedEvent event) {
        RealTimeTask source = (RealTimeTask) event.getSource();
        source.getExecuted().add(source);
        TaskFailureDTO dto = new TaskFailureDTO();
        dto.setId(source.getId());
        dto.setDuration(source.getDuration());
        dto.setOperatorId(source.getOperatorId());
        dto.setException(event.getThrowable().getMessage());
        dto.setRemark("执行任务");
        this.updateStateFailure(dto, source);
    }

    @Override
    @Transactional
    public void updateStateDoing(TaskDoingDTO dto) {
        TaskVO task = this.getRequiredById(dto.getId());
        updateStateDoing(dto, task);
    }

    private void updateStateDoing(TaskDoingDTO dto, TaskVO task) {
        logger.info("更新任务[{}]的状态为[进行中]", dto.getId());
        Task update = new Task();
        update.setId(task.getId());
        update.setStateCode(Tense.DOING.getCode());
        update.setModifierId(dto.getOperatorId());
        update.setModifiedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    @Transactional
    public void updateStateSuccess(TaskSuccessDTO dto) {
        TaskVO task = this.getRequiredById(dto.getId());
        updateStateSuccess(dto, task);
    }

    private void updateStateSuccess(TaskSuccessDTO dto, TaskVO task) {
        logger.info("更新任务[{}]的状态为[执行成功]", dto.getId());

        Task update = new Task();
        update.setId(task.getId());
        update.setStateCode(Tense.SUCCESS.getCode());
        update.setOutput(dto.getOutput());
        update.setDuration(dto.getDuration());
        update.setModifierId(dto.getOperatorId());
        update.setModifiedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    @Transactional
    public void updateStateFailure(TaskFailureDTO dto) {
        TaskVO task = this.getRequiredById(dto.getId());
        updateStateFailure(dto, task);
    }

    private void updateStateFailure(TaskFailureDTO dto, TaskVO task) {
        logger.info("更新任务[{}]的状态为[执行失败]", dto.getId());

        Task update = new Task();
        update.setId(task.getId());
        update.setStateCode(Tense.FAILURE.getCode());
        update.setException(dto.getException());
        update.setDuration(dto.getDuration());
        update.setModifierId(dto.getOperatorId());
        update.setModifiedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(update);
    }
}
