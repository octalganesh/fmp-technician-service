package com.octal.fsm.listeners.event;

import com.google.gson.Gson;
import com.netflix.discovery.converters.Auto;
import com.octal.fsm.clients.AdminClient;
import com.octal.fsm.clients.AuthServiceClient;
import com.octal.fsm.clients.NotificationClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.PushNotificationRequest;
import com.octal.fsm.dto.SendMailRequestDTO;
import com.octal.fsm.dto.UserDetailsDTO;
import com.octal.fsm.dto.enums.PushNotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class AddTechnicianUserInAuthEventListener implements ApplicationListener<AddTechnicianUserInAuthEvent> {
    private static final Logger LOGGER = LogManager.getLogger(AddTechnicianUserInAuthEventListener.class);

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private AdminClient adminClient;

    @Override
    @Async("addTechnicianEvent")
    public void onApplicationEvent(AddTechnicianUserInAuthEvent event) {
        try {
//            PushNotificationRequest.Request request= new PushNotificationRequest.Request();
//            request.setNotificationType(PushNotificationType.NEW_TASK_ASSIGNED);
//            request.setNotificationTypeId(event.getTechnician().getUuid());
//            //request.setPerformedByUserId(loggedInUser.getUuid());
//            //request.setFromDisplayName(loggedInUser.getUsername());
//            ApiResponse response = notificationClient.getNotificationContent(request.getNotificationType().name()).getBody();
//            if (response.getStatus().equalsIgnoreCase(String.valueOf(101))) {
//                //Content Not Found!!
//                LOGGER.error("Content Not Found!!");
//            } else {
//                Gson gson = new Gson();
//                String jsonResponse = gson.toJson(response.getData());
//                NotificationD notificationContentDTO = gson.fromJson(jsonResponse, NotificationContentDTO.class);
//                request.setTitle(notificationContentDTO.getTitle());
//                notificationService.updateRequestMessage(notificationContentDTO, request);
//            }
//
//            PushNotificationRequest fcmUser = new PushNotificationRequest();
//            fcmUser.setId(toUser.getRecordId());
//            if (toUser.getUserDeviceDetails() != null) {
//                Set<MultiUserDeviceDetails> multiUserDeviceDetailsList = multiUserDeviceDetailsRepository.findByUserId(toUser.getUuid());
//                fcmUser.setFcmDeviceToken(multiUserDeviceDetailsList);
//                fcmUser.setDeviceType(toUser.getUserDeviceDetails().getDeviceType());
//                request.setSystemUser(fcmUser);
//                if (loggedInUser.getProfilePicture() != null)
//                    request.setImageUrl(loggedInUser.getProfilePicture());
//                else
//                    request.setImageUrl(defaultProfilePicUrl);
//            }
//            notificationClient.sendPushNotification(request);
//            LOGGER.info("-------------------- Done for Block -----------------");
            authServiceClient.createUser(event.getUser());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SendMailRequestDTO request = new SendMailRequestDTO();
            request.setObjectData(event.getTechnician());
            request.setTemplateName("TECHNICIAN_WELCOME");
            request.setObjectName("Technician");
            adminClient.sendDynamicMail(request,event.getTenantId(),event.isSuperAdmin());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}