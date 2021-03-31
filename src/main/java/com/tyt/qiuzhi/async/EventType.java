package com.tyt.qiuzhi.async;

public enum  EventType {

    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    ADD_QUESTION(5),
    VIOLATION_QUESTION(6),
    QQ_LOGIN(7);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
