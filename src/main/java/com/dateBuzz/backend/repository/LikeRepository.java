package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.LikeEntity;
import com.dateBuzz.backend.model.entity.RecordEntity;
import com.dateBuzz.backend.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByUserAndRecord(UserEntity user, RecordEntity record);
    @Query(value = "select count(*) from LikeEntity entity where entity.record = :record and entity.likeStatus = 1")
    Integer countByRecord(@Param("record") RecordEntity record);
}
