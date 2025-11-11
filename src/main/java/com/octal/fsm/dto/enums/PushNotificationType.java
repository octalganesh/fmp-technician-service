package com.octal.fsm.dto.enums;

public enum PushNotificationType {
    NEW_TASK_ASSIGNED("New Task Assigned");
    private final String status;

    PushNotificationType(String status) {
        this.status = status;
    }

}
