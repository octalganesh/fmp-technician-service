package com.octal.fsm.listeners.event;

import com.octal.fsm.dto.PushNotificationRequest;
import com.octal.fsm.dto.TechnicianRegisterRequest;
import com.octal.fsm.entities.Technician;
import org.springframework.context.ApplicationEvent;

public class SendMailAndPushEvent extends ApplicationEvent {
    private final Technician technicianDTO;
    private final TechnicianRegisterRequest technicianRegisterRequest;
    private final String loggedInuser;
    private final PushNotificationRequest.SendBulkNotificationToUsers sendBulkNotificationToUsers;

    public SendMailAndPushEvent(Technician technicianDTO, TechnicianRegisterRequest technicianRegisterRequest, String loggedInuser, PushNotificationRequest.SendBulkNotificationToUsers sendBulkNotificationToUsers) {
        super(technicianDTO);
        this.technicianDTO = technicianDTO;
        this.technicianRegisterRequest = technicianRegisterRequest;
        this.loggedInuser = loggedInuser;
        this.sendBulkNotificationToUsers = sendBulkNotificationToUsers;
    }

    public Technician getTechnicianDTO() {
        return technicianDTO;
    }

    public TechnicianRegisterRequest getTechnicianRegisterRequest() {
        return technicianRegisterRequest;
    }

    public String getLoggedInuser() {
        return loggedInuser;
    }

    public PushNotificationRequest.SendBulkNotificationToUsers getSendBulkNotificationToUsers() {
        return sendBulkNotificationToUsers;
    }
}
