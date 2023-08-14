package com.dateBuzz.backend.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Table(name = "\"bookmark\"")
@Entity
@Getter
@NoArgsConstructor
public class BookmarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private RecordEntity record;

    private int bookmarkStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt(){
        this.createdAt = Timestamp.from(Instant.now());
    }
    @PreUpdate
    void updatedAt(){
        this.updatedAt = Timestamp.from(Instant.now());
    }

    @Builder
    public BookmarkEntity(UserEntity user, RecordEntity record) {
        this.user = user;
        this.record = record;
        this.bookmarkStatus = 1;
    }

    public static BookmarkEntity bookmarkRecord(UserEntity user, RecordEntity record){
        return new BookmarkEntity(user, record);
    }

    public static void updateBookmarkStatus(BookmarkEntity bookmark){
        if(bookmark.bookmarkStatus == 1){
            bookmark.bookmarkStatus = 0;
            return;
        }
        if(bookmark.bookmarkStatus == 0) bookmark.bookmarkStatus = 1;
    }
}
