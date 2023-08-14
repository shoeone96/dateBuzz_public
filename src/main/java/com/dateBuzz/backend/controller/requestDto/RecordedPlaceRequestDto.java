package com.dateBuzz.backend.controller.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordedPlaceRequestDto {

    private int orders;
    private String placeName;
    private String address;
    private String addressGu;
    private String addressX;
    private String addressY;
    private String placeContent;

    private List<PlaceImageRequestDto> images;


}
