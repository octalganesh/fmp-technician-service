package com.octal.fsm.service;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.*;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.models.request.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    void changeTechnicianPassword(@Valid ChangePasswordDTO passwordDTO, String header)throws CodeException;

    void resetTechnicianPassword(String email) throws CodeException;

    void resetTechnicianPassword(String token, String newPassword, String confirmPassword)throws CodeException;

    void updatePassword(TechnicianDetailDTO.ChangePassword changePassword, Technician loggedIntechnician)throws CodeException;

    void updateProfile(TechnicianDetailDTO admintechnicianDetailDTO, MultipartFile profileImage)throws CodeException;

    Object getProfileDetails(String id)throws CodeException;

    ResponseEntity<ApiResponse> getStaticContentBySlug(String slug) throws CodeException;
}

