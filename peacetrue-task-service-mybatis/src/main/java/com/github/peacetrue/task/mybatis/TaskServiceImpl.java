package com.github.peacetrue.task.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.peacetrue.associate.AssociateUtils;
import com.github.peacetrue.associate.AssociatedSourceBuilder;
import com.github.peacetrue.core.OperatorCapable;
import com.github.peacetrue.flow.Tense;
import com.github.peacetrue.mybatis.dynamic.MybatisDynamicUtils;
import com.github.peacetrue.pagehelper.PageHelperUtils;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.TaskExecutor;
import com.github.peacetrue.task.executor.TaskFailedEvent;
import com.github.peacetrue.task.executor.TaskStartedEvent;
import com.github.peacetrue.task.executor.TaskSucceededEvent;
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
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
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
    @Autowired
    private TaskExecutor taskExecutor;

    @Transactional
    public TaskVO add(TaskAddDTO dto) {
        logger.info("新增任务: {}", dto);

        TaskVO taskVO = new TaskVO();
        this.add(taskVO, dto);
        if (!Boolean.TRUE.equals(dto.getExecute())) return taskVO;

        try {
            this.execute(taskVO);
        } catch (Exception e) {
            logger.warn("执行任务[{}]异常", taskVO.getId(), e);
        }
        return taskVO;
    }

    protected void add(TaskVO vo, TaskAddDTO dto) {
        Task task = this.saveTask(dto);
        this.saveDependency(task.getId(), dto.getDependentIds());
        BeanUtils.copyProperties(task, vo);
        vo.setDependentIds(dto.getDependentIds());
        vo.setDependOn(new LinkedList<>());
        this.saveDependOn(vo, dto.getDependOn());
    }

    protected Task saveTask(TaskAddDTO dto) {
        Task task = BeanUtils.map(dto, Task.class);
        task.setStateCode(Tense.TODO.getCode());
        task.setCreatorId(dto.getOperatorId());
        task.setCreatedTime(new Date());
        task.setModifierId(dto.getOperatorId());
        task.setModifiedTime(task.getCreatedTime());
        logger.debug("保存任务信息[{}]", task);
        int count = taskMapper.insert(task);
        logger.debug("共影响[{}]行记录", count);
        return task;
    }

    protected void saveDependency(Object taskId, @Nullable List dependentIds) {
        if (CollectionUtils.isEmpty(dependentIds)) return;
        for (Object dependentId : dependentIds) {
            TaskDependency record = new TaskDependency();
            record.setTaskId(taskId);
            record.setDependentTaskId(dependentId);
            logger.debug("保存任务依赖信息[{}->{}]", taskId, dependentId);
            taskDependencyMapper.insert(record);
        }
    }

    protected void saveDependOn(TaskVO vo, List<TaskAddDTO> dependOn) {
        if (CollectionUtils.isEmpty(dependOn)) return;

        for (TaskAddDTO dependOnDTO : dependOn) {
            logger.info("保存依赖于任务[{}]的任务[{}]", vo.getId(), dependOnDTO);
            if (dependOnDTO.getDependentIds() == null) dependOnDTO.setDependentIds(new LinkedList<>());
            dependOnDTO.getDependentIds().add(vo.getId());
            TaskVO dependOnVO = new TaskVO();
            vo.getDependOn().add(dependOnVO);
            this.add(dependOnVO, dependOnDTO);
        }
    }

    protected void execute(TaskVO taskVO) {
        List<TaskVO> vos = this.toList(taskVO);
        List<TaskExecuteDTO> executeDTOS = vos.stream().map(vo -> BeanUtils.map(vo, TaskExecuteDTO.class)).collect(Collectors.toList());
        executeDTOS.forEach(dto -> dto.setDependOn(null));
        Map<Object, TaskExecuteDTO> executeDTOMap = BeanUtils.map(executeDTOS, "id");
        vos.forEach(vo -> Optional.ofNullable(vo.getDependentIds()).ifPresent(dependentIds -> dependentIds.forEach(dependentId ->
                executeDTOMap.get(vo.getId()).addDependent(executeDTOMap.get(dependentId)))
        ));
        this.execute(executeDTOMap.get(taskVO.getId()));
    }

    private List<TaskVO> toList(TaskVO taskVO) {
        List<TaskVO> vos = new LinkedList<>();
        this.eachTaskVO(taskVO, vo -> {
            vos.add(vo);
            return false;
        });
        return vos;
    }

    private boolean eachTaskVO(TaskVO vo, Predicate<TaskVO> predicate) {
        if (predicate.test(vo)) return true;
        if (vo.getDependOn() == null) return false;
        for (Object dependOn : vo.getDependOn()) {
            if (eachTaskVO((TaskVO) dependOn, predicate)) return true;
        }
        return false;
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

    private void setDependentIds(List<?> vos) {
        AssociateUtils.setCollectionAssociate(vos, "dependentIds",
                associatedSourceBuilder.buildCollectionAssociatedSource(
                        taskDependencyMapper::selectByTaskId,
                        getPropertyValue("taskId"),
                        getPropertyValue("dependentTaskId")),
                "id");
    }

    @Override
    public TaskVO get(TaskGetDTO dto) {
        return getTask(dto.getId()).map(task -> BeanUtils.map(task, TaskVO.class)).orElse(null);
    }

    @Override
    public TaskVO getRequired(TaskGetDTO dto) {
        return getRequiredById(dto.getId());
    }

    public TaskVO getRequiredById(Object id) {
        return BeanUtils.map(getRequiredTask(id), TaskVO.class);
    }

    private Optional<Task> getTask(Object id) {
        logger.info("获取任务[{}]的详情", id);
        return Optional.ofNullable(taskMapper.selectByPrimaryKey(id));
    }

    private Task getRequiredTask(Object id) {
        logger.info("获取任务[{}]的详情", id);
        return Optional.ofNullable(taskMapper.selectByPrimaryKey(id)).orElseThrow(() -> new EntityNotFoundException(TaskVO.class, "id", id));
    }

    @Override
    public List<TaskVO> getById(List<?> ids) {
        logger.info("获取任务[{}]的详情", ids);
        return BeanUtils.replaceAsList(taskMapper.selectById(ids), TaskVO.class);
    }

    @Override
    public List<TaskVO> getDependent(Object id) {
        logger.info("获取当前任务[{}]依赖的其他任务", id);
        List<TaskDependency<Object>> dependencies = taskDependencyMapper.selectByTaskId(id);
        logger.info("取得当前任务[{}]依赖的其他任务[{}]", id, dependencies);
        if (dependencies.isEmpty()) return Collections.emptyList();
        return getById(dependencies.stream().map(TaskDependency::getDependentTaskId).collect(Collectors.toList()));
    }

    @Override
    public List<TaskVO> getDependOn(Object id) {
        logger.info("获取依赖于当前任务[{}]的其他任务", id);
        List<TaskDependency<Object>> dependencies = taskDependencyMapper.selectByDependentTaskId(id);
        if (dependencies.isEmpty()) return Collections.emptyList();
        logger.info("取得依赖于当前任务[{}]的其他任务", dependencies);
        return getById(dependencies.stream().map(TaskDependency::getTaskId).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void updateStateDoing(TaskDoingDTO dto) {
        logger.info("更新任务[{}]的状态为[进行中]", dto.getId());
        Task update = new Task();
        update.setId(dto.getId());
        update.setStateCode(Tense.DOING.getCode());
        update.setModifierId(dto.getOperatorId());
        update.setModifiedTime(new Date());
        int count = taskMapper.updateByPrimaryKeySelective(update);
        logger.debug("共影响[{}]条记录", count);
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

    @Override
    public void execute(TaskIdExecuteDTO dto) {
        logger.info("执行任务[{}]", dto);

        List<Task> tasks = this.taskMapper.selectGroupById(dto.getId());
        logger.debug("取得与任务[{}]同组的所有任务(共[{}]个)", dto, tasks.size());
        if (tasks.isEmpty()) return;

        List<TaskExecuteDTO> dtos = BeanUtils.replaceAsList(tasks, TaskExecuteDTO.class);
        dtos.forEach(dto1 -> {
            dto1.setOperatorId(dto.getOperatorId());
            dto1.setOperatorName(dto.getOperatorName());
        });
        Map<Object, TaskExecuteDTO> dtoMap = BeanUtils.map(dtos, "id");
        this.setDependency(dtoMap, dto);

        this.taskExecutor.execute(dtoMap.get(dto.getId()));
    }

    private void setDependency(Map<Object, TaskExecuteDTO> dtoMap, OperatorCapable operatorCapable) {
        logger.info("设置任务[{}]的依赖关系", dtoMap.values());

        List<TaskDependency<Object>> taskDependencies = this.taskDependencyMapper.selectByTaskId(dtoMap.keySet());
        if (taskDependencies.isEmpty()) return;

        Map<Object, List<Object>> taskDependencyMap = taskDependencies.stream().collect(Collectors.groupingBy(
                TaskDependency::getTaskId,
                Collectors.mapping(TaskDependency::getDependentTaskId, Collectors.toList())));
        dtoMap.values().forEach(dto -> {
            Optional.ofNullable(taskDependencyMap.get(dto.getId()))
                    .ifPresent(ids -> ids.forEach(id -> dto.addDependent(dtoMap.get(id))));
        });
    }

    @Override
    public void execute(TaskExecuteDTO dto) {
        logger.info("执行任务[{}]", dto);
        taskExecutor.execute(dto);
    }

    @EventListener
    public void handleTaskStarted(TaskStartedEvent event) {
        if (!(event.getSource() instanceof TaskExecuteDTO)) return;
        TaskDoingDTO dto = BeanUtils.map(event.getSource(), TaskDoingDTO.class);
        dto.setRemark("任务开始");
        this.updateStateDoing(dto);
    }

    @EventListener
    public void handleTaskSucceeded(TaskSucceededEvent event) {
        if (!(event.getSource() instanceof TaskExecuteDTO)) return;
        TaskSuccessDTO dto = BeanUtils.map(event.getSource(), TaskSuccessDTO.class);
        dto.setRemark("执行任务");
        this.updateStateSuccess(dto);
    }

    @EventListener
    public void handleTaskFailed(TaskFailedEvent event) {
        if (!(event.getSource() instanceof TaskExecuteDTO)) return;
        TaskFailureDTO dto = BeanUtils.map(event.getSource(), TaskFailureDTO.class);
        dto.setRemark("执行任务");
        this.updateStateFailure(dto);
    }

}
