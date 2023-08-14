package com.dateBuzz.backend.controller.requestDto.modify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModifyUserInfoRequestDto {
    private String nickname;
    private byte[] profileImg;
    private String fileName;
    private String contentType;
}
