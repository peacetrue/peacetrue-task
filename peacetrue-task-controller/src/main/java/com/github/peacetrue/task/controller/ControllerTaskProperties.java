package com.github.peacetrue.task.controller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务配置
 *
 * @author xiayx
 */
@Data
@ConfigurationProperties(prefix = "peacetrue.task")
public class ControllerTaskProperties {

    private Urls urls;

    @Data
    public static class Urls {
        /** 新增地址 */
        private String add;
        /** 查询地址 */
        private String query;
        /** 查看地址 */
        private String get;
        /** 执行地址 */
        private String execute;
    }

}