package com.example.hobiday_backend.domain.perform.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenreCasting {
    //    private final static String[] GENRES = {"연극", "무용", "대중무용", "서양음악", "한국음악", "대중음악", "복합", "서커스", "뮤지컬"};
    public final static HashMap<String, Integer> GENRES = new HashMap<>(){{
        put("연극", 0);
        put("무용", 1);
        put("대중무용", 2);
        put("서양음악", 3);
        put("한국음악", 4);
        put("대중음악", 5);
        put("복합", 6);
        put("서커스", 7);
        put("뮤지컬", 8);
    }};

    // 장르 타입 변환: 리스트 -> 문자열
    // 장르순서: {"연극", "무용", "대중무용", "서양음악", "한국음악", "대중음악", "복합", "서커스", "뮤지컬"}
    public static String getGenreToString(List<String> genreList){
        StringBuilder genreStrBuilder = new StringBuilder("000000000");
        for (String genre:genreList){
            genreStrBuilder.setCharAt(GENRES.get(genre), '1');
        }
        return genreStrBuilder.toString();
    }

    // 장르 타입 변환: 문자열 -> 리스트
    public static List<String> getGenreToList(String genreStr){
        List<String> genreList = new ArrayList<>();
        for (String genre:GENRES.keySet()){
            if(genreStr.charAt(GENRES.get(genre)) == '1'){
                genreList.add(genre);
            }
        }
        return genreList;
    }
}
