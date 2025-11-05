package com.octal.fsm.controller;



import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.AwsDTO;
import com.octal.fsm.dto.TechnicianDto;
import com.octal.fsm.dto.TechnicianNotificationRequest;
import com.octal.fsm.entities.Technician;
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
            String messageResponse = TextUtils.isEmpty(technicianDto.getId()) ? "technician added Successfully!" : "technician updated Successfully!";
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, messageResponse, technicianService.addTechnician(technicianDto), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/list")
    public ResponseEntity<ApiResponse> technicianList(@Valid @RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("TechnicianController./list");
        try {
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, CommonConstants.DETAILS_FETCHED, technicianService.getAllTechnician(listRequest), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return handleException(e);
        }
    }

    @PostMapping("/get-list-for-assignment")
    public ResponseEntity<ApiResponse> getListForAssignment(@Valid @RequestBody PageRequest.List listRequest, HttpServletRequest request) {
        logger.info("TechnicianController./getListForAssignment");
        try {
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, CommonConstants.DETAILS_FETCHED, technicianService.getListForAssignment(listRequest), "200", HttpStatus.OK), HttpStatus.OK);
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
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "technician deleted successfully", technicianService.deleteById(id), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(value = "/get/by/{id}")
    public ResponseEntity<ApiResponse> gettechnicianById(@PathVariable("id") String id, HttpServletRequest request) {
        logger.info("TechnicianController./by/id");
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {

            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "fetched successfully!", technicianService.getTechnicianByUuid(id), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/change/status/{id}")
    public ResponseEntity<ApiResponse> changeStatus(HttpServletRequest request, @PathVariable("id") String id) {
        logger.info("TechnicianController./change/status");
        String userName = request.getHeader(CommonConstants.USER_NAME);
        try {
            Boolean status = technicianService.changeStatus(id);
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
            // User loggedInUser = adminService.getUserByEmailId(username);
            //if (loggedInUser != null) {
            //return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Static Content successfully!", technicianService.getStatiContentBySlug(slug), "200", HttpStatus.OK), HttpStatus.OK);
            return technicianService.getStaticContentBySlug(slug);
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
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                return technicianService.getAnnouncements(listRequest);
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
    public ResponseEntity<ApiResponse>getTechnicians(TechnicianNotificationRequest notificationRequest, HttpServletRequest request) {
        logger.info("TechnicianController.getTechnicians");
        try {
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Technicians fetched successfully!", technicianService.getTechniciansNotificationsData(notificationRequest), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }
}

