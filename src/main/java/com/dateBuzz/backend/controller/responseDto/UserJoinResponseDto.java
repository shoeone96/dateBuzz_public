package com.dateBuzz.backend.controller.responseDto;

import com.dateBuzz.backend.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponseDto {

    private String userName;
    private String nickName;

    public static UserJoinResponseDto fromUser(UserEntity user){
        return new UserJoinResponseDto(
                user.getUserName(),
                user.getNickname()
        );
    }
}
