package com.octal.fsm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.fsm.clients.AdminClient;
import com.octal.fsm.clients.JobClient;
import com.octal.fsm.clients.NotificationClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.*;
import com.octal.fsm.dto.enums.NotificationUserGroup;
import com.octal.fsm.entities.MultiUserDeviceDetails;
import com.octal.fsm.entities.Role;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.UserOtpVerification;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.listeners.event.AddTechnicianUserInAuthEvent;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.repositories.RoleRepository;
import com.octal.fsm.repositories.TechnicianRepository;
import com.octal.fsm.repositories.UserVerificationRepository;
import com.octal.fsm.service.TechnicianService;
import com.octal.fsm.service.UserVerificationService;
import com.octal.fsm.specification.GenericSpecificationsBuilder;
import com.octal.fsm.specification.SpecificationFactory;
import com.octal.fsm.utils.TechnicianTransformer;
import com.octal.fsm.utils.TextUtils;
import feign.FeignException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TechnicianServiceImpl implements TechnicianService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TechnicianServiceImpl.class);

    private static final AtomicInteger sequence = new AtomicInteger(1);
    @Autowired
    private AdminClient adminClient;

    @Autowired
    private TechnicianRepository technicianRepository;
    @Autowired
    private SpecificationFactory<Technician> technicianSpecificationFactory;
    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private JobClient jobClient;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${aws.base-url}")
    private String awsS3BaseUrl;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
    private static final int PASSWORD_LENGTH = 10;

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }


    public static boolean isPasswordValid(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])(.{8,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public String addTechnician(TechnicianDto.Add add, Long tenantId, boolean isSuperAdmin) throws CodeException {
        if (isSuperAdmin) {
            tenantId = 1L;
        }
        String randomPassword = generateRandomPassword();
        if (TextUtils.isEmpty(add.getName()))
            throw new CodeException("name is required", ErrorCode.COMMON);
        if (TextUtils.isEmpty(add.getMobileNumber()))
            throw new CodeException("mobile number is required", ErrorCode.COMMON);
        if (TextUtils.isEmpty(add.getEmail()))
            throw new CodeException("email id is required", ErrorCode.COMMON);
        if (TextUtils.isEmpty(add.getAddress()))
            throw new CodeException("address is required", ErrorCode.COMMON);
        if (add.getGender() == null)
            throw new CodeException("gender is required", ErrorCode.COMMON);
        Optional<Technician> optionalTechnician = technicianRepository.findByUuidAndTenantId(add.getId(), tenantId);
        if (optionalTechnician.isPresent() && !optionalTechnician.get().getUuid().equals(add.getId())) {
            throw new CodeException("technician name is already present!", ErrorCode.RECORD_NOT_FOUND);
        }
        Technician newtechnicianRecord = null;
        if (TextUtils.isEmpty(add.getId())) {
            if (technicianRepository.existsByMobileNumberAndTenantId(add.getMobileNumber(), tenantId))
                throw new CodeException("Technician with mobile number " + add.getMobileNumber() + " already exists", ErrorCode.RECORD_NOT_FOUND);
            if (technicianRepository.existsByEmailAndTenantId(add.getEmail(), tenantId))
                throw new CodeException("Technician with email " + add.getEmail() + " already exists", ErrorCode.RECORD_NOT_FOUND);
            newtechnicianRecord = new Technician();
            newtechnicianRecord.setCreatedAt(LocalDateTime.now());
            newtechnicianRecord.setUpdatedAt(LocalDateTime.now());
            newtechnicianRecord.setEmployeeId(generateEmployeeUniqeId());
            newtechnicianRecord.setAvailable(true);
            newtechnicianRecord.setPassword(passwordEncoder.encode(randomPassword));
            newtechnicianRecord.setActive(true);
        } else {
            Optional<Technician> technician = technicianRepository.findByUuidAndTenantId(add.getId(), tenantId);
            Optional<Technician> optional = technicianRepository.findByMobileNumberAndTenantId(add.getMobileNumber(), tenantId);
            Optional<Technician> optionalTechnician1 = technicianRepository.findByEmail(add.getEmail());
            if (optionalTechnician1.isPresent() && !optionalTechnician1.get().getUuid().equals(add.getId()))
                throw new CodeException("Technician with email " + add.getEmail() + " already exists", ErrorCode.RECORD_NOT_FOUND);
            if (optional.isPresent() && !optional.get().getUuid().equals(add.getId()))
                throw new CodeException("Technician with mobile number " + add.getMobileNumber() + " already exists", ErrorCode.RECORD_NOT_FOUND);
            if (technician.isPresent()) {
                newtechnicianRecord = technician.get();
                newtechnicianRecord.setUpdatedAt(LocalDateTime.now());
                newtechnicianRecord.setActive(add.getIsActive());
            } else {
                throw new CodeException("Technician not Found!", ErrorCode.COMMON);
            }
        }
        newtechnicianRecord.setDeleted(false);
        newtechnicianRecord.setEmail(add.getEmail());
        newtechnicianRecord.setMobileNumber(add.getMobileNumber());
        newtechnicianRecord.setName(add.getName());
        //newtechnicianRecord.setAddress(new Address(add.getAddress().getStreet(), add.getAddress().getCity(), add.getAddress().getState(), add.getAddress().getPostalCode(), add.getAddress().getCountry()));
        newtechnicianRecord.setAddress(add.getAddress());
        newtechnicianRecord.setGender(add.getGender());
        if (!TextUtils.isEmpty(add.getProfilePicture()))
            newtechnicianRecord.setProfilePicture(awsS3BaseUrl + add.getProfilePicture());
        newtechnicianRecord.setJoinDate(add.getJoinedDate().atStartOfDay());
        newtechnicianRecord.setTenantId(tenantId);
        Technician technician = technicianRepository.save(newtechnicianRecord);
        TechnicianRegisterRequest technicianRegisterRequest = new TechnicianRegisterRequest();
        technicianRegisterRequest.setActive(newtechnicianRecord.getActive());
        technicianRegisterRequest.setEmail(newtechnicianRecord.getEmail());
        technicianRegisterRequest.setRole("technician");
        technicianRegisterRequest.setPassword("Technician@123");
        technicianRegisterRequest.setCreatedAt(newtechnicianRecord.getCreatedAt());
        technicianRegisterRequest.setFullName(technician.getName());
        technicianRegisterRequest.setTenantId(String.valueOf(tenantId));
        technician.setPassword(randomPassword);
        eventPublisher.publishEvent(new AddTechnicianUserInAuthEvent(technicianRegisterRequest, technician, tenantId, isSuperAdmin));
        return technician.getUuid();
    }


    @Override
    public Boolean deleteById(String id, Long tenantId, boolean isSuperAdmin) throws CodeException {
        if (isSuperAdmin) {
            tenantId = 1l;
        }
        Optional<Technician> technicianRecord = technicianRepository.findByUuidAndTenantId(id, tenantId);
        if (technicianRecord.isPresent()) {
            technicianRecord.get().setDeleted(true);
            technicianRepository.save(technicianRecord.get());
            return true;
        } else {
            throw new CodeException(CommonConstants.TECHNICIAN_NOT_FOUND + id, ErrorCode.COMMON);
        }
    }

    @Override
    public TechnicianDto.list getTechnicianByUuid(String id, Long tenantId, boolean isSuperAdmin) throws CodeException {
        if (isSuperAdmin) {
            tenantId = 1L;
        }
        Optional<Technician> technicianRecord = technicianRepository.findByUuid(id);
        Map<String, TechnicianDto.TaskStats> taskSummaryMap = new HashMap<>();
        if (technicianRecord.isPresent()) {
            ResponseEntity<ApiResponse> response = jobClient.getTechnicianTaskSummary(List.of(technicianRecord.get().getUuid()), tenantId, isSuperAdmin);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = response.getBody();
                if (apiResponse != null && apiResponse.getData() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    Object data = apiResponse.getData();
                    if (data instanceof Map<?, ?>) {
                        Map<?, ?> mapData = (Map<?, ?>) data;
                        for (Map.Entry<?, ?> entry : mapData.entrySet()) {

                            String key = entry.getKey().toString();

                            // Convert each value to TaskStats object
                            TechnicianDto.TaskStats stats =
                                    mapper.convertValue(entry.getValue(), TechnicianDto.TaskStats.class);

                            taskSummaryMap.put(key, stats);
                        }
                    } else {
                        LOGGER.warn("Unexpected data type in response: {}", data.getClass());
                    }
                } else {
                    LOGGER.warn("Empty ApiResponse body or data");
                }
            } else {
                LOGGER.error("Failed to fetch technician task summary: {}",
                        response != null ? response.getStatusCode() : "null response");
            }

        }
        Map<String, Double> ratingSummaryMap = new HashMap<>();
        if (technicianRecord.isPresent()) {
            ResponseEntity<ApiResponse> response = adminClient.getFeedbackSummary(List.of(technicianRecord.get().getUuid()), tenantId, isSuperAdmin);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = response.getBody();
                if (apiResponse != null && apiResponse.getData() != null) {
                    Object data = apiResponse.getData();
                    if (data instanceof Map<?, ?>) {
                        // Type-safe conversion
                        ratingSummaryMap = ((Map<?, ?>) data).entrySet().stream()
                                .filter(e -> e.getKey() instanceof String && e.getValue() instanceof Double)
                                .collect(Collectors.toMap(
                                        e -> (String) e.getKey(),
                                        e -> (Double) e.getValue()
                                ));
                    } else {
                        LOGGER.warn("Unexpected data type in response for feedback summary: {}", data.getClass());
                    }
                } else {
                    LOGGER.warn("Empty ApiResponse body or data for for feedback summary");
                }
            } else {
                LOGGER.error("Failed to fetch technician feedback summary: {}",
                        response != null ? response.getStatusCode() : "null response");
            }

        }
        if (technicianRecord.isPresent()) {
            TechnicianDto.list technician = new TechnicianDto.list();
            technician.setEmployeeId(technicianRecord.get().getEmployeeId());
            technician.setEmail(technicianRecord.get().getEmail());
            technician.setId(technicianRecord.get().getUuid());
            technician.setName(technicianRecord.get().getName());
            technician.setRating(ratingSummaryMap.getOrDefault(technicianRecord.get().getUuid(), 0.0));
            if (taskSummaryMap.containsKey(technicianRecord.get().getUuid())) {
                technician.setAssignedTasks(taskSummaryMap.get(technicianRecord.get().getUuid()).getAssignedTasks());
                technician.setAllTasks(taskSummaryMap.get(technicianRecord.get().getUuid()).getAllTasks());
                technician.setCompletedTasks(taskSummaryMap.get(technicianRecord.get().getUuid()).getCompletedTasks());
            }
            //technician.setAddress(new AddressDTO(technicianRecord.get().getAddress().getStreet(), technicianRecord.get().getAddress().getCity(), technicianRecord.get().getAddress().getState(), technicianRecord.get().getAddress().getPostalCode(), technicianRecord.get().getAddress().getCountry()));
            technician.setAddress(technicianRecord.get().getAddress());
            technician.setMobileNumber(technicianRecord.get().getMobileNumber());
            technician.setCountryCode(technicianRecord.get().getMobileNumber().split(" ")[0]);
            technician.setProfilePicture(technicianRecord.get().getProfilePicture());
            technician.setIsActive(technicianRecord.get().getActive());
            technician.setJoinedDate(technicianRecord.get().getJoinDate() != null ? technicianRecord.get().getJoinDate().toString() : LocalDateTime.now().toString());
            technician.setCreatedAt(technicianRecord.get().getCreatedAt().toString());
            technician.setUpdatedAt(technicianRecord.get().getUpdatedAt().toString());
            technician.setGender(technicianRecord.get().getGender());
            if (technicianRecord.get().getMultiUserDeviceDetails() != null) {
                MultiUserDeviceDetails multiUserDeviceDetails = technicianRecord.get().getMultiUserDeviceDetails();
                MultiUserDeviceDetailsDTO.Response multiUserDeviceDetailsDTO = new MultiUserDeviceDetailsDTO.Response();
                multiUserDeviceDetailsDTO.setDeviceToken(multiUserDeviceDetails.getDeviceToken());
                multiUserDeviceDetailsDTO.setDeviceType(multiUserDeviceDetails.getDeviceType());
                multiUserDeviceDetailsDTO.setAppVersion(multiUserDeviceDetails.getAppVersion());
                technician.setMultiUserDeviceDetails(multiUserDeviceDetailsDTO);
                technician.setMultiUserDeviceDetails(multiUserDeviceDetailsDTO);
            }
            return technician;
        } else {
            throw new CodeException(CommonConstants.TECHNICIAN_NOT_FOUND + id, ErrorCode.COMMON);
        }
    }

    @Override
    public Boolean changeStatus(String id, Long tenantId, boolean isSuperAdmin) throws CodeException {
        if (isSuperAdmin) {
            tenantId = 1l;
        }
        Optional<Technician> technicianRecord = technicianRepository.findByUuidAndTenantId(id, tenantId);
        if (technicianRecord.isPresent()) {
            if (Boolean.TRUE.equals(technicianRecord.get().getActive())) {
                technicianRecord.get().setActive(false);
                technicianRepository.save(technicianRecord.get());
                return false;
            } else {
                technicianRecord.get().setActive(true);
                technicianRepository.save(technicianRecord.get());
                return true;
            }
        } else {
            throw new CodeException(CommonConstants.TECHNICIAN_NOT_FOUND + id, ErrorCode.COMMON);
        }
    }

    @Override
    public PageItem<TechnicianDto.list> getAllTechnician(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin) {
        if (isSuperAdmin) {
            tenantId = 1l;
        }
        String trimmedText = listRequest.getSearchText().trim();
        listRequest.setSearchText(trimmedText);
        GenericSpecificationsBuilder<Technician> builder = new GenericSpecificationsBuilder<>();
        Pageable pageable = null;
        if (Boolean.TRUE.equals(listRequest.getAsc())) {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).ascending());
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).descending());
        }
        prepareTechnicianSearchFilter(listRequest, builder, tenantId, isSuperAdmin);
        Page<Technician> pagedResult = technicianRepository.findAll(builder.build(), pageable);
        Map<String, TechnicianDto.TaskStats> taskSummaryMap = new HashMap<>();
        if (!pagedResult.isEmpty()) {
            ResponseEntity<ApiResponse> response = jobClient.getTechnicianTaskSummary(pagedResult.get().map(Technician::getUuid).collect(Collectors.toList()), tenantId, isSuperAdmin);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = response.getBody();
                if (apiResponse != null && apiResponse.getData() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    Object data = apiResponse.getData();
                    if (data instanceof Map<?, ?>) {
                        Map<?, ?> mapData = (Map<?, ?>) data;
                        for (Map.Entry<?, ?> entry : mapData.entrySet()) {

                            String key = entry.getKey().toString();

                            // Convert each value to TaskStats object
                            TechnicianDto.TaskStats stats =
                                    mapper.convertValue(entry.getValue(), TechnicianDto.TaskStats.class);

                            taskSummaryMap.put(key, stats);
                        }
                    } else {
                        LOGGER.warn("Unexpected data type in response: {}", data.getClass());
                    }
                } else {
                    LOGGER.warn("Empty ApiResponse body or data");
                }
            } else {
                LOGGER.error("Failed to fetch technician task summary: {}",
                        response != null ? response.getStatusCode() : "null response");
            }

        }
        Map<String, Double> ratingSummaryMap = new HashMap<>();
        if (!pagedResult.isEmpty()) {
            ResponseEntity<ApiResponse> response = adminClient.getFeedbackSummary(pagedResult.get().map(Technician::getUuid).collect(Collectors.toList()), tenantId, isSuperAdmin);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = response.getBody();
                if (apiResponse != null && apiResponse.getData() != null) {
                    Object data = apiResponse.getData();
                    if (data instanceof Map<?, ?>) {
                        // Type-safe conversion
                        ratingSummaryMap = ((Map<?, ?>) data).entrySet().stream()
                                .filter(e -> e.getKey() instanceof String && e.getValue() instanceof Double)
                                .collect(Collectors.toMap(
                                        e -> (String) e.getKey(),
                                        e -> (Double) e.getValue()
                                ));
                    } else {
                        LOGGER.warn("Unexpected data type in response for feedback summary: {}", data.getClass());
                    }
                } else {
                    LOGGER.warn("Empty ApiResponse body or data for for feedback summary");
                }
            } else {
                LOGGER.error("Failed to fetch technician feedback summary: {}",
                        response != null ? response.getStatusCode() : "null response");
            }

        }

        List<TechnicianDto.list> responseList = new ArrayList<>();
        for (Technician technician : pagedResult.getContent()) {
            TechnicianDto.list dto = new TechnicianDto.list();
            dto.setId(technician.getUuid());
            dto.setName(technician.getName());
            dto.setEmail(technician.getEmail());
            dto.setMobileNumber(technician.getMobileNumber());
            dto.setProfilePicture(technician.getProfilePicture());
            dto.setRating(ratingSummaryMap.getOrDefault(technician.getUuid(), 0.0));
            if (taskSummaryMap.containsKey(technician.getUuid())) {
                dto.setAssignedTasks(taskSummaryMap.get(technician.getUuid()).getAssignedTasks());
                dto.setAllTasks(taskSummaryMap.get(technician.getUuid()).getAllTasks());
                dto.setCompletedTasks(taskSummaryMap.get(technician.getUuid()).getCompletedTasks());
            }
            //dto.setAddress(new AddressDTO(technician.getAddress().getStreet(), technician.getAddress().getCity(), technician.getAddress().getState(), technician.getAddress().getPostalCode(), technician.getAddress().getCountry()));
            dto.setAddress(technician.getAddress());
            dto.setIsActive(technician.getActive());
            dto.setCreatedAt(String.valueOf(technician.getCreatedAt()));
            dto.setEmployeeId(technician.getEmployeeId());
            dto.setUpdatedAt(technician.getUpdatedAt().toString());
            dto.setJoinedDate(technician.getJoinDate() != null ? technician.getJoinDate().toString() : LocalDateTime.now().toString());
            dto.setGender(technician.getGender());
            dto.setRoleName(Objects.nonNull(technician.getRole()) ? technician.getRole().getName() : null);
            responseList.add(dto);
        }

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), responseList, listRequest.getPageNumber(),
                listRequest.getPageSize());
    }

    @Override
    public PageItem<TechnicianDto.ListForAssignment> getListForAssignment(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin) {
        if (isSuperAdmin) {
            tenantId = 1l;
        }
        String trimmedText = listRequest.getSearchText().trim();
        listRequest.setSearchText(trimmedText);
        GenericSpecificationsBuilder<Technician> builder = new GenericSpecificationsBuilder<>();
        Pageable pageable = null;
        if (Boolean.TRUE.equals(listRequest.getAsc())) {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).ascending());
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).descending());
        }
        prepareTechnicianSearchFilter(listRequest, builder, tenantId, isSuperAdmin);
        builder.with(technicianSpecificationFactory.isEqual("available", true));
        Page<Technician> pagedResult = technicianRepository.findAll(builder.build(), pageable);
        List<TechnicianDto.ListForAssignment> responseList = new ArrayList<>();
        for (Technician technician : pagedResult.getContent()) {
            TechnicianDto.ListForAssignment dto = new TechnicianDto.ListForAssignment();
            dto.setId(technician.getUuid());
            dto.setName(technician.getName());
            dto.setEmail(technician.getEmail());
            dto.setMobileNumber(technician.getMobileNumber());
            dto.setEmployeeId(technician.getEmployeeId());
            dto.setIsActive(technician.getActive());
            responseList.add(dto);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), responseList, listRequest.getPageNumber(),
                listRequest.getPageSize());
    }

    private void prepareTechnicianSearchFilter(PageRequest.List listRequest, GenericSpecificationsBuilder<Technician> builder, Long tenantId, boolean isSuperAdmin) {

        builder.with(technicianSpecificationFactory.isEqual("deleted", false));

        builder.with(technicianSpecificationFactory.isEqual("tenantId", tenantId));

        builder.with(technicianSpecificationFactory.isEqual("blocked", false));
        if (org.apache.commons.lang.StringUtils.isNotBlank(listRequest.getSearchText())) {
            builder.with(technicianSpecificationFactory.like("name", listRequest.getSearchText()).or(technicianSpecificationFactory.like("employeeId", listRequest.getSearchText())).or(technicianSpecificationFactory.like("mobileNumber", listRequest.getSearchText()))
                    .or(technicianSpecificationFactory.like("email", listRequest.getSearchText())));

        }
        if (listRequest.getGender() != null) {
            builder.with(technicianSpecificationFactory.isEqual("gender", listRequest.getGender()));
        }
        if (listRequest.getIsActive() != null) {
            builder.with(technicianSpecificationFactory.isEqual("isActive", listRequest.getIsActive()));
        }
        if (listRequest.getStartDate() != null) {
            builder.with(technicianSpecificationFactory.isGreaterThanOrEquals("joinDate", listRequest.getStartDate().atStartOfDay()));
        }

        if (listRequest.getEndDate() != null) {
            builder.with(technicianSpecificationFactory.isLessThanOrEquals("joinDate", listRequest.getEndDate().atTime(23, 59, 59)));
        }

    }

    private void prepareTechnicianSearchFilterForAll(PageRequest.List listRequest, GenericSpecificationsBuilder<Technician> builder, Long tenantId, boolean isSuperAdmin) {

        builder.with(technicianSpecificationFactory.isEqual("deleted", false));

        builder.with(technicianSpecificationFactory.isEqual("tenantId", tenantId));

    }

    public String generateEmployeeId() {
        LocalDate now = LocalDate.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String sequencePart = String.format("%04d", sequence.getAndIncrement());
        return "TECH-" + datePart + "-" + sequencePart;

    }

    public String generateEmployeeUniqeId() {
        String code = "";
        Optional<Technician> technician;
        do {
            String newCode = generateEmployeeId();
            technician = technicianRepository.findByEmployeeId(newCode);
            if (!technician.isPresent()) {
                code = newCode;
            }
        } while (technician.isPresent());
        return code;
    }


    @Override
    public AuthTechnicianDTO fetchAuthenticatedUserDetailsByEmail(String email, Long tenantId, boolean isSuperAdmin) {
        Optional<Technician> user = technicianRepository.findByEmail(email);
        return user.map(TechnicianTransformer.userToAuthDto::apply).orElse(null);
    }


    @Override
    @Transactional(readOnly = true) // Ensures the query is optimized for read
    public Technician getTechnicianByEmailId(String email) {
        Optional<Technician> user = technicianRepository.findByEmail(email);
        return user.orElse(null);
    }

    @Override
    public AuthTechnicianDTO fetchAuthenticatedTechnicianDetailsByEmail(String email) {
        Optional<Technician> user = technicianRepository.findByEmail(email);
        return user.map(TechnicianTransformer.userToAuthDto).orElse(null);
    }

    @Override
    public void changeTechnicianPassword(ChangePasswordDTO passwordDTO, String header) {

    }

    @Override
    public void resetTechnicianPassword(String email) throws CodeException {
        Optional<Technician> user = technicianRepository.findByEmail(email);
        // if user is present then send email with generated OTP
        if (user.isPresent()) {
            user.ifPresent(user1 -> {
                try {
                    userVerificationService.generateUserOtp(user1, UserOtpVerification.Types.FORGOT_PASSWORD);
                } catch (CodeException e) {
                    //throw new UserNotFoundException("Failed to generate OTP for user: " + user1.getEmail());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            throw new CodeException("email does not exist . Please enter a valid email", ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public void resetTechnicianPassword(String token, String newPassword, String confirmPassword) throws CodeException {
        if (isPasswordValid(newPassword)) {
            Optional<UserOtpVerification> userVerificationToken = userVerificationRepository.findByToken(token);

            if (userVerificationToken.isPresent()) {

                Technician user = userVerificationToken.get().getUser();
                if (user != null) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    updateUserVerificationStatus(userVerificationToken, user);
                } else {
                    throw new CodeException("Invalid Request, User object not not found", ErrorCode.COMMON);
                }
            } else {
                throw new CodeException("no given token present for user ", ErrorCode.COMMON);
            }
        } else {
            throw new CodeException("password should  contain one lowercase, uppercase, digit and one special character", ErrorCode.COMMON);
        }
    }

    @Override
    public void updatePassword(TechnicianDetailDTO.ChangePassword changePassword, Technician loggedIntechnician, Long tenantId, boolean isSuperAdmin) throws CodeException {
        if (isPasswordValid(changePassword.getNewPassword())) {
            // new password and confirm password should be same
            if (changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
                // Validate user with current password, if user has entered wrong current password then throw error
                if (passwordEncoder.matches(changePassword.getCurrentPassword(), loggedIntechnician.getPassword())) {
                    // encode user password
                    loggedIntechnician.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
                    loggedIntechnician.setUpdatedAt(LocalDateTime.now());
                    technicianRepository.save(loggedIntechnician);
                } else {
                    throw new CodeException("The current password you entered is incorrect.", ErrorCode.BAD_REQUEST);
                }

            } else {
                throw new CodeException("Confirm Password field is not matching with new password field.", ErrorCode.BAD_REQUEST);
            }
        } else {
            throw new CodeException("password should  contain one lowercase, uppercase, digit and one special character", ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public TechnicianDetailDTO updateProfile(Technician technician, TechnicianDetailDTO technicianDetailDTO, Long tenantId, boolean isSuperAdmin) throws CodeException {
        if (isSuperAdmin) {
            tenantId = 1l;
        }
        if (TextUtils.isEmpty(technicianDetailDTO.getProfileImage()))
            throw new CodeException("profile image is required", ErrorCode.BAD_REQUEST);
        technician.setProfilePicture(awsS3BaseUrl + technicianDetailDTO.getProfileImage());
        technicianRepository.save(technician);
        TechnicianDetailDTO response = new TechnicianDetailDTO();
        response.setId(technician.getUuid());
        response.setTechnicianId(technician.getEmployeeId());
        response.setFullName(technician.getName());
        response.setEmail(technician.getEmail());
        response.setContactNumber(technician.getMobileNumber());
        response.setPushEnabled(Optional.ofNullable(technician)
                .map(Technician::getMultiUserDeviceDetails)
                .map(MultiUserDeviceDetails::getPushEnabled)
                .orElse(false));
        response.setProfileImage(technician.getProfilePicture());
        return response;
    }

    @Override
    public TechnicianDetailDTO getProfileDetails(String id) throws CodeException {
        Optional<Technician> user = technicianRepository.findByUuid(id);
        if (user.isEmpty())
            throw new CodeException("User not found.", ErrorCode.COMMON);
        TechnicianDetailDTO response = new TechnicianDetailDTO();
        response.setId(user.get().getUuid());
        response.setTechnicianId(user.get().getEmployeeId());
        response.setFullName(user.get().getName());
        response.setEmail(user.get().getEmail());
        response.setContactNumber(user.get().getMobileNumber());
        response.setPushEnabled(Optional.ofNullable(user.get())
                .map(Technician::getMultiUserDeviceDetails)
                .map(MultiUserDeviceDetails::getPushEnabled)
                .orElse(false));
        response.setProfileImage(user.get().getProfilePicture());
        return response;
    }

    @Override
    public ResponseEntity<ApiResponse> getStaticContentBySlug(String slug, Long tenantId, boolean isSuperAdmin) throws CodeException {
        try {
            ResponseEntity<ApiResponse> response = adminClient.getBySlug(slug, tenantId, isSuperAdmin);

            return response;

        } catch (FeignException e) {
            throw new CodeException("Remote admin-service failed: " + e.contentUTF8(), ErrorCode.COMMON);
        }
    }

    @Override
    public void verifyResetToken(String token) throws CodeException {
        Optional<UserOtpVerification> userOtpVerification = userVerificationRepository.findByToken(token);
        if (userOtpVerification.isEmpty()) {
            throw new CodeException("Invalid link ", ErrorCode.BAD_REQUEST);
        } else {
            if (LocalDateTime.now().isAfter(userOtpVerification.get().getExpiredDateTime())) {
                throw new CodeException("Password reset link is expired", ErrorCode.BAD_REQUEST);
            }
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAnnouncements(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin) throws CodeException {
        listRequest.setIsActive(true);
        try {
            ResponseEntity<ApiResponse> response = adminClient.getAllAnnouncementsForTechnician(listRequest, tenantId, isSuperAdmin);

            return response;

        } catch (FeignException e) {
            throw new CodeException("Remote admin-service failed: " + e.contentUTF8(), ErrorCode.COMMON);
        }
    }

    @Override
    public Set<MultiUserDeviceDetailsDTO.Response> getTechniciansNotificationsData(TechnicianNotificationRequest notificationRequest) throws CodeException {
        List<MultiUserDeviceDetails> multiUserDeviceDetails = new ArrayList<>();
        if (notificationRequest.getUserGroup().equals(NotificationUserGroup.ALL_USER)) {
            multiUserDeviceDetails = technicianRepository.findByDeviceTokenNotNullAndDeviceTypeNotNullAndDeviceTokenNot("");
        } else if (notificationRequest.getUserGroup().equals(NotificationUserGroup.ALL_ANDROID_USER)) {
            multiUserDeviceDetails = technicianRepository.findByDeviceTypeIgnoreCaseAndDeviceTokenIsNotNullAndDeviceTokenNot("android", "");

        } else if (notificationRequest.getUserGroup().equals(NotificationUserGroup.ALL_IOS_USER)) {
            multiUserDeviceDetails = technicianRepository.findByDeviceTypeIgnoreCaseAndDeviceTokenIsNotNullAndDeviceTokenNot("iOS", "");

        } else if (notificationRequest.getUserGroup().equals(NotificationUserGroup.PARTICULAR_USER)) {

            multiUserDeviceDetails = technicianRepository.findByUserIdIn(notificationRequest.getUserIds());

        }
        if (multiUserDeviceDetails.isEmpty()) {
            throw new CodeException("No Active users found to send notification", ErrorCode.COMMON);
        }
        Set<MultiUserDeviceDetailsDTO.Response> deviceDetailsDTOS = new HashSet<>();
        for (MultiUserDeviceDetails userDeviceDetails : multiUserDeviceDetails) {
            MultiUserDeviceDetailsDTO.Response dto = new MultiUserDeviceDetailsDTO.Response();
            dto.setDeviceType(userDeviceDetails.getDeviceType());
            dto.setDeviceToken(userDeviceDetails.getDeviceToken());
            dto.setAppVersion(userDeviceDetails.getAppVersion());
            dto.setDeviceId(userDeviceDetails.getDeviceId());
            deviceDetailsDTOS.add(dto);
        }
        return deviceDetailsDTOS;
    }

    @Override
    public ResponseEntity<ApiResponse> getNotificationList(UserNotificationListDTO.ListRequest listRequest, Technician loggedIntechnician) throws CodeException {
        if (TextUtils.isEmpty(listRequest.getSort()))
            listRequest.setSort("createdAt");
        listRequest.setUserId(loggedIntechnician.getUuid());
        listRequest.setType("TECHNICIAN");
        try {
            ResponseEntity<ApiResponse> response = notificationClient.getUserNotificationList(listRequest);

            return response;

        } catch (FeignException e) {
            throw new CodeException("Remote notification-service failed: " + e.contentUTF8(), ErrorCode.COMMON);
        }
    }


    private void updateUserVerificationStatus(Optional<UserOtpVerification> userVerificationToken, Technician user) throws CodeException {

        if (userVerificationToken.isPresent()) {
            // check verification token is expired or not, if token is expired then throw
            // exception
            if (LocalDateTime.now().isBefore(userVerificationToken.get().getExpiredDateTime())) {
                user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.get().setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED);
                userVerificationToken.get().setConfirmedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.get().setExpiredDateTime(userVerificationToken.get().getExpiredDateTime().minusDays(2));
                userVerificationToken.get().setActive(false);
                List<UserOtpVerification> otpVerifications = new ArrayList<>();
                otpVerifications.add(userVerificationToken.get());
                technicianRepository.save(user);
            } else {
                throw new CodeException("Security key is expired", ErrorCode.COMMON);
            }
        }
    }

    @Override
    public JobDashboardResponseDTO.Detail countTechnician(JobDashboardResponseDTO.Search search) throws CodeException {
        if (search.getStartDate() == null || search.getEndDate() == null) {
            throw new CodeException("Start date and end date are required", ErrorCode.COMMON);
        }
        long count = technicianRepository.countByAndCreatedAtBetween(search.getStartDate().atStartOfDay(), search.getEndDate().atTime(23, 59, 59));
        JobDashboardResponseDTO.Detail st = new JobDashboardResponseDTO.Detail();
        st.setTotalNoOfTechnician(count);
        return st;
    }

    @Override
    public ResponseEntity<ApiResponse> getFrontOfficeDevices(String id, Long tenantId) throws CodeException {
        return adminClient.getFrontOfficeDevices(id, tenantId);
    }

    @Override
    public List<MultiUserDeviceDetails> getAllTechnicianDevices(Long tenantId) {
        GenericSpecificationsBuilder<Technician> builder = new GenericSpecificationsBuilder<>();
        builder.with(technicianSpecificationFactory.isEqual("tenantId", tenantId));
        builder.with(technicianSpecificationFactory.isEqual("deleted", false));
        List<Technician> technicianList = technicianRepository.findAll(builder.build());

        return technicianList.stream()
                .map(Technician::getMultiUserDeviceDetails)
                .filter(Objects::nonNull)
                .filter(device -> device.getDeviceToken() != null && !device.getDeviceToken().isEmpty())
                .filter(device -> device.getDeviceType() != null && !device.getDeviceType().isEmpty())
                .collect(Collectors.toList());

    }

    @Override
    public List<TechnicianDto.list> getAllTechByIds(List<String> ids) throws CodeException {
        List<Technician> byUuid = technicianRepository.findByUuid(ids);
        List<TechnicianDto.list> result = new ArrayList<>();
        for (Technician t : byUuid) {
            TechnicianDto.list dto = new TechnicianDto.list();
            dto.setId(t.getUuid());
            dto.setName(t.getName());
            dto.setEmail(t.getEmail());
            dto.setMobileNumber(t.getMobileNumber());
            dto.setEmployeeId(t.getEmployeeId());
            dto.setProfilePicture(t.getProfilePicture());
            dto.setIsActive(t.getActive());
            dto.setJoinedDate(t.getJoinDate() != null ? t.getJoinDate().toString() : null);
            dto.setRoleName(t.getRole().getName());
            result.add(dto);
        }
        return result;
    }

    @Override
    public PageItem<TechnicianDto.list> getAllTech(PageRequest.List listRequest, Long tenantId, boolean isSuperAdmin) throws CodeException {
        GenericSpecificationsBuilder<Technician> builder = new GenericSpecificationsBuilder<>();
        Pageable pageable = null;
        if (Boolean.TRUE.equals(listRequest.getAsc())) {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).ascending());
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).descending());
        }
//        prepareTechnicianSearchFilterForAll(listRequest, builder, tenantId, isSuperAdmin);
        if (listRequest.getIsActive() != null)
            builder.with(technicianSpecificationFactory.isEqual("isActive", listRequest.getIsActive()));
        Page<Technician> pagedResult = technicianRepository.findAll(builder.build(), pageable);

        List<TechnicianDto.list> responseList = new ArrayList<>();
        for (Technician t : pagedResult.getContent()) {
            TechnicianDto.list dto = new TechnicianDto.list();
            dto.setId(t.getUuid());
            dto.setName(t.getName());
            dto.setEmail(t.getEmail());
            dto.setMobileNumber(t.getMobileNumber());
            dto.setEmployeeId(t.getEmployeeId());
            dto.setProfilePicture(t.getProfilePicture());
            dto.setIsActive(t.getActive());
            dto.setJoinedDate(t.getJoinDate() != null ? t.getJoinDate().toString() : null);
            dto.setRoleName(t.getRole().getName());
            responseList.add(dto);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), responseList, listRequest.getPageNumber(),
                listRequest.getPageSize());
    }

    @Override
    public Boolean notificationToggle(Technician loggedIntechnician, Long tenantId, boolean isSuperAdmin) {
        MultiUserDeviceDetails multiUserDeviceDetails = loggedIntechnician.getMultiUserDeviceDetails();
        if (multiUserDeviceDetails != null) {
            Boolean pushEnabled = multiUserDeviceDetails.getPushEnabled();
            if (pushEnabled != null && pushEnabled) {
                multiUserDeviceDetails.setPushEnabled(false);
            } else {
                multiUserDeviceDetails.setPushEnabled(true);
            }
            technicianRepository.save(loggedIntechnician);
            return multiUserDeviceDetails.getPushEnabled();
        }
        return false;
    }

    @Override
    public List<RoleDTO> getRoleList(Long tenantId, boolean isSuperAdmin) {
        List<Role>roleList= roleRepository.findAll();
        List<RoleDTO> responseList=new ArrayList<>();
        for(Role role:roleList) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getUuid());
            roleDTO.setName(role.getName());
            responseList.add(roleDTO);
        }
        return responseList;
    }

}
