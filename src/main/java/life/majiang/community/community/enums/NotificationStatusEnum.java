package life.majiang.community.community.enums;

public enum NotificationStatusEnum {
    UNREAD(0),REAR(1)
    ;
    private int status;

    NotificationStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
