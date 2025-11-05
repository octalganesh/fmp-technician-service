package com.octal.fsm.listeners.event;

import com.octal.fsm.dto.TechnicianRegisterRequest;
import com.octal.fsm.entities.Technician;
import org.springframework.context.ApplicationEvent;

public class AddTechnicianUserInAuthEvent extends ApplicationEvent {
    private final TechnicianRegisterRequest user;
    private final Technician technician;
    private final Long tenantId;
    private final boolean isSuperAdmin;

    public AddTechnicianUserInAuthEvent(TechnicianRegisterRequest user, Technician technician, Long tenantId, boolean isSuperAdmin) {
        super(user);
        this.user = user;
        this.technician = technician;
        this.tenantId = tenantId;
        this.isSuperAdmin = isSuperAdmin;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public Technician getTechnician() {
        return technician;
    }

    public TechnicianRegisterRequest getUser() {
        return user;
    }
}
