package com.dateBuzz.backend.controller.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BookmarkResponseDto {

    private int bookmarkStatus;
    private int bookmarkCnt;

}
