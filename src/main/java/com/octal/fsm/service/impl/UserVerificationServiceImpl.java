package com.octal.fsm.service.impl;


import com.octal.fsm.dto.EmailDTO;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.UserOtpVerification;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.repositories.TechnicianRepository;
import com.octal.fsm.repositories.UserVerificationRepository;
import com.octal.fsm.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class UserVerificationServiceImpl implements UserVerificationService {
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private UserVerificationRepository userOtpVerificationRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private EmailService emailService;

    @Value("${forgot.password.time.in.second}")
    private String forgetPasswordOtpTimeInSec;

    @Value("${forgot.password.otp.resend.count}")
    private String forgetPasswordOtpAttemptsCounts;

    @Value("${user.forget.password.link}")
    private String forgetPasswordLink;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserOtpVerification generateUserOtp(Technician user, UserOtpVerification.Types types) throws CodeException, MessagingException {
        UserOtpVerification registrationToken;
        if (user != null) {
            Optional<UserOtpVerification> userOtpVerificationObj = userOtpVerificationRepository.findByTypeAndUser(UserOtpVerification.Types.FORGOT_PASSWORD, user);
            if (userOtpVerificationObj.isPresent()) {
                registrationToken = mapUserOtpObject(user, types, userOtpVerificationObj.get());
            } else {
                UserOtpVerification userOtpVerification = new UserOtpVerification();
                userOtpVerification.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                registrationToken = mapUserOtpObject(user, types, userOtpVerification);

            }
            return registrationToken;
        } else {
            throw new CodeException("verification.otp.creation.failed", ErrorCode.COMMON);
        }
    }

    @Override
    public UserOtpVerification resendUserOtp(Technician user, UserOtpVerification.Types types) {
        return null;
    }

    private UserOtpVerification mapUserOtpObject(Technician user, UserOtpVerification.Types types, UserOtpVerification userOtpVerification) throws CodeException, MessagingException {
        // generate random otp
        // generate one random number with 6 digit
        String otp = String.valueOf(random.nextInt(100000));
        userOtpVerification.setActonCount(userOtpVerification.getActonCount() != null ? userOtpVerification.getActonCount() + 1 : 1);

        if (userOtpVerification.getType() != null && userOtpVerification.getType().equals(UserOtpVerification.Types.FORGOT_PASSWORD) && userOtpVerification.getUpdatedAt() != null) {
            // convert localDateTime to date to get epoch time
            Long currentTimeInSec = Date.from(LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneId.systemDefault()).toInstant()).getTime();
            Long actionTime = Date.from(userOtpVerification.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()).getTime();

            Long differenceOfTime = (currentTimeInSec - actionTime) / 1000;
            Long forgotTimeInSec = Long.valueOf(forgetPasswordOtpTimeInSec);

            if (differenceOfTime <= forgotTimeInSec && userOtpVerification.getActonCount() > Integer.parseInt(forgetPasswordOtpAttemptsCounts)) {
                throw new CodeException("Forgot Password was attempted recently. \n" + "Subsequent attempts can be made after " + TimeUnit.SECONDS.toMinutes(forgotTimeInSec) + " Minutes.", ErrorCode.COMMON);
            }

            if (differenceOfTime > Long.valueOf(forgetPasswordOtpTimeInSec)) {
                userOtpVerification.setActonCount(1);
            }

        }
        String token = UUID.randomUUID().toString();
        userOtpVerification.setToken(token);
        userOtpVerification.setOtp(otp);
        userOtpVerification.setPhoneNumber(user.getMobileNumber());
        //userOtpVerification.setExtensionNumber(user.getExtensionNumber());
        userOtpVerification.setType(types);
        userOtpVerification.setIssuedDateTime(LocalDateTime.now(ZoneOffset.UTC));
        userOtpVerification.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        userOtpVerification.setExpiredDateTime(userOtpVerification.getIssuedDateTime().plusDays(1));
        userOtpVerification.setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_PENDING);
        userOtpVerification.setUser(user);
        userOtpVerification.setActive(Boolean.TRUE);
        UserOtpVerification registrationToken = userOtpVerificationRepository.save(userOtpVerification);

        EmailDTO mail = new EmailDTO();
        mail.setMailTo(user.getEmail());
        mail.setSubject("[fsm] Forget password");
        mail.setTemplateName("FORGOT_PASSWORD");
        Map<String, Object> model = new HashMap<>();
        // model.put("name", user.getFirstName() + " " + user.getLastName());
        model.put("location", "India");
        model.put("sign", "FSM Team");
        model.put("link", forgetPasswordLink.replace("<token>", token));
        model.put("otp", otp);
        model.put("#USER", user.getName());
        model.put("#LINK", forgetPasswordLink.replace("<token>", token));
        model.put("SUPPORT_EMAIL", "support@fsm.com");
        mail.setProps(model);
        emailService.sendMail(mail);
        // todo: send email to user for forget password code needs to be implemented
        technicianRepository.save(user);
        return registrationToken;
    }

    @Override
    @Transactional
    public UserOtpVerification createUserVerificationToken(Technician user) throws CodeException {

        if (user != null) {
            UserOtpVerification userVerificationToken = new UserOtpVerification();
            // generate random token
            userVerificationToken.setToken(UUID.randomUUID().toString());
            userVerificationToken.setIssuedDateTime(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setType(UserOtpVerification.Types.NEW_USER);
            userVerificationToken.setExpiredDateTime(userVerificationToken.getIssuedDateTime().plusDays(1));
            userVerificationToken.setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_PENDING);
            userVerificationToken.setUser(user);
            userVerificationToken.setActive(true);
            // in user verification we are not validation user with otp so we have added 0 as default value
            userVerificationToken.setOtp("0");
            return userOtpVerificationRepository.save(userVerificationToken);
        } else {
            throw new CodeException("verification.token.creation.failed", ErrorCode.COMMON);
        }

    }
}