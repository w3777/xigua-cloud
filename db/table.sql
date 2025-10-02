-- 数据库表结构

-- 数据库名称：xigua
-- 数据库编码：utf8mb4
-- 数据库排序规则：utf8mb4_0900_ai_ci

-- 用户表
-- auto-generated definition
create table xg_user
(
    id          bigint            not null comment '主键id'
        primary key,
    username    varchar(30)       null comment '用户名',
    password    varchar(255)      null comment '密码',
    email       varchar(30)       null comment '邮箱',
    phone       varchar(20)       null comment '手机号',
    avatar      varchar(300)      null comment '头像',
    sex         tinyint           null comment '性别（1；男；2：女）',
    region      varchar(200)      null comment '地区',
    signature   varchar(200)      null comment '个性签名',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    update_by   varchar(20)       null comment '修改人',
    create_time datetime          null comment '创建时间',
    update_time datetime          null comment '修改时间',
    create_by   varchar(20)       null comment '创建人'
)
    comment '用户表';

-- 好友关系表
-- auto-generated definition
create table xg_friend_relation
(
    id          bigint            not null comment '主键id'
        primary key,
    user_id     bigint            null comment '自己',
    friend_id   bigint            null comment '对方(好友)',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment '好友关系(双向关系)';

-- 好友请求表
-- auto-generated definition
create table xg_friend_request
(
    id          bigint            not null comment '主键id'
        primary key,
    sender_id   bigint            null comment '发起人',
    receiver_id bigint            null comment '接收人',
    flow_status tinyint           null comment '流程状态（0：待处理；1：同意；2：拒绝）',
    apply_msg   varchar(300)      null comment '验证消息',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment '好友申请';

-- 聊天消息表
-- auto-generated definition
create table xg_chat_message
(
    id          bigint            not null comment '主键id'
        primary key,
    sender_id   bigint            null comment '发送人',
    receiver_id    bigint            null comment '接收人',
    message     text              null comment '消息内容',
    chat_type   tinyint           not null comment '聊天类型（1：单聊；2：群聊）',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment '消息表';

-- 群组表
-- auto-generated definition
create table xg_group
(
    id             bigint            not null comment '主键id'
        primary key,
    owner_id       bigint            null comment '群主id',
    group_name     varchar(300)      null comment '群名称',
    group_avatar   varchar(300)      null comment '群头像',
    description    text              null comment '群描述',
    current_member int               null comment '当前成员数',
    del_flag       tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by      bigint            null comment '创建人',
    create_time    datetime          null comment '创建时间',
    update_by      bigint            null comment '修改人',
    update_time    datetime          null comment '修改时间'
)
    comment '群组表';

-- 群组成员表
-- auto-generated definition
create table xg_group_member
(
    id          bigint            not null comment '主键id'
        primary key,
    group_id    bigint            null comment '群组id',
    user_id     bigint            null comment '用户id',
    nickname    varchar(300)      null comment '群内昵称',
    status      tinyint default 1 null comment '状态（0：已退出；1：正常）',
    role        tinyint           null comment '群内角色（1：群主；2：群管理；3：普通成员）',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment '群成员表';

-- 消息已读表
-- auto-generated definition
create table xg_message_read
(
    id          bigint            not null comment '主键id'
        primary key,
    sender_id   bigint            null comment '发送人id',
    receiver_id bigint            null comment '接收人id',
    message_id  bigint            null comment '消息id',
    is_read     tinyint           not null comment '是否已读（0：未读；1：已读）',
    read_time   datetime          null comment '已读时间',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment '消息已读表';

-- 2025-08-23 删除字段
alter table xg_chat_message
drop column is_read;
alter table xg_chat_message
drop column read_time;

-- ai机器人表
create table xg_bot
(
    id          bigint            not null comment '主键id'
        primary key,
    name        varchar(200)      null comment '名称',
    avatar      varchar(200)      null comment '头像',
    description varchar(300)      null comment '描述',
    prompt      text              null comment '提示词',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment 'ai机器人';
