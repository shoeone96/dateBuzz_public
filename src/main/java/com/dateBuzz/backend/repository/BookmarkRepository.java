package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.BookmarkEntity;
import com.dateBuzz.backend.model.entity.RecordEntity;
import com.dateBuzz.backend.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {
    Optional<BookmarkEntity> findByUserAndRecord(UserEntity user, RecordEntity record);
    @Query(value = "select count(*) from BookmarkEntity entity where entity.record = :record and entity.bookmarkStatus = 1")
    Integer countByRecord(@Param("record") RecordEntity record);

    @Query(value = "select entity from BookmarkEntity entity where entity.user = :user and entity.bookmarkStatus = 1 and entity.record.deletedAt is null")
    List<BookmarkEntity> findAllByUser(UserEntity user);
}
