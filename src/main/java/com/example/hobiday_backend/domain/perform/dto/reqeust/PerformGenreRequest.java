package com.example.hobiday_backend.domain.perform.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PerformGenreRequest {
    public String genre;
    public String rowStart;
    public String rowEnd;
}
