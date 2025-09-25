package com.octal.fsm.models.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageHelper {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String messageKey, Object[] arguments) {
        return messageSource.getMessage(messageKey, arguments, Locale.getDefault());
    }

    public String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
