package com.dateBuzz.backend.controller.responseDto;

import com.dateBuzz.backend.model.entity.RecordEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecordResponseDto {
    private static final String datePattern = "yyyy-MM-dd";
    private Long recordedId;
    private String nickname;
    private int followerCnt;
    private int followingCnt;
    private String profileImg;
    private String title;
    private String recordContent;
    @JsonFormat(pattern = datePattern)
    private Timestamp createdAt;
    private String dateDate;
    private int isBookmark;
    private int likeStatus;
    private int likeCnt;
    private int bookMarkCnt;
    private int recordCnt;
    private int isFollowing;

    private List<RecordedPlaceResponseDto> recordedPlaces;
    private List<HashtagResponseDto> VibeTags;
    private List<HashtagResponseDto> activityTags;
    private List<HashtagResponseDto> customTags;

    @Builder
    public static RecordResponseDto fromRecord(RecordEntity record, List<RecordedPlaceResponseDto> recordedPlaces, List<HashtagResponseDto> vibeTags, List<HashtagResponseDto> activityTags, List<HashtagResponseDto> customTags, int likeStatus, int likeCnt, int bookMarkStatus, int bookMarkCnt, int followingCnt, int followerCnt, int isFollowing, int recordCnt){
        return new RecordResponseDto(
                record.getId(),
                record.getUser().getNickname(),
                followerCnt,
                followingCnt,
                record.getUser().getProfileImg(),
                record.getTitle(),
                record.getRecordedContent(),
                record.getCreatedAt(),
                record.getDateDate(),
                bookMarkStatus,
                likeStatus,
                likeCnt,
                bookMarkCnt,
                recordCnt,
                isFollowing,
                recordedPlaces,
                vibeTags,
                activityTags,
                customTags
        );
    }

    @Builder
    public static RecordResponseDto fromRecordNotLogin(RecordEntity record, List<RecordedPlaceResponseDto> recordedPlaces, List<HashtagResponseDto> vibeTags, List<HashtagResponseDto> activityTags, List<HashtagResponseDto> customTags, int likeCnt, int bookMarkCnt, int followingCnt, int followerCnt, int recordCnt){
        return new RecordResponseDto(
                record.getId(),
                record.getUser().getNickname(),
                followerCnt,
                followingCnt,
                record.getUser().getProfileImg(),
                record.getTitle(),
                record.getRecordedContent(),
                record.getCreatedAt(),
                record.getDateDate(),
                0,
                0,
                likeCnt,
                bookMarkCnt,
                recordCnt,
                0,
                recordedPlaces,
                vibeTags,
                activityTags,
                customTags
        );
    }

}
