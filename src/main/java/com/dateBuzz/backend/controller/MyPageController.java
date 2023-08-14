package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.responseDto.RecordResponseDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.controller.responseDto.FollowListResponseDto;
import com.dateBuzz.backend.service.RecordService;
import com.dateBuzz.backend.util.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final RecordService recordService;

    @GetMapping("/myPage/myRecord")
    public Response<CustomPage<RecordResponseDto>> getMyRecord(@PageableDefault(size = 5, sort = "recordedID", direction = Sort.Direction.ASC) Pageable pageable, Authentication authentication){
        return Response.success(CustomPage.of(recordService.getMyRecord(pageable, authentication.getName())));
    }

    @GetMapping("/myPage/myBookmark")
    public Response<CustomPage<RecordResponseDto>> getMyBookmarkRecord(@PageableDefault(size = 5, sort = "recordedID", direction = Sort.Direction.ASC) Pageable pageable, Authentication authentication){
        return Response.success(CustomPage.of(recordService.getMyBookmarkRecord(pageable, authentication.getName())));
    }

    @GetMapping("/myPage/myFollow")
    public Response<List<FollowListResponseDto>> getMyFollower(Authentication authentication){
        return Response.success(recordService.getMyFollower(authentication.getName()));
    }
}
