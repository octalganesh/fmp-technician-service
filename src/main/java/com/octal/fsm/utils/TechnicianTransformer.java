package com.octal.fsm.utils;

import com.octal.fsm.dto.AuthTechnicianDTO;
import com.octal.fsm.entities.Technician;

import java.util.function.Function;

public class TechnicianTransformer {

    public static final Function<Technician, AuthTechnicianDTO> userToAuthDto = user -> {

        AuthTechnicianDTO AuthTechnicianDTO = new AuthTechnicianDTO();
        AuthTechnicianDTO.setId(user.getUuid());
        AuthTechnicianDTO.setContactNumber(user.getMobileNumber());
        AuthTechnicianDTO.setEmail(user.getEmail());
//        AuthTechnicianDTO.setExtensionNumber(user.get());
        AuthTechnicianDTO.setPassword(user.getPassword());
        AuthTechnicianDTO.setActive(user.getActive());
        AuthTechnicianDTO.setDeleted(user.isDeleted());
        AuthTechnicianDTO.setAuthToken(user.getToken());

        return AuthTechnicianDTO;
    };

    private TechnicianTransformer() {
        //default private constructor
    }
}