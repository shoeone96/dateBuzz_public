package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.HashtagEntity;
import com.dateBuzz.backend.model.entity.HashtagType;
import com.dateBuzz.backend.model.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<HashtagEntity, Long> {
    List<HashtagEntity> findAllByRecordAndHashtagType(RecordEntity record, HashtagType hashtagType);

    List<HashtagEntity> findAllByTagName(String hashtagName);

    void deleteAllByRecord(RecordEntity record);
}
