package com.dateBuzz.backend.model.entity;

import com.dateBuzz.backend.controller.requestDto.RecordRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.ModifyRecordRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"record\"")
@SQLDelete(sql = "update record set deleted_at = now() where id = ?")
@Where(clause = "deleted_at is null")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private UserEntity user;
    private String title;
    private String recordedContent;
    private String dateDate;
    @Enumerated(EnumType.STRING)
    private Exposure exposure;

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


    public static RecordEntity FromRecordRequestDtoAndUserEntity(RecordRequestDto requestDto, UserEntity user){
        RecordEntity record = new RecordEntity();
        record.user = user;
        record.title = requestDto.getTitle();
        record.recordedContent = requestDto.getRecordedContent();
        record.dateDate = requestDto.getDateDate();
        record.exposure = Exposure.returnExposure(requestDto.getExposure());
        return record;
    }

    public void fixRecord(ModifyRecordRequestDto requestDto) {
        if(!(requestDto.getTitle() == null)) this.title = requestDto.getTitle();
        if(!(requestDto.getRecordedContent() == null)) this.recordedContent = requestDto.getRecordedContent();
        if(!(requestDto.getDateDate() == null)) this.dateDate = requestDto.getDateDate();
    }
}
