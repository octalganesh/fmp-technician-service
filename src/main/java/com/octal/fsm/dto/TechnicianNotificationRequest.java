package com.octal.fsm.dto;

import com.octal.fsm.dto.enums.NotificationUserGroup;
import lombok.Data;

import java.util.List;

@Data
public class TechnicianNotificationRequest {

    private NotificationUserGroup userGroup; // Enum: ALL_USER, ALL_ANDROID_USER, ALL_IOS_USER, PARTICULAR_USER
    private List<String> userIds; // For particular users
}
