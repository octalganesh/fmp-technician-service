package com.octal.fsm.listeners.event;

import com.octal.fsm.clients.AdminClient;
import com.octal.fsm.clients.AuthServiceClient;
import com.octal.fsm.dto.SendMailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class AddTechnicianUserInAuthEventListener implements ApplicationListener<AddTechnicianUserInAuthEvent> {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private AdminClient adminClient;

    @Override
    @Async("addTechnicianEvent")
    public void onApplicationEvent(AddTechnicianUserInAuthEvent event) {
        try {
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