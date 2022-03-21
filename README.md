## 资料

https://spring.io/guides

https://spring.io/guides/gs/serving-web-content/

https://docs.github.com/en/developers/apps/getting-started-with-apps

---
## 工具
OKhttp

Lombok

Flyway

Markdown

aliyun oss

MGB

fastjson

jwt

OAuth2.0

BootStrap


---

##
数据库表生成命令

mysql ：create database community

mvn flyway:migrate

mybatis generator自动生成代码

mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate

---
## 目前待改bug：

 一、当第一次使用gitee/github 登录时解除绑定并不能删除users信息

   改进：
   
   1、users增加标识字段 标识第一次注册时使用的方式 将绑定键置灰
    
   2、增加短信验证功能，第一次登录使用短信验证作
   
 二、未进行sql优化
         
