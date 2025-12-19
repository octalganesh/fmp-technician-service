package com.octal.fsm.controller;


import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.*;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.repositories.TechnicianRepository;
import com.octal.fsm.service.S3PresignedUrlService;
import com.octal.fsm.utils.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/technician")
public class TechnicianController extends BaseController {

    private static final Logger logger = LogManager.getLogger(TechnicianController.class);

    @Autowired
    private S3PresignedUrlService s3PresignedUrlService;
    @Autowired
    private TechnicianRepository technicianRepository;


    @PostMapping("/add-technician")
    public ResponseEntity<ApiResponse> addTechnician(@RequestBody TechnicianDto.Add technicianDto, HttpServletRequest request) {
        logger.info("TechnicianController.addTechnician");
        String loginUserId = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            String messageResponse = TextUtils.isEmpty(technicianDto.getId()) ? "Technician added Successfully!" : "Technician updated Successfully!";
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, messageResponse, technicianService.addTechnician(technicianDto, tenantId, isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/list")
    public ResponseEntity<ApiResponse> technicianList(@Valid @RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("TechnicianController./list");
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, CommonConstants.DETAILS_FETCHED, technicianService.getAllTechnician(listRequest, tenantId, isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return handleException(e);
        }
    }

    @PostMapping("/get-list-for-assignment")
    public ResponseEntity<ApiResponse> getListForAssignment(@Valid @RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("TechnicianController./getListForAssignment");
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, CommonConstants.DETAILS_FETCHED, technicianService.getListForAssignment(listRequest, tenantId, isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return handleException(e);
        }
    }


    @DeleteMapping("/delete/by/id/{id}")
    public ResponseEntity<ApiResponse> deleteId(@PathVariable("id") String id, HttpServletRequest request) {
        logger.info("TechnicianController./delete/by/id");
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "technician deleted successfully", technicianService.deleteById(id, tenantId, isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(value = "/get/by/{id}")
    public ResponseEntity<ApiResponse> gettechnicianById(@PathVariable("id") String id, HttpServletRequest request) {
        logger.info("TechnicianController./by/id");
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "fetched successfully!", technicianService.getTechnicianByUuid(id, tenantId, isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/change/status/{id}")
    public ResponseEntity<ApiResponse> changeStatus(HttpServletRequest request, @PathVariable("id") String id) {
        logger.info("TechnicianController./change/status");
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Boolean status = technicianService.changeStatus(id, tenantId, isSuperAdmin);
            String messageResponse = Boolean.TRUE.equals(status) ? "technician activated Successfully!" : "technician deactivated Successfully!";
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, messageResponse, null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }

    }

    @GetMapping("/get-static-content/by/slug/{slug}")
    public ResponseEntity<ApiResponse> getBySlug(@PathVariable("slug") String slug, HttpServletRequest request) {
        logger.info("StaticContentController.getBySlug");
        String username = request.getHeader("userName");
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            // User loggedInUser = adminService.getUserByEmailId(username);
            //if (loggedInUser != null) {
            //return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Static Content successfully!", technicianService.getStatiContentBySlug(slug), "200", HttpStatus.OK), HttpStatus.OK);
            return technicianService.getStaticContentBySlug(slug, tenantId, isSuperAdmin);
            //} else {
            //  return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, messageHelper.getMessage(MessageConstants.USER_FOUND), null, "200", HttpStatus.OK), HttpStatus.OK);
            //}

        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse> getPresignedUrl(@RequestBody AwsDTO.GetPreSignedUrlRequest getPreSignedUrlRequest, HttpServletRequest httpServletRequest) {
        logger.info("AdminAuthController.getPresignedUrl");
        String technicianname = httpServletRequest.getHeader(CommonConstants.technician_NAME);
        try {
            String url = s3PresignedUrlService.generatePresignedUrl(getPreSignedUrlRequest.getPath(), getPreSignedUrlRequest.getContentType());
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Presigned URL Generated Successfully.", url,
                    "200", HttpStatus.OK), HttpStatus.OK);

            //Todo Uncomment below code when technician login functionality is done.
//            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianname);
//            if (loggedIntechnician != null) {
//                String url = s3PresignedUrlService.generatePresignedUrl(getPreSignedUrlRequest.getPath(), getPreSignedUrlRequest.getContentType());
//                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Presigned URL Generated Successfully.", url,
//                        "200", HttpStatus.OK), HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
//                        null, "101", HttpStatus.OK), HttpStatus.OK);
//            }

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "101", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/get-announcements")
    public ResponseEntity<ApiResponse> getAnnouncements(@RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("TechnicianController./get-announcements");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                listRequest.setTechnicianId(Collections.singletonList(loggedIntechnician.getUuid()));
                return technicianService.getAnnouncements(listRequest, tenantId, isSuperAdmin);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return handleException(e);
        }
    }

    @PostMapping("/get-technicians-notification-data")
    public ResponseEntity<ApiResponse> getTechnicians(@RequestBody TechnicianNotificationRequest notificationRequest, HttpServletRequest request) {
        logger.info("TechnicianController.getTechnicians");
        try {
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Technicians fetched successfully!", technicianService.getTechniciansNotificationsData(notificationRequest), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/get-notification-list")
    public ResponseEntity<ApiResponse> getNotificationList(@RequestBody UserNotificationListDTO.ListRequest listRequest, HttpServletRequest request) {
        logger.info("TechnicianController.getNotificationList");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            //Long tenantId = getTenantId(request);
            //boolean isSuperAdmin=isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                return technicianService.getNotificationList(listRequest, loggedIntechnician);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return handleException(e);
        }
    }

    @PostMapping("/count")
    public ResponseEntity<ApiResponse> totalCount(@RequestBody JobDashboardResponseDTO.Search search, HttpServletRequest request) {
        logger.info("TechnicianController./count");
        try {
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Dashboard data Generated Successfully.", technicianService.countTechnician(search), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/getTechnicianDevices")
    public ResponseEntity<ApiResponse> getTechnicianDevices(HttpServletRequest httpServletRequest) throws CodeException {
        logger.info("Technician.getTechnicianDevices");
        try {
            Long tenantId = getTenantId(httpServletRequest);
            return new ResponseEntity<>(new ApiResponse("Technician Data.", technicianService.getAllTechnicianDevices(tenantId), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (
                Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/get-by-ids")
    public ResponseEntity<ApiResponse> getTechByIds(@RequestBody List<String> ids, HttpServletRequest httpServletRequest) throws CodeException {
        logger.info("Technician.getTechByIds");
        try {
            Long tenantId = getTenantId(httpServletRequest);
            return new ResponseEntity<>(new ApiResponse("Technician Data.", technicianService.getAllTechByIds(ids), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (
                Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllByTenantId(@RequestBody PageRequest.List listRequest,HttpServletRequest request) {
        logger.info("StaticContentController.getAllByTenantId");
        String username = request.getHeader("userName");
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Data fetch successfully!", technicianService.getAllTech(listRequest,tenantId,isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/get-by-uuid/{id}")
    public ResponseEntity<ApiResponse> getByUuid(@PathVariable("id") String id, HttpServletRequest request) {
        logger.info("StaticContentController.getByUuid");
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Data fetch successfully!", technicianService.getProfileDetails(id), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }
    @PutMapping("/notification-toggle")
    public ResponseEntity<ApiResponse> notificationToggle(HttpServletRequest request) {
        logger.info("TechnicianController.notificationToggle");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                Boolean status = technicianService.notificationToggle(loggedIntechnician, tenantId, isSuperAdmin);
                String messageResponse = Boolean.TRUE.equals(status) ? "Notifications enabled Successfully!" : "Notifications disabled Successfully!";
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, messageResponse, null, "200", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }

    }

    @GetMapping("/role-list")
    public ResponseEntity<ApiResponse>RoleList(HttpServletRequest request) {
        logger.info("TechnicianController.RoleList");
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Role list fetched successfully!", technicianService.getRoleList(tenantId, isSuperAdmin), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/read-unread-announcement/{id}")
    public ResponseEntity<ApiResponse> updateReadUnreadAnnouncement(@PathVariable("id") String id, HttpServletRequest request) {
        logger.info("TechnicianController.updateReadUnreadAnnouncement");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                return technicianService.readUnreadAnnouncements(id,loggedIntechnician.getUuid(),tenantId);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

}

