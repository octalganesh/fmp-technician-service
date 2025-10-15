package com.octal.fsm.listeners.event;

import com.octal.fsm.dto.TechnicianRegisterRequest;
import com.octal.fsm.entities.Technician;
import org.springframework.context.ApplicationEvent;

public class AddTechnicianUserInAuthEvent extends ApplicationEvent {
    private final TechnicianRegisterRequest user;
    private final Technician technician;

    public AddTechnicianUserInAuthEvent(TechnicianRegisterRequest user, Technician technician) {
        super(user);
        this.user = user;
        this.technician = technician;
    }

    public Technician getTechnician() {
        return technician;
    }

    public TechnicianRegisterRequest getUser() {
        return user;
    }
}
