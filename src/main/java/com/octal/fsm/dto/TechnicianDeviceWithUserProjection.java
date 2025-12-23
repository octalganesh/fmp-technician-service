package com.octal.fsm.dto;

import com.octal.fsm.entities.MultiUserDeviceDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TechnicianDeviceWithUserProjection {

    String uuid; // Technician UUID
    MultiUserDeviceDetails multiUserDeviceDetails;
}
