package com.octal.fsm.service;


import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.UserOtpVerification;
import com.octal.fsm.exceptions.CodeException;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public interface UserVerificationService {


    UserOtpVerification generateUserOtp(Technician user, UserOtpVerification.Types types) throws CodeException, MessagingException;

    UserOtpVerification resendUserOtp(Technician user, UserOtpVerification.Types types);

    UserOtpVerification createUserVerificationToken(Technician user) throws CodeException;
}