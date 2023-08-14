package com.dateBuzz.backend.controller.responseDto;

import com.dateBuzz.backend.model.entity.RecordedPlaceEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecordedPlaceResponseDto {

    private Long placeId;
    private Long recordId;
    private int orders;
    private String placeName;
    private String address;
    private String addressGu;
    private String addressX;
    private String addressY;
    private String placeContent;
    private List<PlaceImgResponseDto> placeImages;

    public static RecordedPlaceResponseDto fromRecordedPlace(RecordedPlaceEntity recordedPlace, List<PlaceImgResponseDto> images){
        return new RecordedPlaceResponseDto(
                recordedPlace.getId(),
                recordedPlace.getRecord().getId(),
                recordedPlace.getOrders(),
                recordedPlace.getPlaceName(),
                recordedPlace.getAddress(),
                recordedPlace.getAddressGu(),
                recordedPlace.getAddressX(),
                recordedPlace.getAddressY(),
                recordedPlace.getPlaceContent(),
                images
        );
    }
}
