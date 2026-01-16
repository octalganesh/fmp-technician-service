package com.octal.fsm.service;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.*;
import com.octal.fsm.entities.MultiUserDeviceDetails;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.models.request.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Service
public interface TechnicianService {

    String addTechnician(TechnicianDto.Add add, Long tenantId, boolean isSuperAdmin) throws CodeException;

    Boolean deleteById(String id, Long tenantId, boolean isSuperAdmin) throws CodeException;

    TechnicianDto.list getTechnicianByUuid(String id, Long tenantId, boolean isSuperAdmin) throws CodeException;

    Boolean changeStatus(String id, Long tenantId, boolean isSuperAdmin) throws CodeException;

    PageItem<TechnicianDto.list> getAllTechnician(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin);

    PageItem<TechnicianDto.ListForAssignment> getListForAssignment(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin);


    AuthTechnicianDTO fetchAuthenticatedUserDetailsByEmail(String email, Long tenantId, boolean isSuperAdmin);

    Technician getTechnicianByEmailId(String email);

    Technician getTechnicianByEmailIdAndTenantId(String email, Long tenantId);

    AuthTechnicianDTO fetchAuthenticatedTechnicianDetailsByEmail(String email);

    void changeTechnicianPassword(@Valid ChangePasswordDTO passwordDTO, String header) throws CodeException;

    void resetTechnicianPassword(String email,Long tenantId) throws CodeException;

    void resetTechnicianPassword(String token, String newPassword, String confirmPassword) throws CodeException;

    void updatePassword(TechnicianDetailDTO.ChangePassword changePassword, Technician loggedIntechnician, Long tenantId, boolean isSuperAdmin) throws CodeException;

    TechnicianDetailDTO updateProfile(Technician technician, TechnicianDetailDTO technicianDetailDTO, Long tenantId, boolean isSuperAdmin) throws CodeException;

    TechnicianDetailDTO getProfileDetails(String id) throws CodeException;

    ResponseEntity<ApiResponse> getStaticContentBySlug(String slug, Long tenantId, boolean isSuperAdmin) throws CodeException;

    void verifyResetToken(String token) throws CodeException;

    ResponseEntity<ApiResponse> getAnnouncements(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin) throws CodeException;

    Set<MultiUserDeviceDetailsDTO.Response> getTechniciansNotificationsData(TechnicianNotificationRequest notificationRequest) throws CodeException;

    ResponseEntity<ApiResponse> getNotificationList(UserNotificationListDTO.ListRequest listRequest, Technician loggedIntechnician) throws CodeException;

    JobDashboardResponseDTO.Detail countTechnician(JobDashboardResponseDTO.Search search) throws CodeException;

    ResponseEntity<ApiResponse> getFrontOfficeDevices(String id, Long tenantId) throws CodeException;

    List<MultiUserDeviceDetails> getAllTechnicianDevices(Long tenantId) throws CodeException;

    List<TechnicianDto.list> getAllTechByIds(List<String> ids)throws CodeException;

    PageItem<TechnicianDto.list> getAllTech(PageRequest.List listRequest,Long tenantId,boolean isSuperAdmin)throws CodeException;

    Boolean notificationToggle(Technician loggedIntechnician, Long tenantId, boolean isSuperAdmin);

    List<RoleDTO> getRoleList(Long tenantId, boolean isSuperAdmin);

    ResponseEntity<ApiResponse> readUnreadAnnouncements(String id,String userId, Long tenantId) throws CodeException;

    ResponseEntity<ApiResponse> getAllInventoryData(PageRequest.List listRequest,Long tenantId,boolean isSuperAdmin) throws CodeException;

    ResponseEntity<ApiResponse> getAllInventoryRequest(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin) throws CodeException;


    TechnicianTenantDTO getTechnicianByEmailIdWithTenants(String email);
}

