package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.responseDto.LikeResponseDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    @PostMapping("community/{recordId}/likes")
    public Response<LikeResponseDto> likeRecord(@PathVariable Long recordId, Authentication authentication){
        return Response.success(likeService.like(recordId, authentication.getName()));
    }
}
