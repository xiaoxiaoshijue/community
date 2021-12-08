alter table question modify CREATOR BIGINT;
alter table comment modify COMMENTATOR BIGINT not null;