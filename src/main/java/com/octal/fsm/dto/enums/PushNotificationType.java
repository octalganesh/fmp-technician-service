package com.octal.fsm.dto.enums;

public enum PushNotificationType {
    NEW_TASK_ASSIGNED("New Task Assigned"),
    TECHNICIAN_FILE_SUBMITTED("TECHNICIAN_FILE_SUBMITTED");
    private final String status;

    PushNotificationType(String status) {
        this.status = status;
    }

}
