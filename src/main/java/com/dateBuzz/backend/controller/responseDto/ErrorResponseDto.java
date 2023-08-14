package com.dateBuzz.backend.controller.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ErrorResponseDto {
    private String errorMessage;
    private String httpStatus;


}
