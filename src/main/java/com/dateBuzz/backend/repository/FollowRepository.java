package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.FollowEntity;
import com.dateBuzz.backend.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFollowedAndFollower(UserEntity followed, UserEntity follower);

    @Query(value = "select count(*) from follow where follower_id = ?1 and follow_status = 1", nativeQuery = true)
    Integer countFollowed(Long userId);

    @Query(value = "select count(*) from follow where followed_id = ?1 and follow_status = 1", nativeQuery = true)
    Integer countFollowing(Long userId);

    @Query(value = "select * from follow where follower_id = ?1 and follow_status = 1", nativeQuery = true)
    List<FollowEntity> findAllByFollower(Long userId);
}
