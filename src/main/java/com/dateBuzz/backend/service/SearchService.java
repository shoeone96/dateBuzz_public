package com.dateBuzz.backend.service;

import com.dateBuzz.backend.controller.responseDto.*;
import com.dateBuzz.backend.model.entity.*;
import com.dateBuzz.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final RecordedPlaceRepository recordedPlaceRepository;
    private final HashtagRepository hashtagRepository;
    private final PlaceImgRepository placeImgRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FollowRepository followRepository;
    private final RecordRepository recordRepository;

    public Page<RecordResponseDto> findByTag(String hashtagName, Pageable pageable) {
        List<HashtagEntity> hashtagRecords = hashtagRepository.findAllByTagName(hashtagName);
        List<RecordEntity> records = hashtagRecords.stream().map(HashtagEntity::getRecord).toList();
        List<RecordResponseDto> recordedList = new ArrayList<>();
        for (RecordEntity record : records) {
            List<RecordedPlaceResponseDto> places = new ArrayList<>();
            List<RecordedPlaceEntity> recordedPlaces = recordedPlaceRepository.findAllByRecord(record);
            for(RecordedPlaceEntity place: recordedPlaces){
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

            List<HashtagResponseDto> activityTags= hashtagRepository
                    .findAllByRecordAndHashtagType(record, HashtagType.ACTIVITY)
                    .stream()
                    .map(HashtagResponseDto::fromHashtag)
                    .toList();
            List<HashtagResponseDto> customTags= hashtagRepository
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
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recordedList.size());
        return new PageImpl<>(recordedList.subList(start, end), pageable, recordedList.size());
    }
}
