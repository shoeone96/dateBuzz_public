package com.dateBuzz.backend.service;

import com.dateBuzz.backend.controller.responseDto.BookmarkResponseDto;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.ErrorCode;
import com.dateBuzz.backend.model.entity.BookmarkEntity;
import com.dateBuzz.backend.model.entity.RecordEntity;
import com.dateBuzz.backend.model.entity.UserEntity;
import com.dateBuzz.backend.repository.BookmarkRepository;
import com.dateBuzz.backend.repository.RecordRepository;
import com.dateBuzz.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;

    private final RecordRepository recordRepository;

    private final BookmarkRepository bookmarkRepository;


    public BookmarkResponseDto bookmark(Long recordId, String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository
                .findById(recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%d 번호를 가진 기록을 찾을 수 없습니다.", recordId)));
        Optional<BookmarkEntity> bookmark = bookmarkRepository.findByUserAndRecord(user, record);
        bookmark.ifPresent(BookmarkEntity::updateBookmarkStatus);
        if (bookmark.isEmpty())bookmarkRepository.save(BookmarkEntity.bookmarkRecord(user, record));

        int bookmarkCnt = bookmarkRepository.countByRecord(record);

        int bookmarkStatus = bookmarkRepository.findByUserAndRecord(user, record).get().getBookmarkStatus();
        bookmarkRepository.flush();

        return BookmarkResponseDto.builder()
                .bookmarkCnt(bookmarkCnt)
                .bookmarkStatus(bookmarkStatus)
                .build();
    }
}
