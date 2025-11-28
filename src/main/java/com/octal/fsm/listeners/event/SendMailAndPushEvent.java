package com.octal.fsm.listeners.event;

import com.octal.fsm.dto.DocumentDTO;
import com.octal.fsm.dto.PushNotificationRequest;
import com.octal.fsm.dto.TechnicianRegisterRequest;
import com.octal.fsm.entities.Technician;
import org.springframework.context.ApplicationEvent;

public class SendMailAndPushEvent extends ApplicationEvent {
    private final String loggedInuser;
    private final Long tenantId;
    private final DocumentDTO.Add uploadDocument;

    public SendMailAndPushEvent(String loggedInuser, Long tenantId, DocumentDTO.Add uploadDocument) {
        super(loggedInuser);
        this.loggedInuser = loggedInuser;
        this.tenantId = tenantId;
        this.uploadDocument = uploadDocument;
    }

    public String getLoggedInuser() {
        return loggedInuser;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public DocumentDTO.Add getUploadDocument() {
        return uploadDocument;
    }
}
