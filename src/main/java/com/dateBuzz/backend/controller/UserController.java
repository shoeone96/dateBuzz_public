package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.UserJoinRequestDto;
import com.dateBuzz.backend.controller.requestDto.UserLoginRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.ModifyPasswordRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.ModifyUserInfoRequestDto;
import com.dateBuzz.backend.controller.responseDto.*;
import com.dateBuzz.backend.service.RecordService;
import com.dateBuzz.backend.service.UserService;
import com.dateBuzz.backend.util.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RecordService recordService;

    @PostMapping("/join")
    public Response<UserJoinResponseDto> join(@RequestBody UserJoinRequestDto requestDto){
        UserJoinResponseDto responseDto = userService.join(requestDto);
        return Response.success(responseDto);
    }

    @PostMapping("/login")
    public Response<UserLoginResponseDto> join(@RequestBody UserLoginRequestDto requestDto){
        return Response.success(userService.login(requestDto));
    }

    @GetMapping("/userInfo")
    public Response<UserInfoResponseDto> getUserInfo(Authentication authentication){
        return Response.success(userService.getInfo(authentication.getName()));
    }

    @GetMapping("/user")
    public Response<UserInfoResponseDto> getUserInfo(@RequestParam("nickname") String nickname){
        return Response.success(userService.getOtherInfo(nickname));
    }

    @PatchMapping("/modifyPassword")
    public Response<Void> modifyPassword(@RequestBody ModifyPasswordRequestDto passwordDto, Authentication authentication){
        return Response.success(userService.ModifyPassword(authentication.getName(), passwordDto));
    }

    @PatchMapping("/modifyUserInfo")
    public Response<Void> modifyUserInfo(@RequestBody ModifyUserInfoRequestDto requestDto, Authentication authentication){
        return Response.success(userService.modifyUserInfo(authentication.getName(), requestDto));
    }

    @GetMapping("/records")
    public Response<CustomPage<RecordResponseDto>> getUserRecord(@PageableDefault(size = 5, sort = "recordedID", direction = Sort.Direction.ASC) Pageable pageable, @RequestParam String nickname){
        return Response.success(CustomPage.of(recordService.getUserRecord(pageable, nickname)));
    }
}
