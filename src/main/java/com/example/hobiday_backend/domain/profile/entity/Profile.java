package com.example.hobiday_backend.domain.profile.entity;

import com.example.hobiday_backend.domain.follow.entity.Follow;
import com.example.hobiday_backend.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;


@Table(name = "profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    private Long userId; // 방법1
    @OneToOne // 방법2
    @JoinColumn(name="user_id")
    private User user;

    @Column(length=20)
    private String profileNickname;
    private String profileEmail;

    @Column(length=20)
    private String profileGenre;

    @Column(length=500)
    private String profileIntroduction;

    @Column(columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private Boolean profileActiveFlag; // 프로필 등록 여부

    @Column(nullable = true)
    private String profileImageUrl;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();

    public void updateProfileActiveFlag(){
        this.profileActiveFlag = true;
    }

    @Builder
    public Profile(//Long userId, //방법1
                   User user, // 방법2
                   String profileNickname, String profileGenre, String profileEmail,
                   String profileIntroduction, String profileImageUrl) {
//        this.userId = userId; //방법1
        this.user = user;
        this.profileNickname = profileNickname;
        this.profileEmail = profileEmail;
        this.profileGenre = profileGenre;
        this.profileIntroduction = profileIntroduction;
        this.profileImageUrl = profileImageUrl;
    }
}