package com.octal.fsm.listeners.event;

import com.octal.fsm.dto.TechnicianRegisterRequest;
import org.springframework.context.ApplicationEvent;

public class AddTechnicianUserInAuthEvent extends ApplicationEvent {
    private final TechnicianRegisterRequest user;

    public AddTechnicianUserInAuthEvent(TechnicianRegisterRequest user) {
        super(user);
        this.user = user;
    }

    public TechnicianRegisterRequest getUser() {
        return user;
    }
}
