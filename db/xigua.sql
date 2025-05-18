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
    status      tinyint default 1 null comment '状态（0：失效；1：有效）',
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
    receiver    bigint            null comment '接收人',
    message     text              null comment '消息内容',
    is_read     tinyint           null comment '是否已读（0：未读；1：已读）',
    del_flag    tinyint default 0 null comment '是否删除 （0：未删除；1：已删除）',
    create_by   bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_by   bigint            null comment '修改人',
    update_time datetime          null comment '修改时间'
)
    comment '消息表';


