package com.dateBuzz.backend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "\"follow\"")
@Entity
@Getter
@NoArgsConstructor
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private UserEntity followed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private UserEntity follower;

    private int followStatus;

    private FollowEntity(UserEntity followed, UserEntity follower) {
        this.followed = followed;
        this.follower = follower;
        this.followStatus = 1;
    }

    public static FollowEntity following(UserEntity following, UserEntity follower){
        return new FollowEntity(following, follower);
    }

    public static void updateFollow(FollowEntity follow){
        if(follow.getFollowStatus() == 0) {
            follow.followStatus = 1;
            return;
        }
        if(follow.getFollowStatus() == 1){
            follow.followStatus = 0;
        }
    }
}
