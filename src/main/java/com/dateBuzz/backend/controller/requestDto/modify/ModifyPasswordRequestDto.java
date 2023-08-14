package com.dateBuzz.backend.controller.requestDto.modify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModifyPasswordRequestDto {

    private String newPassword;
    private String oldPassword;
}
