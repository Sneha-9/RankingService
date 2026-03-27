package com.sneha.exceptions;

import com.sneha.Constant;
import com.sneha.errorservice.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();


    @Test
    void shouldTestValidationException(){
        SystemException systemException = new SystemException(Constant.SYSTEM_EXCEPTION_MESSAGE);

        ErrorResponse errorResponse = ErrorResponse.newBuilder()
                .setMessage(Constant.SYSTEM_EXCEPTION_MESSAGE)
                .build();

        ResponseEntity<ErrorResponse> responseEntity = handler.handleSystemException(systemException);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assertions.assertEquals(errorResponse, responseEntity.getBody());
    }

}