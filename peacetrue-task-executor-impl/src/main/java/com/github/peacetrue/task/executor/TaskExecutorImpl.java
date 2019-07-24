package com.github.peacetrue.task.executor;

import com.github.peacetrue.flow.FinalState;
import com.github.peacetrue.flow.Tense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

/**
 * @author xiayx
 */
public class TaskExecutorImpl implements TaskExecutor, BeanFactoryAware {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier(TaskExecutorAutoConfiguration.EXECUTOR_SERVICE_TASK)
    private ExecutorService taskExecutorService;
    @Autowired
    @Qualifier(TaskExecutorAutoConfiguration.EXECUTOR_SERVICE_TRIGGER)
    private ExecutorService triggerExecutorService;
    @Autowired
    private TaskIOMapper taskIOMapper;
    @Autowired
    @Qualifier(TaskExecutorAutoConfiguration.TASK_ID)
    private BiFunction<Task, Integer, String> taskId;
    @Autowired(required = false)
    private TaskDependencyService taskDependencyService;
    @Autowired
    private ExpressionParser expressionParser;
    private BeanResolver beanResolver;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private Map<Task, List<Task>> dependentCache = new HashMap<>();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanResolver = new BeanFactoryResolver(beanFactory);
    }

    public void execute(Task task) {
        logger.info("执行任务[{}]", task);

        this.checkState(task);
        this.checkDependent(task);
        CompletableFuture<Object> future = this.executeCurrent(task);
        triggerExecutorService.execute(() -> this.triggerStarted(task));
        future = future.whenComplete((output, throwable) -> dependentCache.remove(task));
        future = this.updateAfterCompleted(future, task);
        future.whenCompleteAsync((output, throwable) -> {
            try {
                this.triggerCompleted(task, output, throwable);
            } catch (Exception e) {
                logger.warn("触发任务[{}]已完成发生异常", task, e);
            }
        }, triggerExecutorService);
        future.thenRunAsync(() -> {
            try {
                this.executeDependOn(task);
            } catch (Exception e) {
                logger.warn("执行依赖于当前任务[{}]的其他任务发生异常", task, e);
            }
        }, taskExecutorService);
    }

    protected void checkState(Task task) {
        if (task.getStateCode().equals(Tense.DOING.getCode())) {
            throw new TaskExecuteException("任务正在执行中，请勿重复执行");
        }

        if (task.getStateCode().equals(Tense.SUCCESS.getCode())) {
            throw new TaskExecuteException("任务已经执行成功了，请勿重复执行");
        }
    }

    protected void checkDependent(Task task) {
        logger.info("检查当前任务[{}]依赖的其他任务是否都已完成", task);
        if (taskDependencyService == null) {
            logger.warn("尚未配置 TaskDependencyService，跳过依赖检查");
            return;
        }
        List<Task> dependents = taskDependencyService.getDependent(task);
        dependentCache.put(task, dependents);
        logger.debug("取得当前任务[{}]依赖的其他任务[{}]", task, dependents);
        if (CollectionUtils.isEmpty(dependents)) {
            logger.debug("当前任务[{}]不依赖其他任务", task);
            return;
        }
        if (dependents.stream().anyMatch(item -> !item.getStateCode().equals(FinalState.SUCCESS.getCode()))) {
            throw new TaskExecuteException(String.format("当前任务[%s]依赖的其他任务尚未执行成功", task));
        }
        logger.debug("当前任务[{}]依赖的其他任务都已执行成功", task);
    }

    protected CompletableFuture<Object> executeCurrent(Task task) {
        logger.info("任务线程池异步执行任务[{}]", task);
        return CompletableFuture.supplyAsync(() -> {
            Expression expression = expressionParser.parseExpression(task.getBody());
            Object rootObject = taskIOMapper.readObject(task, task.getInput());
            logger.debug("取得任务[{}]的输入参数[{}]作为Root", task, rootObject);
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext(rootObject);
            evaluationContext.setBeanResolver(beanResolver);
            Map<String, Object> variables = getDependentVariables(task);
            logger.debug("取得任务[{}]的依赖参数[{}]作为变量", task, variables);
            evaluationContext.setVariables(variables);
            return expression.getValue(evaluationContext);
        }, taskExecutorService);
    }

    protected CompletableFuture<Object> updateAfterCompleted(CompletableFuture<Object> future, Task task) {
        long started = System.currentTimeMillis();
        return future.whenComplete((output, throwable) -> {
            logger.info("任务[{}]执行完成，同步任务执行结果[{}]", task, output, throwable);
            task.setDuration(System.currentTimeMillis() - started);
            if (throwable == null) {
                task.setStateCode(Tense.SUCCESS.getCode());
                task.setOutput(taskIOMapper.writeObject(task, output));
            } else {
                task.setStateCode(Tense.FAILURE.getCode());
                task.setOutput(throwable.getMessage());
            }
        });
    }

    protected Map<String, Object> getDependentVariables(Task task) {
        if (taskDependencyService == null) return Collections.emptyMap();
        List<Task> dependent = dependentCache.get(task);
        if (CollectionUtils.isEmpty(dependent)) return Collections.emptyMap();
        Map<String, Object> variables = new HashMap<>();
        int i = 0;
        for (Task item : dependent) {
            variables.put(taskId.apply(item, i++), taskIOMapper.readObject(item, item.getOutput()));
        }
        return variables;
    }

    protected void triggerStarted(Task task) {
        logger.debug("任务[{}]已开始", task);
        eventPublisher.publishEvent(new TaskStartedEvent(task));
    }

    protected void triggerCompleted(Task task, @Nullable Object output, @Nullable Throwable throwable) {
        if (throwable == null) {
            logger.debug("任务[{}]执行成功，触发成功完成", task);
            eventPublisher.publishEvent(new TaskSucceededEvent(task, output));
        } else {
            logger.warn("任务[{}]执行异常，触发失败完成", task);
            eventPublisher.publishEvent(new TaskFailedEvent(task, throwable));
        }
    }

    protected void executeDependOn(Task task) {
        logger.info("执行依赖于当前任务[{}]的其他任务", task);
        if (taskDependencyService == null) {
            logger.warn("尚未配置 TaskDependencyService，跳过执行依赖的其他任务");
            return;
        }

        List<Task> dependOn = taskDependencyService.getDependOn(task);
        logger.debug("取得依赖于当前任务[{}]的其他任务[{}]", task, dependOn);
        if (CollectionUtils.isEmpty(dependOn)) {
            logger.debug("不存在依赖于当前任务[{}]的其他任务", task);
            return;
        }
        dependOn.forEach(this::execute);
    }
}
