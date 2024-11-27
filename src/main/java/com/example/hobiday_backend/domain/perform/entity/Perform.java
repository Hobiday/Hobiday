package com.example.hobiday_backend.domain.perform.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "Performs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Perform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ========================= 공연 기본정보 =========================*/
    private String mt20id;          // 공연상세ID
    private String prfnm;           // 공연명
    private String prfpdfrom;       // 시작일 (2016.05.12)
    private String prfpdto;         // 종료일
    private String genrenm;         // 장르명
    private String prfstate;        // 공연상태 (예정, 중, 완료)
    private String fcltynm;         // 공연시설명 (피가로아트홀)
    private Boolean openrun;        // 오픈런 (Y/N)
    private String area;            // 지역명 (서울)
    private String poster;          // 공연포스터 경로 (url)

    @Builder
    public Perform(String prfnm, String prfpdfrom, String prfpdto, String genrenm, String fcltynm, String area, String poster) {
        this.prfnm = prfnm;
        this.prfpdfrom = prfpdfrom;
        this.prfpdto = prfpdto;
        this.genrenm = genrenm;
        this.fcltynm = fcltynm;
        this.area = area;
        this.poster = poster;
    }
}

