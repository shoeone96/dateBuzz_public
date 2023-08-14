package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.FollowRequestDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public Response<Void> follow(@RequestBody FollowRequestDto requestDto, Authentication authentication){
        return Response.success(followService.follow(requestDto, authentication.getName()));
    }

}
