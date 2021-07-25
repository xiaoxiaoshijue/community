package life.majiang.community.community.exception;

import java.lang.annotation.Target;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001, "你找的问题不存在了，要不要换一个试试"),
    TARGET_PARAM_NOT_FOUND(2002, "未选择任何问题或者评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登陆请登陆后重试"),
    SYS_ERROR(2004,"服务器冒烟了"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"你回复的评论不存在"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空");
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
