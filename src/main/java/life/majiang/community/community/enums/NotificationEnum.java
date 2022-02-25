package life.majiang.community.community.enums;

public enum NotificationEnum {
    REPLY_QUESTION(1,"问题"),
    REPLY_COMMENT(2,"评论"),
    ;
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    NotificationEnum(int status, String name) {
        this.type = status;
        this.name = name;
    }
    public static String nameOfType(int type){
        for (NotificationEnum value : NotificationEnum.values()) {
            if(value.getType() == type)
                return value.getName();
        }
        return "";
    }
}
