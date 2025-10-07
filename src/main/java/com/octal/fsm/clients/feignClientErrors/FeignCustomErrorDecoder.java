package com.octal.fsm.clients.feignClientErrors;

import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FeignCustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String message;
        try {
            message = response.body() != null ? Util.toString(response.body().asReader()) : response.reason();
        } catch (IOException e) {
            message = response.reason();
        }
        return new CodeException("Feign call failed: " + message, ErrorCode.COMMON);
    }

}
