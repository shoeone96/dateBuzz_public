package com.dateBuzz.backend.controller.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceImageRequestDto {

    private int orders;
    private byte[] imgFormData;
    private String fileName;
    private String contentType;
}
