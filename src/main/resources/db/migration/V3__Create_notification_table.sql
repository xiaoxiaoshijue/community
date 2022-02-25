create table notification
(
	id BIGINT auto_increment,
	notifier BIGINT not null COMMENT '通知发送人',
	receiver BIGINT not null COMMENT '通知接收者',
	outer_id BIGINT not null COMMENT '通知标题(问题名/评论名)',
	type int not null COMMENT '1一级评论/2二级评论',
	gmt_create BIGINT not null,
	status int default 0 not null COMMENT '状态(0未读/1已读)',
	notifier_name varchar(100) COMMENT '通知发送人name',
	receiver_name varchar(100) COMMENT '通知接收者name',
	outer_title varchar(256) COMMENT '评论内容',
	constraint NOTIFICATION_PK
		primary key (id)
)ENGINE=InnoDB DEFAULT CHARACTER SET = utf8 COLLATE utf8_general_ci;
