package com.octal.fsm.service.impl;

import com.octal.fsm.clients.AdminClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.common.CommonConstants;
import com.octal.fsm.dto.*;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.UserOtpVerification;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.repositories.TechnicianRepository;
import com.octal.fsm.repositories.UserVerificationRepository;
import com.octal.fsm.service.TechnicianService;
import com.octal.fsm.service.UserVerificationService;
import com.octal.fsm.specification.GenericSpecificationsBuilder;
import com.octal.fsm.specification.SpecificationFactory;
import com.octal.fsm.utils.TechnicianTransformer;
import com.octal.fsm.utils.TextUtils;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TechnicianServiceImpl implements TechnicianService {

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

    public static boolean isPasswordValid(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])(.{8,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public String addTechnician(TechnicianDto.Add add) throws CodeException {
        if (TextUtils.isEmpty(add.getName()))
            throw new CodeException("name is required", ErrorCode.COMMON);
        if (TextUtils.isEmpty(add.getMobileNumber()))
            throw new CodeException("mobile number is required", ErrorCode.COMMON);
        if (TextUtils.isEmpty(add.getEmail()))
            throw new CodeException("email id is required", ErrorCode.COMMON);
        if (TextUtils.isEmpty(add.getAddress()))
            throw new CodeException("address is required", ErrorCode.COMMON);
        if(add.getGender()==null)
            throw new CodeException("gender is required", ErrorCode.COMMON);
        Optional<Technician> optionalTechnician = technicianRepository.findByUuid(add.getId());
        if (optionalTechnician.isPresent() && !optionalTechnician.get().getUuid().equals(add.getId())) {
            throw new CodeException("technician name is already present!", ErrorCode.RECORD_NOT_FOUND);
        }
        Technician newtechnicianRecord = null;
        if (TextUtils.isEmpty(add.getId())) {
            if(technicianRepository.existsByMobileNumber(add.getMobileNumber()))
                throw new CodeException("Technician with mobile number "+add.getMobileNumber()+" already exists",ErrorCode.RECORD_NOT_FOUND);
            if(technicianRepository.existsByEmail(add.getEmail()))
                throw new CodeException("Technician with email "+add.getEmail()+" already exists",ErrorCode.RECORD_NOT_FOUND);
            newtechnicianRecord = new Technician();
            newtechnicianRecord.setCreatedAt(LocalDateTime.now());
            newtechnicianRecord.setUpdatedAt(LocalDateTime.now());
            newtechnicianRecord.setEmployeeId(generateEmployeeUniqeId());
            newtechnicianRecord.setPassword(passwordEncoder.encode("Technician@123"));
            newtechnicianRecord.setActive(true);
        } else {
            Optional<Technician> technician = technicianRepository.findByUuid(add.getId());
            Optional<Technician>optional=technicianRepository.findByMobileNumber(add.getMobileNumber());
            Optional<Technician>optionalTechnician1=technicianRepository.findByEmail(add.getEmail());
            if(optionalTechnician1.isPresent() && !optionalTechnician1.get().getUuid().equals(add.getId()))
                throw new CodeException("Technician with email "+add.getEmail()+" already exists",ErrorCode.RECORD_NOT_FOUND);
            if(optional.isPresent() && !optional.get().getUuid().equals(add.getId()))
                throw new CodeException("Technician with mobile number "+add.getMobileNumber()+" already exists",ErrorCode.RECORD_NOT_FOUND);
            if (technician.isPresent()) {
                newtechnicianRecord = technician.get();
                newtechnicianRecord.setUpdatedAt(LocalDateTime.now());
                newtechnicianRecord.setActive(add.getIsActive());
            } else {
                throw new CodeException("technician not Found!", ErrorCode.COMMON);
            }
        }
        newtechnicianRecord.setDeleted(false);
        newtechnicianRecord.setEmail(add.getEmail());
        newtechnicianRecord.setMobileNumber(add.getMobileNumber());
        newtechnicianRecord.setName(add.getName());
        //newtechnicianRecord.setAddress(new Address(add.getAddress().getStreet(), add.getAddress().getCity(), add.getAddress().getState(), add.getAddress().getPostalCode(), add.getAddress().getCountry()));
        newtechnicianRecord.setAddress(add.getAddress());
        newtechnicianRecord.setGender(add.getGender());
        newtechnicianRecord.setJoinDate(add.getJoinedDate().atStartOfDay());
        Technician technician = technicianRepository.save(newtechnicianRecord);
        return technician.getUuid();
    }


    @Override
    public Boolean deleteById(String id) throws CodeException {
        Optional<Technician> technicianRecord = technicianRepository.findByUuid(id);
        if (technicianRecord.isPresent()) {
            technicianRecord.get().setDeleted(true);
            technicianRepository.save(technicianRecord.get());
            return true;
        } else {
            throw new CodeException(CommonConstants.TECHNICIAN_NOT_FOUND + id, ErrorCode.COMMON);
        }
    }

    @Override
    public TechnicianDto.list getTechnicianByUuid(String id) throws CodeException {
        Optional<Technician> technicianRecord = technicianRepository.findByUuid(id);
        if (technicianRecord.isPresent()) {
            TechnicianDto.list technician = new TechnicianDto.list();
            technician.setEmployeeId(technicianRecord.get().getEmployeeId());
            technician.setEmail(technicianRecord.get().getEmail());
            technician.setId(technicianRecord.get().getUuid());
            technician.setName(technicianRecord.get().getName());
            //technician.setAddress(new AddressDTO(technicianRecord.get().getAddress().getStreet(), technicianRecord.get().getAddress().getCity(), technicianRecord.get().getAddress().getState(), technicianRecord.get().getAddress().getPostalCode(), technicianRecord.get().getAddress().getCountry()));
            technician.setAddress(technicianRecord.get().getAddress());
            technician.setMobileNumber(technicianRecord.get().getMobileNumber());
            technician.setProfilePicture(technicianRecord.get().getProfilePicture());
            technician.setIsActive(technicianRecord.get().getActive());
            technician.setJoinedDate(technicianRecord.get().getJoinDate()!=null?technicianRecord.get().getJoinDate().toString():LocalDateTime.now().toString());
            technician.setCreatedAt(technicianRecord.get().getCreatedAt().toString());
            technician.setUpdatedAt(technicianRecord.get().getUpdatedAt().toString());
            technician.setGender(technicianRecord.get().getGender());
            return technician;
        } else {
            throw new CodeException(CommonConstants.TECHNICIAN_NOT_FOUND + id, ErrorCode.COMMON);
        }
    }

    @Override
    public Boolean changeStatus(String id) throws CodeException {
        Optional<Technician> technicianRecord = technicianRepository.findByUuid(id);
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
    public PageItem<TechnicianDto.list> getAllTechnician(PageRequest.List listRequest) {
        String trimmedText = listRequest.getSearchText().trim();
        listRequest.setSearchText(trimmedText);
        GenericSpecificationsBuilder<Technician> builder = new GenericSpecificationsBuilder<>();
        Pageable pageable = null;
        if (Boolean.TRUE.equals(listRequest.getAsc())) {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).ascending());
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(listRequest.getPageNumber(), listRequest.getPageSize(), Sort.by(listRequest.getShortingField()).descending());
        }
        prepareTechnicianSearchFilter(listRequest, builder);
        Page<Technician> pagedResult = technicianRepository.findAll(builder.build(), pageable);
        List<TechnicianDto.list> responseList = new ArrayList<>();
        for(Technician technician:pagedResult.getContent()){
            TechnicianDto.list dto=new TechnicianDto.list();
            dto.setId(technician.getUuid());
            dto.setName(technician.getName());
            dto.setEmail(technician.getEmail());
            dto.setMobileNumber(technician.getMobileNumber());
            //dto.setAddress(new AddressDTO(technician.getAddress().getStreet(), technician.getAddress().getCity(), technician.getAddress().getState(), technician.getAddress().getPostalCode(), technician.getAddress().getCountry()));
            dto.setAddress(technician.getAddress());
            dto.setIsActive(technician.getActive());
            dto.setCreatedAt(String.valueOf(technician.getCreatedAt()));
            dto.setEmployeeId(technician.getEmployeeId());
            dto.setUpdatedAt(technician.getUpdatedAt().toString());
            dto.setJoinedDate(technician.getJoinDate()!=null?technician.getJoinDate().toString():LocalDateTime.now().toString());
            dto.setGender(technician.getGender());
            responseList.add(dto);
        }

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), responseList, listRequest.getPageNumber(),
                listRequest.getPageSize());
    }

    private void prepareTechnicianSearchFilter(PageRequest.List listRequest, GenericSpecificationsBuilder<Technician> builder) {


        builder.with(technicianSpecificationFactory.isEqual("deleted", false));

        if (org.apache.commons.lang.StringUtils.isNotBlank(listRequest.getSearchText())) {
            builder.with(technicianSpecificationFactory.like("name", listRequest.getSearchText()).or(technicianSpecificationFactory.like("employeeId", listRequest.getSearchText())).or(technicianSpecificationFactory.like("mobileNumber", listRequest.getSearchText()))
                    .or(technicianSpecificationFactory.like("email", listRequest.getSearchText())));

        }
        if(listRequest.getGender()!=null){
            builder.with(technicianSpecificationFactory.isEqual("gender", listRequest.getGender()));
        }
        if(listRequest.getIsActive()!=null){
            builder.with(technicianSpecificationFactory.isEqual("isActive", listRequest.getIsActive()));
        }
        if (listRequest.getStartDate() != null) {
            builder.with(technicianSpecificationFactory.isGreaterThanOrEquals("joinDate", listRequest.getStartDate().atStartOfDay()));
        }

        if (listRequest.getEndDate() != null) {
            builder.with(technicianSpecificationFactory.isLessThanOrEquals("joinDate", listRequest.getEndDate().atTime(23,59,59)));
        }

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
    public AuthTechnicianDTO fetchAuthenticatedUserDetailsByEmail(String email) {
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
    public void updatePassword(TechnicianDetailDTO.ChangePassword changePassword, Technician loggedIntechnician) throws CodeException {

    }

    @Override
    public void updateProfile(TechnicianDetailDTO admintechnicianDetailDTO, MultipartFile profileImage) throws CodeException {

    }

    @Override
    public Object getProfileDetails(String id) throws CodeException {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> getStaticContentBySlug(String slug) throws CodeException {
        try {
            ResponseEntity<ApiResponse> response = adminClient.getBySlug(slug);

            return response;

        } catch (FeignException e) {
            throw new CodeException("Remote admin-service failed: " + e.contentUTF8(), ErrorCode.COMMON);
        }
    }

    @Override
    public void verifyResetToken(String token) throws CodeException {
        Optional<UserOtpVerification>userOtpVerification=userVerificationRepository.findByToken(token);
        if(userOtpVerification.isEmpty()){
            throw new CodeException("Invalid link ",ErrorCode.BAD_REQUEST);
        }else{
            if(LocalDateTime.now().isAfter(userOtpVerification.get().getExpiredDateTime())){
                throw new CodeException("Password reset link is expired",ErrorCode.BAD_REQUEST);
            }
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


}
