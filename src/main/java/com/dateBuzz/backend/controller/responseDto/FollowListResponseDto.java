package com.dateBuzz.backend.controller.responseDto;

import com.dateBuzz.backend.model.entity.FollowEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FollowListResponseDto {
    private String nickname;
    private int follower;
    private int following;
    private String imgUrl;

    public static FollowListResponseDto fromFollower(FollowEntity entity, int followerCnt, int followingCnt) {
        return new FollowListResponseDto(
                entity.getFollowed().getNickname(),
                followerCnt,
                followingCnt,
                entity.getFollowed().getProfileImg()
        );
    }
}
