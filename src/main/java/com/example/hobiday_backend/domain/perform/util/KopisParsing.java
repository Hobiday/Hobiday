package com.example.hobiday_backend.domain.perform.util;

import java.util.HashMap;

public class KopisParsing {
    public final static HashMap<String, String> GENRE_CODES_REQUEST = new HashMap<>(){{
        put("연극", "AAAA");
        put("무용", "BBBC");
        put("대중무용", "BBBE");
        put("서양음악", "CCCA");
        put("한국음악", "CCCC");
        put("대중음악", "CCCD");
        put("복합", "EEEA");
        put("서커스", "EEEB");
        put("뮤지컬", "GGGA");
    }};

    public final static HashMap<String, String> PERFORM_DETAILS_REQUEST = new HashMap<>(){{
        put("공연상세", "mt20id");
        put("시설상세", "mt10id");
    }};


}
