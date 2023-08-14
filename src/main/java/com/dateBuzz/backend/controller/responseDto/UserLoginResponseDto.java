package com.dateBuzz.backend.controller.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginResponseDto {

    private String token;
    private String nickname;
}
