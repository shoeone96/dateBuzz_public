package com.dateBuzz.backend.model.entity;

import com.dateBuzz.backend.controller.requestDto.HashtagRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"Hashtag\"")
@Getter
public class HashtagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private RecordEntity record;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "hashtag_type")
    @Enumerated(EnumType.STRING)
    private HashtagType hashtagType;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt(){
        this.createdAt = Timestamp.from(Instant.now());
    }
    @PreUpdate
    void updatedAt(){
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static HashtagEntity FromRecordRequestDtoAndRecordEntity(HashtagRequestDto requestDto, RecordEntity record){
        HashtagEntity hashtag = new HashtagEntity();
        hashtag.record = record;
        hashtag.hashtagType = HashtagType.returnHashtag(requestDto.getType());
        hashtag.tagName = requestDto.getTagName();
        return hashtag;
    }
}
