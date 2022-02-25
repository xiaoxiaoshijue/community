package life.majiang.community.community.exception;

import java.lang.annotation.Target;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001, "你找的问题不存在了，要不要换一个试试"),
    TARGET_PARAM_NOT_FOUND(2002, "未选择任何问题或者评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登陆请登陆后重试"),
    SYS_ERROR(2004,"服务器冒烟了"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"你回复的评论不存在"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"非法操作：正在读别人的信息"),
    NOTIFICATION_NOT_FOUND(2009,"消息莫非是不翼而飞了？"),
    FILE_NOT_FOUND(2010,"文件上传失败"),
    COMMENT_IS_TOO_LONG(2011,"评论内容过长"),
    AUTH_NOT_FOUND(2012,"第三方登录异常，请重试或换个方式登陆吧~"),
    DB_USER_NOT_FOUND(2013,"dbUser未找到请重试吧~");
    private String message;
    private Integer code;


    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


}
