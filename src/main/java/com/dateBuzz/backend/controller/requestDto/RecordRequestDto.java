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
public class RecordRequestDto {
    private String title;
    private String dateDate;
    private String recordedContent;
    private List<HashtagRequestDto> hashtags;
    private List<RecordedPlaceRequestDto> recordedPlaces;
    private String exposure;

}
