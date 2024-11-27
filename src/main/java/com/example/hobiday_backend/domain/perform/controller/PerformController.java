package com.example.hobiday_backend.domain.perform.controller;

import com.example.hobiday_backend.domain.perform.service.PerformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@Tag(name="공연 정보")
public class PerformController {
    private final PerformService performService;

//    @Operation(summary="프론트에서 사용X")
//    @GetMapping("/parsing")
//    public void runTesT() throws ParserConfigurationException, IOException, SAXException {
//        performService.save();
//    }
}

