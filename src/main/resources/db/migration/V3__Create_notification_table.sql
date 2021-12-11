create table notification
(
	id BIGINT auto_increment,
	notifier BIGINT not null,
	receiver BIGINT not null,
	outer_id BIGINT not null,
	type int not null,
	gmt_create BIGINT not null,
	status int default 0 not null,
	notifier_name varchar(100),
	outer_title varchar(256),
	constraint NOTIFICATION_PK
		primary key (id)
)ENGINE=InnoDB DEFAULT CHARACTER SET = utf8 COLLATE utf8_general_ci;
