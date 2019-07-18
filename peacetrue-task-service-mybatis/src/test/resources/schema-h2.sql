DROP TABLE IF EXISTS task;
CREATE TABLE task
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    group_id      VARCHAR(255)                      NOT NULL COMMENT '任务组标识，一组任务共同完成一个目标',
    name          VARCHAR(63)                       NOT NULL COMMENT '任务名称',
    input         VARCHAR(1022) COMMENT '输入参数，json格式数组',
    body          VARCHAR(510)                      NOT NULL COMMENT '任务执行内容，spel表达式',
    state_code    VARCHAR(31)                       NOT NULL COMMENT 'todo,doing,success,failure',
    output        VARCHAR(255)                      NULL COMMENT '输出参数，json格式对象',
    exception     VARCHAR(255)                      NULL COMMENT '异常信息',
    duration      LONG                              NULL COMMENT '耗时，单位毫秒',
    creator_id    BIGINT                            NOT NULL COMMENT '创建者主键',
    created_time  DATETIME                          NOT NULL COMMENT '创建时间',
    modifier_id   BIGINT                            NOT NULL COMMENT '创建者主键',
    modified_time DATETIME                          NOT NULL COMMENT '创建时间'
);

DROP TABLE IF EXISTS task_dependency;
CREATE TABLE task_dependency
(
    task_id           BIGINT NOT NULL COMMENT '任务主键',
    dependent_task_id BIGINT NOT NULL COMMENT '依赖的任务主键',
    primary key (task_id, dependent_task_id)
);


insert into task (id, group_id, name, input, body, state_code, output, exception, duration, creator_id, created_time, modifier_id, modified_time)
values (1, '解决问题', '提出问题', null, '@solveQuestion.submitQuestion(1000)', 'todo', null, null, null, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP);

insert into task (id, group_id, name, input, body, state_code, output, exception, duration, creator_id, created_time, modifier_id, modified_time)
values (2, '解决问题', '思考方案', null, '@solveQuestion.thinkPlan(#task_1,1000)', 'todo', null, null, null, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP);

insert into task (id, group_id, name, input, body, state_code, output, exception, duration, creator_id, created_time, modifier_id, modified_time)
values (3, '解决问题', '执行方案', null, '@solveQuestion.execute(#task_2,1000)', 'todo', null, null, null, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP);

insert into task_dependency (task_id, dependent_task_id)values (2, 1);
insert into task_dependency (task_id, dependent_task_id)values (3, 2);