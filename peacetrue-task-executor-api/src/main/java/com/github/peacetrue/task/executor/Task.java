package com.github.peacetrue.task.executor;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiayx
 */
public interface Task extends Serializable {

    /*------任务主体相关--------*/

    /** 获取任务内容 */
    String getBody();

    /** 获取任务输入参数 */
    String getInput();

    /*------任务执行结果相关--------*/

    /** 设置状态 */
    void setStateCode(String stateCode);

    /** 设置耗时 */
    void setDuration(Long duration);

    /** 设置输出结果 */
    void setOutput(String output);

    /*------任务依赖相关--------*/

    /** 获取任务状态. see {@link com.github.peacetrue.flow.Tense} */
    String getStateCode();

    /** 获取任务输出结果 */
    String getOutput();

    /** 获取当前任务依赖的其他任务 */
    List<Task> getDependent();

    /** 获取依赖于当前任务的其他任务 */
    List<Task> getDependOn();

}


