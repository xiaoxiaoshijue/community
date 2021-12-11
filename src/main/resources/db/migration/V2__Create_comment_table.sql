create table `comment`
(
	id BIGINT auto_increment,
	parent_id BIGINT not null,
	type int not null,
	commentator BIGINT not null,
	gmt_create BIGINT not null,
	gmt_modified BIGINT not null,
	like_count BIGINT default 0 not null,
	content varchar(1024) not null,
	comment_count int default 0 not null,
	constraint COMMENT_PK
		primary key (id)
)ENGINE=InnoDB DEFAULT CHARACTER SET = utf8 COLLATE utf8_general_ci;
