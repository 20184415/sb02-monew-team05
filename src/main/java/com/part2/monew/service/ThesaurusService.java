package com.part2.monew.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ThesaurusService {

    private final WebClient webClient;
    
    @Value("${korean.dict.api.key:dummy_key}")
    private String apiKey;

    // 뉴스 분야별 키워드 패턴
    private final Map<String, Pattern> newsFieldPatterns = new HashMap<>();
    
    // 백업용 하드코딩 사전 (API 실패시 사용)
    private final Map<String, List<String>> fallbackExpansionMap = new HashMap<>();

    public ThesaurusService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://stdict.korean.go.kr")
                .build();
        initializeFieldPatterns();
        initializeFallbackMap();
    }

    /**
     * 뉴스 분야별 definition 패턴 초기화
     */
    private void initializeFieldPatterns() {
        // 경제 관련 키워드 패턴
        newsFieldPatterns.put("경제", Pattern.compile(".*(재화|생산|분배|소비|금융|산업|투자|증권|주식|은행|자본|시장|경영|매출|수익|비용|가격|물가|인플레이션|GDP|경제성장|무역|수출|수입|기업|회사|상업|판매|구매).*"));
        
        // IT/기술 관련 키워드 패턴  
        newsFieldPatterns.put("IT", Pattern.compile(".*(정보기술|컴퓨터|소프트웨어|하드웨어|프로그램|시스템|네트워크|인터넷|웹|데이터|디지털|전자|통신|기술|과학|연구|개발|혁신).*"));
        
        // 정치 관련 키워드 패턴
        newsFieldPatterns.put("정치", Pattern.compile(".*(정치|정부|국회|대통령|장관|의원|선거|정당|여당|야당|법안|정책|행정|입법|사법|국정|외교|안보|국방).*"));
        
        // 사회 관련 키워드 패턴
        newsFieldPatterns.put("사회", Pattern.compile(".*(사회|시민|국민|복지|교육|문화|환경|보건|의료|안전|범죄|법률|인권|여성|청년|노인|아동|지역|도시|농촌).*"));
        
        // 스포츠 관련 키워드 패턴
        newsFieldPatterns.put("스포츠", Pattern.compile(".*(스포츠|운동|경기|선수|팀|리그|올림픽|월드컵|축구|야구|농구|배구|테니스|골프|수영|육상|체육).*"));
        
        log.info("뉴스 분야별 definition 패턴 초기화 완료: {}개 분야", newsFieldPatterns.size());
    }

    /**
     * 백업용 키워드 확장 사전 초기화
     */
    private void initializeFallbackMap() {
        fallbackExpansionMap.put("경제", Arrays.asList("경제", "금융", "투자", "기업", "산업", "무역"));
        fallbackExpansionMap.put("IT", Arrays.asList("IT", "기술", "컴퓨터", "소프트웨어", "디지털"));
        fallbackExpansionMap.put("정치", Arrays.asList("정치", "정부", "국회", "대통령", "정책"));
        fallbackExpansionMap.put("사회", Arrays.asList("사회", "교육", "문화", "환경", "의료"));
        fallbackExpansionMap.put("스포츠", Arrays.asList("스포츠", "축구", "야구", "올림픽", "경기"));
        
        log.info("백업용 키워드 확장 사전 초기화 완료: {}개 기본 키워드", fallbackExpansionMap.size());
    }

    /**
     * 표준국어대사전 API를 사용해서 뉴스 분야에 맞는 키워드만 확장합니다.
     * @param keyword 원본 키워드
     * @return 확장된 키워드 리스트 (원본 포함)
     */
    public List<String> expandKeyword(String keyword) {
        Set<String> expandedKeywords = new HashSet<>();
        expandedKeywords.add(keyword); // 원본 키워드 포함
        
        log.info("키워드 확장 시작: {}", keyword);
        
        try {
            if (!"dummy_key".equals(apiKey)) {
                // 표준국어대사전 API 호출
                String xmlResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/search.do")
                                .queryParam("key", apiKey)
                                .queryParam("q", keyword)
                                .queryParam("req_type", "xml")
                                .queryParam("start", 1)
                                .queryParam("num", 10)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                if (xmlResponse != null && !xmlResponse.trim().isEmpty()) {
                    parseXmlAndExtractNewsKeywords(xmlResponse, expandedKeywords, keyword);
                }
            } else {
                log.warn("표준국어대사전 API 키가 설정되지 않음. 백업 사전 사용");
                expandWithFallback(keyword, expandedKeywords);
            }
            
        } catch (Exception e) {
            log.warn("표준국어대사전 API 호출 실패: {} - 백업 사전 사용", e.getMessage());
            expandWithFallback(keyword, expandedKeywords);
        }
        
        List<String> result = new ArrayList<>(expandedKeywords);
        log.info("키워드 '{}' 확장 결과: {}개 키워드 - {}", keyword, result.size(), result);
        return result;
    }

    /**
     * 백업 사전을 사용한 키워드 확장
     */
    private void expandWithFallback(String keyword, Set<String> expandedKeywords) {
        // 정확히 일치하는 키워드 찾기
        if (fallbackExpansionMap.containsKey(keyword)) {
            expandedKeywords.addAll(fallbackExpansionMap.get(keyword));
        }
        
        // 부분 매칭으로 추가 확장
        for (Map.Entry<String, List<String>> entry : fallbackExpansionMap.entrySet()) {
            if (entry.getKey().contains(keyword) || keyword.contains(entry.getKey())) {
                expandedKeywords.addAll(entry.getValue());
            }
        }
    }

    /**
     * 여러 키워드를 한번에 확장합니다.
     */
    public List<String> expandKeywords(List<String> keywords) {
        Set<String> allExpandedKeywords = new HashSet<>();
        
        for (String keyword : keywords) {
            List<String> expanded = expandKeyword(keyword);
            allExpandedKeywords.addAll(expanded);
        }
        
        List<String> result = new ArrayList<>(allExpandedKeywords);
        log.info("전체 키워드 확장: {}개 → {}개", keywords.size(), result.size());
        return result;
    }

    /**
     * 표준국어대사전 XML 응답에서 뉴스 분야에 맞는 sense만 선택해서 키워드 추출
     */
    private void parseXmlAndExtractNewsKeywords(String xmlResponse, Set<String> keywords, String originalKeyword) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")));
            
            // sense 단위로 처리
            NodeList senseNodes = doc.getElementsByTagName("sense_info");
            
            for (int i = 0; i < senseNodes.getLength(); i++) {
                Element senseElement = (Element) senseNodes.item(i);
                
                // definition 추출
                NodeList definitionNodes = senseElement.getElementsByTagName("definition");
                if (definitionNodes.getLength() > 0) {
                    String definition = definitionNodes.item(0).getTextContent().trim();
                    log.debug("키워드 '{}' - Sense {}: {}", originalKeyword, i+1, definition);
                    
                    // 뉴스 분야에 맞는 definition인지 확인
                    String matchedField = findMatchingNewsField(definition);
                    if (matchedField != null) {
                        log.info("키워드 '{}' - {}분야 sense 발견: {}", originalKeyword, matchedField, definition);
                        
                        // 이 sense에서 관련 키워드 추출
                        extractKeywordsFromSense(senseElement, keywords);
                    }
                }
            }
            
        } catch (Exception e) {
            log.warn("표준국어대사전 XML 파싱 실패: {}", e.getMessage());
        }
    }

    /**
     * definition이 어떤 뉴스 분야에 해당하는지 확인
     */
    private String findMatchingNewsField(String definition) {
        for (Map.Entry<String, Pattern> entry : newsFieldPatterns.entrySet()) {
            if (entry.getValue().matcher(definition).matches()) {
                return entry.getKey();
            }
        }
        return null; // 뉴스 분야와 맞지 않음
    }

    /**
     * 특정 sense에서 관련 키워드 추출
     */
    private void extractKeywordsFromSense(Element senseElement, Set<String> keywords) {
        // 1. sense 내의 lexical_info에서 관련어 추출
        NodeList lexicalNodes = senseElement.getElementsByTagName("lexical_info");
        for (int i = 0; i < lexicalNodes.getLength(); i++) {
            Element lexicalElement = (Element) lexicalNodes.item(i);
            NodeList wordNodes = lexicalElement.getElementsByTagName("word");
            if (wordNodes.getLength() > 0) {
                String word = wordNodes.item(0).getTextContent().trim();
                if (isValidKeyword(word)) {
                    keywords.add(word);
                    log.debug("관련어 추가: {}", word);
                }
            }
        }
        
        // 2. definition에서 명사 추출
        NodeList definitionNodes = senseElement.getElementsByTagName("definition");
        if (definitionNodes.getLength() > 0) {
            String definition = definitionNodes.item(0).getTextContent().trim();
            extractNounsFromDefinition(definition, keywords);
        }
    }

    /**
     * definition에서 명사 키워드 추출
     */
    private void extractNounsFromDefinition(String definition, Set<String> keywords) {
        // 간단한 명사 추출 로직
        String[] words = definition.split("[\\s,;:.()\\[\\]\"'~]+");
        for (String word : words) {
            word = word.trim();
            if (isValidKeyword(word) && word.length() >= 2 && word.length() <= 8) {
                // 한글 명사로 보이는 단어만 추가
                if (word.matches(".*[가-힣].*") && !word.matches(".*[0-9].*")) {
                    keywords.add(word);
                    log.debug("definition에서 키워드 추출: {}", word);
                }
            }
        }
    }

    /**
     * 유효한 키워드인지 검증
     */
    private boolean isValidKeyword(String word) {
        if (word == null || word.length() < 2 || word.length() > 20) {
            return false;
        }
        
        // 특수문자나 숫자만으로 구성된 단어 제외
        if (word.matches("^[^가-힣a-zA-Z]+$")) {
            return false;
        }
        
        // 불용어 제외
        List<String> stopWords = Arrays.asList("것", "수", "때", "등", "및", "또는", "그", "이", "그것", "이것", "하나", "둘", "셋", "관한", "대한", "따른", "위한", "통한");
        return !stopWords.contains(word);
    }
} 