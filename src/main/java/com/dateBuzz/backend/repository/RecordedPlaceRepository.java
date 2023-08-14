package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.RecordEntity;
import com.dateBuzz.backend.model.entity.RecordedPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordedPlaceRepository extends JpaRepository<RecordedPlaceEntity, Long> {

    @Query(value = "select entity from RecordedPlaceEntity entity where entity.record = :record and entity.deletedAt is null order by entity.orders asc ")
    List<RecordedPlaceEntity> findAllByRecord(@Param("record") RecordEntity record);

    @Query(value = "select entity from RecordedPlaceEntity entity where entity.id = :placeId and entity.record = :record and entity.deletedAt is null")
    Optional<RecordedPlaceEntity> findByIdAndRecord(@Param("placeId") Long placeId, @Param("record") RecordEntity record);

    @Modifying
    @Query(value = "update RecordedPlaceEntity place set place.deletedAt = now() where place.id = :placeId")
    void deletePlace(@Param("placeId") Long placeId);
    @Modifying
    @Query(value = "update RecordedPlaceEntity place set place.deletedAt = now() where place.record = :record")
    void deleteAllPlaceByDeletingRecord(@Param("record") RecordEntity record);
}
