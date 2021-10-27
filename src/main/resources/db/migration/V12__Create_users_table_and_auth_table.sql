
DROP TABLE IF EXISTS `user_auth_rel`;

CREATE TABLE `user_auth_rel` (
                                 `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                 `user_id` bigint(11) DEFAULT NULL,
                                 `auth_id` bigint(11) DEFAULT NULL,
                                 `auth_type` varchar(20) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_third_auth`;

CREATE TABLE `user_third_auth` (
                                   `auth_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                   `openid` int(11) DEFAULT NULL COMMENT '第三方用户唯一标识id',
                                   `login_type` varchar(40) DEFAULT NULL COMMENT '登陆方式如github,gitee,qq....',
                                   `access_token` varchar(1000) DEFAULT NULL COMMENT '访问openaoi的令牌',
                                   `expire_in` bigint(20) DEFAULT NULL COMMENT '过期时间 = 获取令牌时间+令牌有效时间',
                                   `refresh_token` varchar(100) DEFAULT NULL COMMENT 'token过期后通过此参数获取新的token',
                                   PRIMARY KEY (`auth_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
                         `user_id` bigint(11) NOT NULL AUTO_INCREMENT,
                         `token` varchar(200) DEFAULT NULL,
                         `user_info` varchar(200) DEFAULT NULL COMMENT '。。。。',
                         `avatar_url` varchar(100) DEFAULT NULL COMMENT '头像',
                         `user_name` varchar(100) DEFAULT NULL COMMENT '用户名',
                         `mobile` int(11) DEFAULT NULL COMMENT '手机号',
                         PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `user_local_auth`;

CREATE TABLE `user_local_auth` (
                                   `auth_id` bigint(11) NOT NULL AUTO_INCREMENT,
                                   `user_name` varchar(100) DEFAULT NULL,
                                   `user_password` varchar(100) DEFAULT NULL,
                                   `mobile` int(11) DEFAULT NULL,
                                   PRIMARY KEY (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
