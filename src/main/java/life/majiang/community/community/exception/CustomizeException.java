package life.majiang.community.community.exception;

public class CustomizeException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 错误码
     */
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public CustomizeException(BaseErrorCode errorCode) {
        this.code = errorCode.getResultCode();
        this.message = errorCode.getResultMsg();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
