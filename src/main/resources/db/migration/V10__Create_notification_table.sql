create table notification
(
	id BIGINT auto_increment,
	notifier BIGINT not null,
	receiver BIGINT not null,
	outer_id BIGINT not null,
	type int not null,
	gmt_create BIGINT not null,
	status int default 0 not null,
	constraint NOTIFICATION_PK
		primary key (id)
);

comment on column notification.notifier is '回复人';

comment on column notification.receiver is '接收人';

comment on column notification.outer_id is '问题id / 评论id 外键id';

comment on column notification.type is '回复类型';

comment on column notification.gmt_create is '回复时间';

comment on column notification.status is '0未读 1已读';