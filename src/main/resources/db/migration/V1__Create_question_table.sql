create table question
(
	id BIGINT auto_increment,
	title varchar(50),
	description text,
	gmt_create BIGINT,
	gmt_modified BIGINT,
	creator BIGINT,
	comment_count int default 0,
	view_count int default 0,
	like_count int default 0,
	tag varchar(256),
	constraint TABLE_NAME_PK
		primary key (id)
)ENGINE=InnoDB DEFAULT CHARACTER SET = utf8 COLLATE utf8_general_ci;

