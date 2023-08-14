package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.responseDto.RecordResponseDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.SearchService;
import com.dateBuzz.backend.util.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public Response<CustomPage<RecordResponseDto>> findByHashtag(@RequestParam String hashtagName, @PageableDefault(size = 5, sort = "recordedID", direction = Sort.Direction.ASC) Pageable pageable){
        return Response.success(CustomPage.of(searchService.findByTag(hashtagName, pageable)));
    }
}
