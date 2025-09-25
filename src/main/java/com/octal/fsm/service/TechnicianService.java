package com.octal.fsm.service;

import com.octal.fsm.dto.AuthTechnicianDTO;
 import com.octal.fsm.dto.ChangePasswordDTO;
import com.octal.fsm.dto.PageItem;
import com.octal.fsm.dto.TechnicianDto;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.models.request.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
public interface TechnicianService {

    String addTechnician(TechnicianDto.Add add) throws CodeException;

    Boolean deleteById(String id ) throws CodeException;

    TechnicianDto.list getTechnicianByUuid(String id) throws CodeException;

    Boolean changeStatus(String id ) throws CodeException;

    PageItem<TechnicianDto.list> getAllTechnician(PageRequest.List listRequest );


    AuthTechnicianDTO fetchAuthenticatedUserDetailsByEmail(String email);

    Technician getTechnicianByEmailId(String email);

    AuthTechnicianDTO fetchAuthenticatedTechnicianDetailsByEmail(String email);

    void changeTechnicianPassword(@Valid ChangePasswordDTO passwordDTO, String header);

    void resetTechnicianPassword(String email) throws CodeException;

    void resetTechnicianPassword(String token, String newPassword, String confirmPassword)throws CodeException;
}

