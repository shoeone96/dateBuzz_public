package com.dateBuzz.backend.repository;

import com.dateBuzz.backend.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = "select user from UserEntity user where user.userName = :userName and user.deletedAt is null")
    Optional<UserEntity> findByUserName(@Param("userName") String userName);
    @Query(value = "select user from UserEntity user where user.nickname = :nickname and user.deletedAt is null")
    Optional<UserEntity> findByNickname(@Param("nickname") String nickname);
}
