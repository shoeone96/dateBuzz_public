package com.dateBuzz.backend.controller.responseDto;

import com.dateBuzz.backend.model.entity.PlaceImgEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PlaceImgResponseDto {

    private String imageUrl;
    private int orders;

    public static PlaceImgResponseDto fromRecordedPlace(PlaceImgEntity image){
        return new PlaceImgResponseDto(
                image.getImgUrl(),
                image.getOrders()
        );
    }
}
