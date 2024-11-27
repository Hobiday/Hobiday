package com.example.hobiday_backend.domain.perform.entity;

import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "facility_details")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//@Entity
public class PerformFacilityDetails {
    /* ========================= 공연상세 =========================*/
    private String mt10id;          // 시설상세ID
    private String prfcast;         // 공연 출연진
    private String prfruntime;      // 공연 런타임 (1시간 30분)
    private String prfage;          // 공연 관람 연령 (만 12세 이상)
    private String pcseguidance;    // 티켓가격 (전석 30,000원)
    private String sty;             // 줄거리
    private String styurl;          // [상위 여러개에서 1개]소개 이미지 (url)
    private String dtguidance;      // 공연시간
    private String relatenm;        // [상위 여러개에서 1개]예약 방법
    private String relateurl;       // [상위 여러개에서 1개]예약 사이트
}
