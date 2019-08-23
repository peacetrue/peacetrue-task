package com.github.peacetrue.task.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.peacetrue.associate.AssociateUtils;
import com.github.peacetrue.associate.AssociatedSourceBuilder;
import com.github.peacetrue.core.OperatorCapable;
import com.github.peacetrue.flow.Tense;
import com.github.peacetrue.mybatis.dynamic.MybatisDynamicUtils;
import com.github.peacetrue.pagehelper.PageHelperUtils;
import com.github.peacetrue.serialize.SerializeService;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.task.executor.TaskExecutor;
import com.github.peacetrue.task.executor.TaskFailedEvent;
import com.github.peacetrue.task.executor.TaskStartedEvent;
import com.github.peacetrue.task.executor.TaskSucceededEvent;
import com.github.peacetrue.task.service.*;
import com.github.peacetrue.util.EntityNotFoundException;
import lombok.Data;
import org.mybatis.dynamic.sql.SqlBuilder;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
    @Autowired
    private SerializeService<String> serializeService;

    @Transactional
    public TaskVO add(TaskAddDTO dto, boolean execute) {
        logger.info("新增任务[{}]{}", dto, execute ? "，并立即执行" : "");

        TaskVO vo = new TaskVO();
        Saved saved = new Saved();
        this.add(vo, dto, saved);
        if (!execute) return vo;

        try {
            Set<TaskVO> vos = this.toSet(vo);
            Map<Object, TaskExecuteDTO> dtos = this.toExecuteDTO(vos, swap(saved.getTasks()));
            this.execute(dtos.get(vo.getId()));
        } catch (Exception e) {
            logger.warn("执行任务[{}]异常", vo, e);
        }
        return vo;
    }

    protected void add(TaskVO vo, TaskAddDTO dto, Saved saved) {
        this.addInner(vo, dto, saved);
        this.saveDependOn(vo, dto.getDependOn(), saved);
    }

    private void addInner(TaskVO vo, TaskAddDTO dto, Saved saved) {
        if (vo.getId() == null) {
            Task task = this.saveTask(dto);
            BeanUtils.copyProperties(task, vo);
            BeanUtils.copyProperties(dto, vo);
            vo.setDependentIds(new LinkedList());
            vo.setDependOn(new LinkedList<>());
            saved.getTasks().put(dto, vo);
        } else {
            logger.debug("任务[{}]已保存为任务[{}]", dto, vo.getId());
        }

        Set<Object> savedDependentIds = saved.getDependencies().computeIfAbsent(vo.getId(), v -> new HashSet<>());
        Set differenceSet = (Set) this.differenceSet(HashSet::new, dto.getDependentIds(), savedDependentIds);
        this.saveDependency(vo.getId(), differenceSet);
        savedDependentIds.addAll(differenceSet);
        vo.getDependentIds().addAll(differenceSet);
    }

    private <T, C extends Collection<T>> C differenceSet(Supplier<? extends C> supplier,
                                                         @Nullable Collection<T> one,
                                                         Collection<T> another) {
        if (one == null) return supplier.get();
        C collection = supplier.get();
        collection.addAll(one);
        collection.removeAll(another);
        return collection;
    }

    protected Task saveTask(TaskAddDTO dto) {
        Task task = BeanUtils.map(dto, Task.class);
        task.setInput(serializeService.serialize(dto.getInput()));
        task.setStateCode(Tense.TODO.getCode());
        task.setCreatorId(dto.getOperatorId());
        task.setCreatedTime(new Date());
        task.setModifierId(dto.getOperatorId());
        task.setModifiedTime(task.getCreatedTime());
        logger.debug("保存任务信息[{}]", task);
        taskMapper.insert(task);
        logger.debug("得到任务主键为[{}]", task.getId());
        return task;
    }

    protected void saveDependency(Object taskId, @Nullable Collection dependentIds) {
        if (CollectionUtils.isEmpty(dependentIds)) return;
        for (Object dependentId : dependentIds) {
            TaskDependency record = new TaskDependency();
            record.setTaskId(taskId);
            record.setDependentTaskId(dependentId);
            logger.debug("保存任务依赖信息[{}->{}]", taskId, dependentId);
            taskDependencyMapper.insert(record);
        }
    }

    protected void saveDependOn(TaskVO vo, @Nullable List<TaskAddDTO> dependOn, Saved saved) {
        if (CollectionUtils.isEmpty(dependOn)) return;

        for (TaskAddDTO dependOnDTO : dependOn) {
            logger.info("保存依赖于任务[{}]的任务[{}]", vo.getId(), dependOnDTO);
            if (dependOnDTO.getDependentIds() == null) dependOnDTO.setDependentIds(new LinkedList<>());
            dependOnDTO.getDependentIds().add(vo.getId());
            TaskVO dependOnVO = saved.getTasks().getOrDefault(dependOnDTO, new TaskVO());
            vo.getDependOn().add(dependOnVO);
            this.addInner(dependOnVO, dependOnDTO, saved);
        }

        for (TaskAddDTO dependOnDTO : dependOn) {
            this.saveDependOn(saved.getTasks().get(dependOnDTO), dependOnDTO.getDependOn(), saved);
        }
    }

    private Set<TaskVO> toSet(TaskVO taskVO) {
        Set<TaskVO> vos = new LinkedHashSet<>();
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

    private void setDependency(Map<Object, TaskExecuteDTO> dtos) {
        logger.info("设置任务[{}]的依赖关系", dtos.values());

        List<TaskDependency<Object>> taskDependencies = this.taskDependencyMapper.selectByTaskId(dtos.keySet());
        if (taskDependencies.isEmpty()) return;

        Map<Object, List<Object>> taskDependencyMap = taskDependencies.stream().collect(Collectors.groupingBy(
                TaskDependency::getTaskId,
                Collectors.mapping(TaskDependency::getDependentTaskId, Collectors.toList())));
        dtos.values().forEach(dto -> {
            Optional.ofNullable(taskDependencyMap.get(dto.getId()))
                    .ifPresent(ids -> ids.forEach(id -> dto.addDependent(dtos.get(id))));
        });
    }

    @Override
    @Transactional
    public List<TaskVO> add(List<TaskAddDTO> adds, boolean execute) {
        logger.info("添加任务集合共[{}]个{}", adds.size(), execute ? "，并立即执行" : "");

        List<TaskVO> treeTaskVos = new LinkedList<>();
        Saved saved = new Saved();
        for (TaskAddDTO unique : adds) {
            TaskVO taskVO = new TaskVO();
            this.addInner(taskVO, unique, saved);
            treeTaskVos.add(taskVO);
        }

        for (TaskAddDTO unique : adds) {
            this.saveDependOn(saved.getTasks().get(unique), unique.getDependOn(), saved);
        }

        if (!execute) return treeTaskVos;

        Set<TaskVO> listTaskVos = treeTaskVos.stream().flatMap(vo -> toSet(vo).stream()).collect(Collectors.toSet());
        Map<Object, TaskExecuteDTO> executes = this.toExecuteDTO(listTaskVos, swap(saved.getTasks()));
        treeTaskVos.forEach(vo -> {
            try {
                this.execute(executes.get(vo.getId()));
            } catch (Exception e) {
                logger.warn("执行任务[{}]异常", vo, e);
            }
        });
        return treeTaskVos;
    }


    @Override
    public Page<TaskVO> query(TaskQueryDTO dto, Pageable pageable) {
        logger.info("分页查询任务[{}]信息", dto);

        if (dto == null) dto = new TaskQueryDTO();
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<Task> entities = taskMapper.selectByExample()
                .where(groupId, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(dto.getGroupId())))
                .and(body, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(dto.getBody())))
                .and(input, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(dto.getInput())))
                .and(stateCode, SqlBuilder.isEqualToWhenPresent(dto.getStateCode()))
                .and(output, SqlBuilder.isLikeWhenPresent(MybatisDynamicUtils.likeValue(dto.getOutput())))
                .and(createdTime, SqlBuilder.isBetweenWhenPresent(dto.getCreatedTime().getLowerBound()).and(dto.getCreatedTime().getUpperBound()))
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
        update.setOutput(serializeService.serialize(dto.getOutput()));
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
        update.setException(dto.getException().getMessage());
        update.setDuration(dto.getDuration());
        update.setModifierId(dto.getOperatorId());
        update.setModifiedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    public void execute(TaskGroupIdExecuteDTO dto) {
        logger.info("执行任务组[{}]", dto);

        List<Task> tasks = this.taskMapper.selectByGroupId(dto.getGroupId());
        logger.debug("取得任务组[{}]下的所有任务(共[{}]个)", dto.getGroupId(), tasks.size());
        if (tasks.isEmpty()) return;

        Map<Object, OperatorCapable> taskOperators = tasks.stream().collect(Collectors.toMap(Function.identity(), task -> dto));
        Map<Object, TaskExecuteDTO> dtos = this.toExecuteDTO(taskOperators);
        for (TaskExecuteDTO executeDTO : dtos.values()) {
            if (Tense.SUCCESS.getCode().equals(executeDTO.getStateCode())) {
                logger.debug("跳过已经执行成功的任务[{}]", executeDTO);
                continue;
            }
            if (executeDTO.getDependent().stream().anyMatch(dependent ->
                    !Tense.SUCCESS.getCode().equals(executeDTO.getStateCode()))) {
                logger.debug("跳过依赖任务中存在未完成的任务[{}]", executeDTO);
                continue;
            }
            taskExecutor.execute(executeDTO);
        }
    }

    public static <K, V> Map<V, K> swap(Map<K, V> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private <T> Map<Object, TaskExecuteDTO> toExecuteDTO(Collection<T> tasks, Map<T, ? extends OperatorCapable> operators) {
        return this.toExecuteDTO(tasks.stream().collect(Collectors.toMap(Function.identity(), operators::get)));
    }

    private Map<Object, TaskExecuteDTO> toExecuteDTO(Map<Object, OperatorCapable> tasks) {
        List<TaskExecuteDTO> dtos = new LinkedList<>();
        for (Object task : tasks.keySet()) {
            dtos.add(this.toExecuteDTO(task, tasks.get(task)));
        }
        Map<Object, TaskExecuteDTO> dtoMap = BeanUtils.map(dtos, "id");
        this.setDependency(dtoMap);
        return dtoMap;
    }

    /** {@link Task} or {@link TaskVO} to {@link TaskExecuteDTO} */
    private TaskExecuteDTO toExecuteDTO(Object task, OperatorCapable operatorCapable) {
        TaskExecuteDTO dto = new TaskExecuteDTO();
        BeanUtils.copyProperties(operatorCapable, dto);
        BeanUtils.copyProperties(task, dto);
        dto.setInput(serializeService.deserialize((String) BeanUtils.getPropertyValue(task, "input")));
        dto.setOutput(serializeService.deserialize((String) BeanUtils.getPropertyValue(task, "output")));
        return dto;
    }

    @Override
    public void execute(TaskIdExecuteDTO dto) {
        logger.info("执行任务[{}]", dto);

        List<Task> tasks = this.taskMapper.selectGroupById(dto.getId());
        logger.debug("取得与任务[{}]同组的所有任务(共[{}]个)", dto, tasks.size());
        if (tasks.isEmpty()) return;

        Map<Task, TaskIdExecuteDTO> operators = tasks.stream().collect(Collectors.toMap(Function.identity(), task -> dto));
        Map<Object, TaskExecuteDTO> dtos = this.toExecuteDTO(tasks, operators);
        this.taskExecutor.execute(dtos.get(dto.getId()));
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


    @Data
    public static class Saved {
        private Map<TaskAddDTO, TaskVO> tasks = new HashMap<>();
        private Map<Object, Set<Object>> dependencies = new HashMap<>();
    }

}
