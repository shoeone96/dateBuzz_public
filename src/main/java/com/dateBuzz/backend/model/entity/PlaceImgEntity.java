package com.dateBuzz.backend.model.entity;

import com.dateBuzz.backend.controller.requestDto.PlaceImageRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "\"place_img\"")
@Entity
@Getter
@NoArgsConstructor
public class PlaceImgEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private RecordedPlaceEntity recordedPlace;

    private int orders;

    private String imgUrl;

    @Builder
    public PlaceImgEntity(RecordedPlaceEntity recordedPlace, int orders, String imgUrl) {
        this.recordedPlace = recordedPlace;
        this.orders = orders;
        this.imgUrl = imgUrl;
    }


    public static PlaceImgEntity FromPlaceImgRequestDto(RecordedPlaceEntity recordedPlace, PlaceImageRequestDto imageRequestDto, String url) {
        return new PlaceImgEntity(
                recordedPlace,
                imageRequestDto.getOrders(),
                url
        );
    }
}
