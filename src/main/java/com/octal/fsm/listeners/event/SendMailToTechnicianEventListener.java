package com.octal.fsm.listeners.event;

import com.octal.fsm.clients.NotificationClient;
import com.octal.fsm.dto.PushNotificationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class SendMailToTechnicianEventListener implements ApplicationListener<SendMailAndPushEvent> {
    private static final Logger LOGGER = LogManager.getLogger(SendMailToTechnicianEventListener.class);

    @Autowired
    private NotificationClient notificationClient;


    @Override
    public void onApplicationEvent(SendMailAndPushEvent sendMailAndPushEvent) {
//        Technician technicianDTO = sendMailAndPushEvent.getTechnicianDTO();
//        TechnicianRegisterRequest technicianRegisterRequest = sendMailAndPushEvent.getTechnicianRegisterRequest();
        PushNotificationRequest.SendBulkNotificationToUsers sendBulkNotificationToUsers = sendMailAndPushEvent.getSendBulkNotificationToUsers();

//        sendJobEmailToTechnician(technicianDTO, jobDetails, event.getLoggedInuser());
        sendNotificationToUser(sendBulkNotificationToUsers);
    }

    private void sendNotificationToUser(PushNotificationRequest.SendBulkNotificationToUsers sendBulkNotificationToUsers) {
        notificationClient.sendBulkPushNotification(sendBulkNotificationToUsers);
    }
}