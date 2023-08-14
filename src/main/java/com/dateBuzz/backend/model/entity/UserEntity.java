package com.dateBuzz.backend.model.entity;


import com.dateBuzz.backend.controller.requestDto.modify.ModifyUserInfoRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"users\"")
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;
    private String profileImg;
    private String nickname;
    private String email;
    @OneToMany(mappedBy = "followed")
    private List<FollowEntity> followedList = new ArrayList<>();
    @OneToMany(mappedBy = "follower")
    private List<FollowEntity> follower = new ArrayList<>();

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
    public UserEntity(String userName, String password, String nickname, String email) {
        this.userName = userName;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
    public static UserEntity fromJoinRequestDto(String userName, String password, String nickname, String email){
        return new UserEntity(
                userName,
                password,
                nickname,
                email
        );
    }

    public void modifyPassword(String newPassword) {
        this.password = newPassword;
    }

    public void modifyProfile(ModifyUserInfoRequestDto requestDto, String imgUrl) {
        this.nickname = requestDto.getNickname();
        this.profileImg = imgUrl;
    }
}
