package com.dateBuzz.backend.controller.requestDto.modify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyRecordRequestDto {
    private String title;
    private String dateDate;
    private String recordedContent;

}
