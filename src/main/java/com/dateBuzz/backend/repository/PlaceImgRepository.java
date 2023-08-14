package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.PlaceImgEntity;
import com.dateBuzz.backend.model.entity.RecordedPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceImgRepository extends JpaRepository<PlaceImgEntity, Long> {
    @Query(value = "select entity from PlaceImgEntity entity where entity.recordedPlace = :place order by entity.orders asc ")
    List<PlaceImgEntity> findAllByRecordedPlace(RecordedPlaceEntity place);
    void deleteAllByRecordedPlace(RecordedPlaceEntity recordedPlace);
}
