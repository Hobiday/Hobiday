package com.example.hobiday_backend.domain.perform.service;

import com.example.hobiday_backend.domain.perform.entity.Perform;
import com.example.hobiday_backend.domain.perform.repository.PerformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PerformService {
    private final PerformRepository performRepository;
    public void save() throws ParserConfigurationException, IOException, SAXException {
        log.info("파싱 작업 시행");
        /*
        <요청 값>
        시작일 11월 12일
        종료일 11월 30일
        rows(요청 데이터) 10개
        페이지 1
        지역코드 11(서울)
        장르 AAAA(연극)
         */
        String baseUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";
        String service = "ecb03304355244159098962ad6c4a1eb";
        String stdate = "20241112"; // 시작 검색기간
        String eddate = "20241130"; // 종료 검색기간
        String rows = "10";         // 공연 개수
        String cpage = "1";
        String signgucode = "11";   // 지역 코드
        String shcate = "AAAA";     // 장르 코드

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("?service="+service);
        urlBuilder.append("&stdate="+stdate);
        urlBuilder.append("&eddate="+eddate);
        urlBuilder.append("&rows="+rows);
        urlBuilder.append("&cpage="+cpage);
        urlBuilder.append("&signgucode="+signgucode);
        urlBuilder.append("&shcate="+shcate);

        Document doc = DocumentBuilderFactory
                .newInstance().newDocumentBuilder().parse(String.valueOf(urlBuilder));

        //root tag
        doc.getDocumentElement().normalize();

        //파싱할 tag
        NodeList nodeList = doc.getElementsByTagName("db"); // xml은 노드로 계층구조
        int n = nodeList.getLength();
//		System.out.println("파싱할 db개수(rows): " + n);
        String prfnm;
        String prfpdfrom;
        String prfpdto;
        String genrenm;
        String fcltynm;
        String area;
        String poster;

        log.info("파싱한 데이터 DB에 시행 시작");
        for (int i = 0; i < n; i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                prfnm = element.getElementsByTagName("prfnm").item(0).getTextContent();
                prfpdfrom = element.getElementsByTagName("prfpdfrom").item(0).getTextContent();
                prfpdto = element.getElementsByTagName("prfpdto").item(0).getTextContent();
                genrenm = element.getElementsByTagName("genrenm").item(0).getTextContent();
                fcltynm = element.getElementsByTagName("fcltynm").item(0).getTextContent();
                area = element.getElementsByTagName("area").item(0).getTextContent();
                poster = element.getElementsByTagName("poster").item(0).getTextContent();
//                확인용
//                System.out.println("=====================DB"+(i+1)+"=====================");
//                System.out.println("공연명: "+element.getElementsByTagName("prfnm").item(0).getTextContent());
//                System.out.println("시작일: "+element.getElementsByTagName("prfpdfrom").item(0).getTextContent());
//                System.out.println("종료일: "+element.getElementsByTagName("prfpdto").item(0).getTextContent());
//                System.out.println("장르명: "+element.getElementsByTagName("genrenm").item(0).getTextContent());
//                System.out.println("공연시설명: "+element.getElementsByTagName("fcltynm").item(0).getTextContent());
//                System.out.println("지역명: "+element.getElementsByTagName("area").item(0).getTextContent());
//                System.out.println("공연포스터 경로: "+element.getElementsByTagName("poster").item(0).getTextContent());
                performRepository.save(Perform.builder()
                        .prfnm(prfnm)
                        .prfpdfrom(prfpdfrom)
                        .prfpdto(prfpdto)
                        .genrenm(genrenm)
                        .fcltynm(fcltynm)
                        .area(area)
                        .poster(poster)
                        .build());
            }
        }
        log.info("파싱한 데이터 DB에 저장 성공");

    }
}