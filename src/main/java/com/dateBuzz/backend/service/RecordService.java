package com.dateBuzz.backend.service;

import com.dateBuzz.backend.cachingService.CachingRecordService;
import com.dateBuzz.backend.controller.requestDto.*;
import com.dateBuzz.backend.controller.requestDto.HashtagRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.*;
import com.dateBuzz.backend.controller.responseDto.*;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.ErrorCode;
import com.dateBuzz.backend.model.entity.*;
import com.dateBuzz.backend.repository.*;
import com.dateBuzz.backend.util.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "listResponse")
public class RecordService {

    private final RecordRepository recordRepository;
    private final RecordedPlaceRepository recordedPlaceRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PlaceImgRepository placeImgRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FollowRepository followRepository;
    private final S3Service s3Service;

    private final CachingRecordService cachingRecordService;

    public CustomPage<RecordResponseDto> getList(Pageable pageable, String sort) {
        List<RecordResponseDto> recordedList = cachingRecordService.getRecordList();
        sort(sort, recordedList);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recordedList.size());
        return CustomPage.of(new PageImpl<>(recordedList.subList(start, end), pageable, recordedList.size()));
    }

    // 로그인 한 채로 리스트 받기
    public CustomPage<RecordResponseDto> getListLogin(Pageable pageable, String userName, String sort) {
        List<RecordResponseDto> recordedList = cachingRecordService.getRecordListWithLogin(userName);
        sort(sort, recordedList);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recordedList.size());
        return CustomPage.of(new PageImpl<>(recordedList.subList(start, end), pageable, recordedList.size()));
    }


    public RecordResponseDto getRecordLogin(Long recordId, String userName) {
        UserEntity user = userRepository
                .findByUserName(userName)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", userName)));
        RecordEntity record = recordRepository.findById(recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 에 해당하는 게시물이 존재하지 않습니다.", recordId)));
        List<RecordedPlaceEntity> places = recordedPlaceRepository.findAllByRecord(record);
        List<RecordedPlaceResponseDto> placeResponseDtos = new ArrayList<>();
        for (RecordedPlaceEntity place : places) {
            List<PlaceImgResponseDto> imgResponseDtos = placeImgRepository
                    .findAllByRecordedPlace(place)
                    .stream()
                    .map(PlaceImgResponseDto::fromRecordedPlace)
                    .toList();
            placeResponseDtos.add(RecordedPlaceResponseDto.fromRecordedPlace(place, imgResponseDtos));
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
        int isBookmark = 0;
        Optional<BookmarkEntity> bookmark = bookmarkRepository.findByUserAndRecord(user, record);
        if (bookmark.isPresent() && bookmark.get().getBookmarkStatus() == 1) isBookmark = 1;

        int isFollowing = 0;
        Optional<FollowEntity> follow = followRepository.findByFollowedAndFollower(record.getUser(), user);
        if (follow.isPresent() && follow.get().getFollowStatus() == 1) isFollowing = 1;

        int followerCnt = followRepository.countFollowed(record.getUser().getId());

        int followingCnt = followRepository.countFollowing(record.getUser().getId());

        int recordCnt = recordRepository.recordCnt(record.getUser());

        return RecordResponseDto.fromRecord(record, placeResponseDtos, vibeTags, activityTags, customTags, likeStatus, likeCnt, isBookmark, bookmarkCnt, followingCnt, followerCnt, isFollowing, recordCnt);
    }

    @CacheEvict(allEntries = true)
    public Long deleteArticle(Long recordId, String userName) {
        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", userName)));
        RecordEntity record = recordRepository.findById(recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 에 해당하는 게시물이 존재하지 않습니다.", recordId)));
        if (record.getUser() != user)
            throw new DateBuzzException(ErrorCode.INVALID_USER, String.format("%s는 %d를 삭제할 권한이 없습니다.", userName, recordId));
        recordRepository.deleteRecord(record.getId());
        hashtagRepository.deleteAllByRecord(record);

        List<RecordedPlaceEntity> places = recordedPlaceRepository.findAllByRecord(record);
        recordedPlaceRepository.deleteAllPlaceByDeletingRecord(record);

        for (RecordedPlaceEntity place : places) {
            placeImgRepository.deleteAllByRecordedPlace(place);
        }
        return recordId;
    }

    public Page<RecordResponseDto> getMyRecord(Pageable pageable, String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        List<RecordEntity> records = recordRepository.findAllByUser(user);
        List<RecordResponseDto> recordedList = new ArrayList<>();
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
            if (likeRepository.findByUserAndRecord(user, record).isPresent()) likeStatus++;
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
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recordedList.size());
        return new PageImpl<>(recordedList.subList(start, end), pageable, recordedList.size());
    }

//    @Cacheable(value = "recordResponse", key = "{'one', #recordId}")
    public RecordResponseDto getRecord(Long recordId) {
        RecordEntity record = recordRepository.findById(recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 에 해당하는 게시물이 존재하지 않습니다.", recordId)));
        List<RecordedPlaceEntity> places = recordedPlaceRepository.findAllByRecord(record);
        List<RecordedPlaceResponseDto> placeResponseDtos = new ArrayList<>();
        for (RecordedPlaceEntity place : places) {
            List<PlaceImgResponseDto> imgResponseDtos = placeImgRepository
                    .findAllByRecordedPlace(place)
                    .stream()
                    .map(PlaceImgResponseDto::fromRecordedPlace)
                    .toList();
            placeResponseDtos.add(RecordedPlaceResponseDto.fromRecordedPlace(place, imgResponseDtos));
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

        return RecordResponseDto.fromRecordNotLogin(record, placeResponseDtos, vibeTags, activityTags, customTags, likeCnt, bookmarkCnt, followingCnt, followerCnt, recordCnt);

    }

    @CacheEvict(allEntries = true)
    public void writes(RecordRequestDto requestDto, String userName) {
        UserEntity user = userRepository
                .findByUserName(userName)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", userName)));
        RecordEntity record = RecordEntity.FromRecordRequestDtoAndUserEntity(requestDto, user);
        recordRepository.saveAndFlush(record);
        for (HashtagRequestDto hashtags : requestDto.getHashtags()) {
            HashtagEntity hashtag = HashtagEntity.FromRecordRequestDtoAndRecordEntity(hashtags, record);
            hashtagRepository.save(hashtag);
        }
        for (RecordedPlaceRequestDto recordedPlaces : requestDto.getRecordedPlaces()) {
            RecordedPlaceEntity recordedPlace = RecordedPlaceEntity.fromRecordedRequestDtoAndRecordEntity(recordedPlaces, record);
            if (!(recordedPlaces.getImages() == null)) {
                for (PlaceImageRequestDto imageRequestDto : recordedPlaces.getImages()) {
                    String imageName = imageRequestDto.getFileName();
                    String contentType = imageRequestDto.getContentType();
                    String url = "url"; // test 코드 및 로컬 실험용 url
//                    String url = s3Service.uploadFileByteArray(imageRequestDto.getImgFormData(), imageName, contentType);
                    PlaceImgEntity placeImg = PlaceImgEntity.FromPlaceImgRequestDto(recordedPlace, imageRequestDto, url);
                    placeImgRepository.save(placeImg);
                }
            }
            recordedPlaceRepository.save(recordedPlace);
        }
    }

    public Page<RecordResponseDto> getMyBookmarkRecord(Pageable pageable, String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        List<RecordEntity> records = bookmarkRepository
                .findAllByUser(user)
                .stream()
                .map(BookmarkEntity::getRecord)
                .toList();
        List<RecordResponseDto> recordedList = new ArrayList<>();
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
            if (likeRepository.findByUserAndRecord(user, record).isPresent()) likeStatus++;
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
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recordedList.size());
        return new PageImpl<>(recordedList.subList(start, end), pageable, recordedList.size());
    }


    @CacheEvict(allEntries = true)
    public void modifyRecord(Long recordId, ModifyRecordRequestDto requestDto, String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository.findByUserAndId(user, recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 가 작성한 %d 게시물이 존재하지 않습니다.", name, recordId)));
        record.fixRecord(requestDto);
    }

    @CacheEvict(allEntries = true)
    public Void deletePlace(Long recordedId, String name, DeleteRecordedPlaceRequestDto requestDto) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository.findByUserAndId(user, recordedId).orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND));
        List<RecordedPlaceEntity> places = recordedPlaceRepository.findAllByRecord(record);
        for (PlaceIdRequestDto placeId : requestDto.getDeletePlace()) {
            RecordedPlaceEntity place = recordedPlaceRepository
                    .findByIdAndRecord(placeId.getPlaceId(), record)
                    .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND));
            recordedPlaceRepository.deletePlace(place.getId());
            placeImgRepository.deleteAllByRecordedPlace(place);
            int order = places.indexOf(place);
            for (int i = order + 1; i < places.size(); i++) {
                places.get(i).reduceOrder();
            }
        }
        return null;
    }

    @CacheEvict(allEntries = true)
    public Void addPlace(Long recordId, String name, AddPlacesRequestDto requestDto) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository.findByUserAndId(user, recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 가 작성한 %d 게시물이 존재하지 않습니다.", name, recordId)));

        // delete 된 장소를 제외한 모든 장소 리스트로 만들기
        List<RecordedPlaceEntity> recordedPlaces = recordedPlaceRepository.findAllByRecord(record);

        // 장소 5개 제한
        if (recordedPlaces.size() > 5) throw new DateBuzzException(ErrorCode.MAX_PLACES);

        // 새로운 장소를 돌면서 order 늘리면서 저장하기
        for (RecordedPlaceRequestDto place : requestDto.getNewPlaces()) {
            int orders = place.getOrders();
            for (int i = orders - 1; i < recordedPlaces.size(); i++) {
                recordedPlaces.get(i).increaseOrder();
            }
            RecordedPlaceEntity newPlace = RecordedPlaceEntity.fromRecordedRequestDtoAndRecordEntity(place, record);
            recordedPlaceRepository.saveAndFlush(newPlace);
            recordedPlaces.add(newPlace);
            recordedPlaces.sort(Comparator.comparing(RecordedPlaceEntity::getOrders));
        }
        return null;
    }

    @CacheEvict(allEntries = true)
    public Void modifyPlace(Long recordId, String name, ModifyRecordedPlaceRequestDto requestDto) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository.findByUserAndId(user, recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 가 작성한 %d 게시물이 존재하지 않습니다.", name, recordId)));
        for (ModifyRecordedPlaceInfoRequestDto info : requestDto.getModifyInfo()) {
            RecordedPlaceEntity changingPlace = recordedPlaceRepository.findByIdAndRecord(info.getPlaceId(), record).orElseThrow(() -> new DateBuzzException(ErrorCode.INVALID_USER, "해당 기록에는 삭제하려는 장소가 존재하지 않습니다."));
            changingPlace.modifyPlaceInfo(info);
        }
        return null;
    }

    @CacheEvict(allEntries = true)
    public Void changeOrder(Long recordId, String name, ModifyRecordedPlaceOrderRequestDto requestDto) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        RecordEntity record = recordRepository.findByUserAndId(user, recordId)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND, String.format("%s 가 작성한 %d 게시물이 존재하지 않습니다.", name, recordId)));

        // requestDto 돌면서 순서를 변경
        for (RecordedOrder order : requestDto.getChangingOrders()) {
            RecordedPlaceEntity place = recordedPlaceRepository.findByIdAndRecord(order.getPlaceId(), record).orElseThrow(() -> new DateBuzzException(ErrorCode.DATE_NOT_FOUND));
            place.changeOrder(order.getNewOrders());
        }
        return null;
    }

    public List<FollowListResponseDto> getMyFollower(String name) {
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", name)));
        List<FollowEntity> followList = followRepository.findAllByFollower(user.getId());
        List<FollowListResponseDto> followInfoList = new ArrayList<>();
        for (FollowEntity follow : followList) {
            int followCnt = followRepository.countFollowed(follow.getFollower().getId());
            int followingCnt = followRepository.countFollowing(follow.getFollowed().getId());
            followInfoList.add(FollowListResponseDto.fromFollower(follow, followCnt, followingCnt));
        }
        return followInfoList;
    }

    public Page<RecordResponseDto> getUserRecord(Pageable pageable, String nickname) {
        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new DateBuzzException(ErrorCode.USER_NOT_FOUND, String.format("%s 는 없는 유저입니다.", nickname)));
        List<RecordEntity> records = recordRepository
                .findAllByUser(user);
        List<RecordResponseDto> recordedList = new ArrayList<>();
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
            if (likeRepository.findByUserAndRecord(user, record).isPresent()) likeStatus++;
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
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recordedList.size());
        return new PageImpl<>(recordedList.subList(start, end), pageable, recordedList.size());
    }

    public void sort(String sort, List<RecordResponseDto> recordedList) {
        if (sort.equals("latest")) recordedList.sort(Comparator.comparing(RecordResponseDto::getCreatedAt).reversed());
        if (sort.equals("popular")) recordedList.sort(Comparator.comparing(RecordResponseDto::getLikeCnt).reversed());
    }
}
