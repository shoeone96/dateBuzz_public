package com.dateBuzz.backend.service;

import com.dateBuzz.backend.controller.responseDto.LikeResponseDto;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.ErrorCode;
import com.dateBuzz.backend.model.entity.LikeEntity;
import com.dateBuzz.backend.model.entity.RecordEntity;
import com.dateBuzz.backend.model.entity.UserEntity;
import com.dateBuzz.backend.repository.LikeRepository;
import com.dateBuzz.backend.repository.RecordRepository;
import com.dateBuzz.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;

    private final RecordRepository recordRepository;

    private final LikeRepository likeRepository;


    public LikeResponseDto like(Long recordId, String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository
                .findById(recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%d 번호를 가진 기록을 찾을 수 없습니다.", recordId)));
        Optional<LikeEntity> like = likeRepository.findByUserAndRecord(user, record);
        like.ifPresent(LikeEntity::updateLikeStatus);
        if (like.isEmpty())likeRepository.save(LikeEntity.likeRecord(user, record));

        // like Cnt
        int likeCnt = likeRepository.countByRecord(record);

        // like Status
        int likeStatus = likeRepository.findByUserAndRecord(user, record).get().getLikeStatus();
        likeRepository.flush();

        return LikeResponseDto.builder()
                .likeCnt(likeCnt)
                .likeStatus(likeStatus).build();
    }
}
