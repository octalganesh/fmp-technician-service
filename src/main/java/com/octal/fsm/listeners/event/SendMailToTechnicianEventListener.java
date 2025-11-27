package com.octal.fsm.listeners.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.fsm.clients.NotificationClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.DocumentDTO;
import com.octal.fsm.dto.NotificationContentDTO;
import com.octal.fsm.dto.PushNotificationRequest;
import com.octal.fsm.dto.enums.PushNotificationType;
import com.octal.fsm.entities.MultiUserDeviceDetails;
import com.octal.fsm.service.TechnicianService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class SendMailToTechnicianEventListener implements ApplicationListener<SendMailAndPushEvent> {
    private static final Logger LOGGER = LogManager.getLogger(SendMailToTechnicianEventListener.class);

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private TechnicianService technicianService;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    @Async("SendMailToTechnicianEvent")
    public void onApplicationEvent(SendMailAndPushEvent sendMailAndPushEvent) {
        DocumentDTO.Add uploadDocument = sendMailAndPushEvent.getUploadDocument();
        sendNotificationToUser(uploadDocument, sendMailAndPushEvent.getTenantId());
    }

    private void sendNotificationToUser(DocumentDTO.Add uploadDocument, Long tenantId) {
        try {
            PushNotificationRequest.SendBulkNotificationToUsers sendBulkNotificationToFront = new PushNotificationRequest.SendBulkNotificationToUsers();
            ResponseEntity<ApiResponse> notificationSlugContent = notificationClient.getNotificationContent(PushNotificationType.TECHNICIAN_FILE_SUBMITTED.toString());
            ApiResponse body = notificationSlugContent.getBody();
            if (body != null) {
                NotificationContentDTO.Request content = objectMapper.convertValue(body.getData(), NotificationContentDTO.Request.class);
                sendBulkNotificationToFront.setTitle(content.getTitle());
                sendBulkNotificationToFront.setBody(content.getMessage());
                sendBulkNotificationToFront.setType(PushNotificationType.TECHNICIAN_FILE_SUBMITTED);

                sendBulkNotificationToFront.setTypeId(uploadDocument.getDocumentTypeId());

                ResponseEntity<ApiResponse> frontOfficeDevices = technicianService.getFrontOfficeDevices(null, tenantId);
                ApiResponse fronBdy = frontOfficeDevices.getBody();
                Set<MultiUserDeviceDetails> frontOfficeDeviceDetails = new HashSet<>();
                if (fronBdy != null) {
                    List<MultiUserDeviceDetails> frontOfficedeviceList = objectMapper.convertValue(
                            fronBdy.getData(),
                            new TypeReference<List<MultiUserDeviceDetails>>() {
                            }
                    );
                    if (frontOfficedeviceList != null && !frontOfficedeviceList.isEmpty()) {
                        for (MultiUserDeviceDetails multiUserDeviceDetails : frontOfficedeviceList) {
                            MultiUserDeviceDetails dto = new MultiUserDeviceDetails();
                            dto.setDeviceToken(multiUserDeviceDetails.getDeviceToken());
                            dto.setDeviceType(multiUserDeviceDetails.getDeviceType());
                            dto.setAppVersion(multiUserDeviceDetails.getAppVersion());
                            dto.setDeviceId(multiUserDeviceDetails.getDeviceId());
                            frontOfficeDeviceDetails.add(dto);
                        }
                    }
                    sendBulkNotificationToFront.setTechnicianFcmTokenList(new HashSet<>());
                    sendBulkNotificationToFront.setFrontOfficeFcmTokenList(frontOfficeDeviceDetails);
                    notificationClient.sendBulkPushNotification(sendBulkNotificationToFront);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while sending notification to technician : {}", e.getMessage(), e);
        }
    }
}