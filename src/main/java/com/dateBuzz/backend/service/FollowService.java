package com.dateBuzz.backend.service;

import com.dateBuzz.backend.controller.requestDto.FollowRequestDto;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.ErrorCode;
import com.dateBuzz.backend.model.entity.FollowEntity;
import com.dateBuzz.backend.model.entity.UserEntity;
import com.dateBuzz.backend.repository.FollowRepository;
import com.dateBuzz.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;

    private final FollowRepository followRepository;
    public Void follow(FollowRequestDto followName, String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        UserEntity followingUser = userRepository.findByNickname(followName.getNickname())
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        Optional<FollowEntity> follow = followRepository.findByFollowedAndFollower(followingUser, user);
        follow.ifPresent(FollowEntity::updateFollow);
        if (follow.isEmpty()) followRepository.save(FollowEntity.following(followingUser, user));
        return null;
    }
}
