package com.dateBuzz.backend.controller.requestDto.modify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyRecordedPlaceRequestDto {
    private List<ModifyRecordedPlaceInfoRequestDto> modifyInfo;
}
