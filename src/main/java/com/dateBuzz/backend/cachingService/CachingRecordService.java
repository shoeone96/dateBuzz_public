package com.dateBuzz.backend.cachingService;

import com.dateBuzz.backend.controller.responseDto.HashtagResponseDto;
import com.dateBuzz.backend.controller.responseDto.PlaceImgResponseDto;
import com.dateBuzz.backend.controller.responseDto.RecordResponseDto;
import com.dateBuzz.backend.controller.responseDto.RecordedPlaceResponseDto;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.ErrorCode;
import com.dateBuzz.backend.model.entity.*;
import com.dateBuzz.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = "listResponse")
public class CachingRecordService {

    private final RecordRepository recordRepository;
    private final RecordedPlaceRepository recordedPlaceRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PlaceImgRepository placeImgRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FollowRepository followRepository;

    @Cacheable(key = "{'all'}")
    public List<RecordResponseDto> getRecordList() {
        List<RecordResponseDto> recordedList = new ArrayList<>();
        List<RecordEntity> records = recordRepository.findAll();
        for (RecordEntity record : records) {
            List<RecordedPlaceResponseDto> places = new ArrayList<>();
            List<RecordedPlaceEntity> recordedPlaces = recordedPlaceRepository.findAllByRecord(record);
            for (RecordedPlaceEntity place : recordedPlaces) {
                List<PlaceImgEntity> imgEntities = placeImgRepository.findAllByRecordedPlace(place);
                List<PlaceImgResponseDto> imgResponseDtos = imgEntities
                        .stream()
                        .map(PlaceImgResponseDto::fromRecordedPlace)
                        .toList();
                RecordedPlaceResponseDto placeResponseDto = RecordedPlaceResponseDto.fromRecordedPlace(place, imgResponseDtos);
                places.add(placeResponseDto);
            }
            List<HashtagResponseDto> vibeTags = hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.VIBE)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();

            List<HashtagResponseDto> activityTags = hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.ACTIVITY)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();
            List<HashtagResponseDto> customTags = hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.CUSTOM)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();
            // like Cnt
            int likeCnt = likeRepository.countByRecord(record);

            // bookmark Cnt
            int bookmarkCnt = bookmarkRepository.countByRecord(record);

            int followerCnt = followRepository.countFollowed(record.getUser().getId());

            int followingCnt = followRepository.countFollowing(record.getUser().getId());

            int recordCnt = recordRepository.recordCnt(record.getUser());

            RecordResponseDto recordResponseDto = RecordResponseDto.fromRecordNotLogin(record, places, vibeTags, activityTags, customTags, likeCnt, bookmarkCnt, followingCnt, followerCnt, recordCnt);
            recordedList.add(recordResponseDto);

        }
        return recordedList;
    }

    @Cacheable(key = "{'all', #userName}")
    public List<RecordResponseDto> getRecordListWithLogin(String userName) {
        List<RecordResponseDto> recordedList = new ArrayList<>();
        UserEntity user = userRepository
                .findByUserName(userName)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", userName)));
        // TODO: 한번에 조인해서 가져오는 방식 도입
        List<RecordEntity> records = recordRepository.findAll();
        for (RecordEntity record : records) {
            List<RecordedPlaceResponseDto> places = new ArrayList<>();
            List<RecordedPlaceEntity> recordedPlaces = recordedPlaceRepository.findAllByRecord(record);
            for (RecordedPlaceEntity place : recordedPlaces) {
                List<PlaceImgEntity> imgEntities = placeImgRepository.findAllByRecordedPlace(place);
                List<PlaceImgResponseDto> imgResponseDtos = imgEntities
                        .stream()
                        .map(PlaceImgResponseDto::fromRecordedPlace)
                        .toList();
                RecordedPlaceResponseDto placeResponseDto = RecordedPlaceResponseDto.fromRecordedPlace(place, imgResponseDtos);
                places.add(placeResponseDto);
            }
            List<HashtagResponseDto> vibeTags = hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.VIBE)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();

            List<HashtagResponseDto> activityTags = hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.ACTIVITY)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();
            List<HashtagResponseDto> customTags = hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.CUSTOM)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();
            // like Cnt
            int likeCnt = likeRepository.countByRecord(record);

            // like Status
            int likeStatus = 0;
            Optional<LikeEntity> like = likeRepository.findByUserAndRecord(user, record);
            if (like.isPresent() && like.get().getLikeStatus() == 1) likeStatus++;

            // bookmark Cnt
            int bookmarkCnt = bookmarkRepository.countByRecord(record);

            // bookmark Status
            int bookmarkStatus = 0;
            Optional<BookmarkEntity> bookmark = bookmarkRepository.findByUserAndRecord(user, record);
            if (bookmark.isPresent() && bookmark.get().getBookmarkStatus() == 1) bookmarkStatus++;

            int followerCnt = followRepository.countFollowed(record.getUser().getId());

            int followingCnt = followRepository.countFollowing(record.getUser().getId());

            int recordCnt = recordRepository.recordCnt(record.getUser());

            RecordResponseDto recordResponseDto = RecordResponseDto.fromRecord(record, places, vibeTags, activityTags, customTags, likeStatus, likeCnt, bookmarkStatus, bookmarkCnt, followingCnt, followerCnt, recordCnt, recordCnt);
            recordedList.add(recordResponseDto);
        }
        return recordedList;
    }
}
