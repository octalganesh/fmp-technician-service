package com.octal.fsm.listeners.event;

import com.octal.fsm.clients.AuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class AddTechnicianUserInAuthEventListener implements ApplicationListener<AddTechnicianUserInAuthEvent> {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Override
    @Async("addTechnicianEvent")
    public void onApplicationEvent(AddTechnicianUserInAuthEvent event) {
        authServiceClient.createUser(event.getUser());
    }

}