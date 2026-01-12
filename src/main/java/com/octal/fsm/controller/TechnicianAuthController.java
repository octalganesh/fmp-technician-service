package com.octal.fsm.controller;


import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.*;
import com.octal.fsm.entities.MultiUserDeviceDetails;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.InvalidPasswordException;
import com.octal.fsm.jwt.JwtTokenProvider;
import com.octal.fsm.listeners.event.AddTechnicianUserInAuthEvent;
import com.octal.fsm.repositories.TechnicianRepository;
import com.octal.fsm.service.TechnicianService;
import com.octal.fsm.utils.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class TechnicianAuthController extends BaseController {
    private static final Logger logger = LogManager.getLogger(TechnicianAuthController.class);

    @Autowired
    private TechnicianService technicianService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @GetMapping(value = "/auth/details/by/email/{email}")
    public ResponseEntity<AuthTechnicianDTO> getTechnicianByTechnicianName(@PathVariable("email") String email) {
        return ResponseEntity.ok(technicianService.fetchAuthenticatedTechnicianDetailsByEmail(email));
    }


    @PostMapping(value = "/auth/login")
    public ResponseEntity<ApiResponse> technicianLogin(@Valid @RequestBody LoginRequest request) {
        try {

            Technician technician = technicianService.getTechnicianByEmailId(request.getEmail());
            if (technician == null) {
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Invalid email address provided. Please enter a registered and valid email.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
            if(!technician.getActive())
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "User account is inactive. Please contact support.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            authenticate(request.getEmail(), request.getPassword());
            AuthenticationResponse authenticationResponse = jwtTokenProvider.generateToken(technician);
            //save jwt token on the time of log in
            technician.setToken(authenticationResponse.getJwtToken());
            if(technician.getMultiUserDeviceDetails()!=null) {
                technician.getMultiUserDeviceDetails().setDeviceId(request.getDeviceId());
                technician.getMultiUserDeviceDetails().setDeviceType(request.getDeviceType());
                technician.getMultiUserDeviceDetails().setDeviceToken(request.getFcmToken());
            }
            else{
                MultiUserDeviceDetails multiUserDeviceDetails = new MultiUserDeviceDetails();
                multiUserDeviceDetails.setDeviceId(request.getDeviceId());
                multiUserDeviceDetails.setDeviceType(request.getDeviceType());
                multiUserDeviceDetails.setDeviceToken(request.getFcmToken());
                technician.setMultiUserDeviceDetails(multiUserDeviceDetails);
            }
            Technician save = technicianRepository.save(technician);

            TechnicianRegisterRequest authRegisterRequest = new TechnicianRegisterRequest();
            authRegisterRequest.setActive(save.getActive());
            authRegisterRequest.setEmail(save.getEmail());
            authRegisterRequest.setRole("technician");
            authRegisterRequest.setCreatedAt(save.getCreatedAt());
            authRegisterRequest.setFullName(save.getName());
            authRegisterRequest.setToken(save.getToken());
            eventPublisher.publishEvent(new AddTechnicianUserInAuthEvent(authRegisterRequest, save, null, false,false));

            authenticationResponse.setRole("technician");

            authenticationResponse.setId(technician.getUuid());
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "User logged in successfully", authenticationResponse,
                    "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof InvalidPasswordException) {
                InvalidPasswordException invalidPasswordException = (InvalidPasswordException) e;
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, invalidPasswordException.getMessage(), null,
                        invalidPasswordException.getCode(), HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }

    }

    @PostMapping(value = "/auth/signout")
    public ResponseEntity<ApiResponse> technicianLogout(HttpServletRequest request) {
        logger.info("TechnicianAuthController.technicianLogout");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                //remove jwt token on the time of log out
                loggedIntechnician.setToken(null);
                loggedIntechnician.getMultiUserDeviceDetails().setDeviceToken("");
                technicianRepository.save(loggedIntechnician);

                TechnicianRegisterRequest authRegisterRequest = new TechnicianRegisterRequest();
                authRegisterRequest.setActive(loggedIntechnician.getActive());
                authRegisterRequest.setRole("technician");
                authRegisterRequest.setEmail(loggedIntechnician.getEmail());
                authRegisterRequest.setFullName(loggedIntechnician.getName());
                authRegisterRequest.setToken(null);
                eventPublisher.publishEvent(new AddTechnicianUserInAuthEvent(authRegisterRequest, loggedIntechnician, getTenantId(request), false,false));

                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Technician logout successfully", null,
                        "200", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/auth/get-profile-details")
    public ResponseEntity<ApiResponse> getProfileDetails(HttpServletRequest httpServletRequest) {
        logger.info("AdminAuthController.getProfileDetails");
        String technicianName = httpServletRequest.getHeader(CommonConstants.technician_NAME);
        try {
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                //if (Boolean.TRUE.equals(loggedIntechnician.getIsAdmin())) {
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Profile Details.", technicianService.getProfileDetails(loggedIntechnician.getUuid()),
                        "200", HttpStatus.OK), HttpStatus.OK);
//                } else {
//                    String apiUri = "auth/get-profile-details";
//                    //Boolean access = checkApiAccess(loggedIntechnician.getRole().getUuid(), apiUri, "VIEW");
//                    if (Boolean.TRUE.equals(access)) {
//                        return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Profile Details.", technicianService.getProfileDetails(id),
//                                "200", HttpStatus.OK), HttpStatus.OK);
//                    } else {
//                        return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Access Denied, Only admin can perform this operation", null,
//                                "101", HttpStatus.OK), HttpStatus.OK);
//                    }
//                }
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "technician not found.",
                        null, "101", HttpStatus.OK), HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "101", HttpStatus.OK), HttpStatus.OK);
        }

    }

    @PostMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody TechnicianDetailDTO technicianDetailDTO,
                                                     HttpServletRequest request) {
        logger.info("AdminAuthController.updateProfile");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Profile Update Successfully", technicianService.updateProfile(loggedIntechnician, technicianDetailDTO, tenantId, isSuperAdmin),
                        "200", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (CodeException c) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, c.getMessage(), null,
                    String.valueOf(c.getCode().getCode()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception o) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, o.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }

    }

    @PostMapping("/change/password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody TechnicianDetailDTO.ChangePassword changePassword,
                                                      HttpServletRequest request) {
        logger.info("TechnicianAuthController.change-password");
        String technicianName = request.getHeader(CommonConstants.technician_NAME);
        try {
            Long tenantId = getTenantId(request);
            boolean isSuperAdmin = isSuperAdmin(request);
            Technician loggedIntechnician = technicianService.getTechnicianByEmailId(technicianName);
            if (loggedIntechnician != null) {
                technicianService.updatePassword(changePassword, loggedIntechnician, tenantId, isSuperAdmin);
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Password Update Successfully", null,
                        "200", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, "Invalid technician.", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (CodeException c) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, c.getMessage(), null,
                    String.valueOf(c.getCode().getCode()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception o) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, o.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }

    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        } catch (DisabledException e) {
            e.getMessage();
            throw new InvalidPasswordException("Invalid User", "500");
        } catch (BadCredentialsException e) {
            e.getMessage();
            throw new InvalidPasswordException("The password you entered is incorrect. Please verify your credentials and try again.", "400");

        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/forget/password")
    public ResponseEntity<ApiResponse> forgetTechnicianPassword(@RequestParam("email") String email) {
        try {
            if (!TextUtils.isEmpty(email)) {
                technicianService.resetTechnicianPassword(email);
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "A password reset email has been sent. Please check your inbox and follow the link to reset your password.", null,
                        "200", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Invalid request,email id not found in request", null,
                        "400", HttpStatus.OK), HttpStatus.OK);
            }
        } catch (CodeException e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    String.valueOf(e.getCode().getCode()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }

    }

    @GetMapping("/verify/token")
    public ResponseEntity<ApiResponse> verifyResetToken(@RequestParam("token") String token, @RequestParam("type") String userType) {
        try {
            technicianService.verifyResetToken(token);
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Token is valid", null,
                    "200", HttpStatus.OK), HttpStatus.OK);
        } catch (CodeException e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    String.valueOf(e.getCode().getCode()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/reset/password")
    public ResponseEntity<ApiResponse> resetTechnicianPassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                                               HttpServletRequest request) {
        try {
            technicianService.resetTechnicianPassword(passwordDTO.getToken(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
            return new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "Password reset successfully", null,
                    "200", HttpStatus.OK), HttpStatus.OK);
        } catch (CodeException e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    String.valueOf(e.getCode().getCode()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(Boolean.FALSE, e.getMessage(), null,
                    "500", HttpStatus.OK), HttpStatus.OK);
        }

    }
}
