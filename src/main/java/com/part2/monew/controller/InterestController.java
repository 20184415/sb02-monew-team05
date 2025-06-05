package com.part2.monew.controller;

import com.part2.monew.dto.request.InterestRegisterRequestDto;
import com.part2.monew.dto.request.InterestSearchRequest;
import com.part2.monew.dto.request.InterestUpdateRequestDto;
import com.part2.monew.dto.response.CursorPageResponse;
import com.part2.monew.dto.response.InterestDto;
import com.part2.monew.service.InterestService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

  private final InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestDto> registerInterest(@Valid @RequestBody
      InterestRegisterRequestDto requestDto, @RequestHeader(value = "Monew-Request-User-ID",required = false)
      UUID requestUserId){
    InterestDto createdInterest = interestService.registerInterest(requestDto, requestUserId);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdInterest);
  }

  @PatchMapping("/{interestId}")
  public ResponseEntity<InterestDto> updateInterestKeywords(@PathVariable UUID interestId, @Valid @RequestBody
      InterestUpdateRequestDto requestDto, @RequestHeader(value = "Monew-Request-User-Id", required = false) UUID requestUserId) {
    InterestDto updatedIntertest = interestService.updateInterestKeywords(interestId, requestDto,
        requestUserId);
    return ResponseEntity.ok(updatedIntertest);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<InterestDto>> searchInterests(
      @Valid InterestSearchRequest searchRequestDto,
      @RequestHeader("Monew-Request-User-ID") UUID requestUserId
  ) {
    CursorPageResponse<InterestDto> response = interestService.searchInterests(
        searchRequestDto.keyword(),
        searchRequestDto.orderBy(),
        searchRequestDto.direction(),
        searchRequestDto.cursor(),
        searchRequestDto.after(),
        searchRequestDto.limit(),
        requestUserId
    );
    return ResponseEntity.ok(response);
  }

  @GetMapping("/temp")
  public ResponseEntity<List<Map<String, Object>>> getInterests() {
    // 임시로 빈 관심사 목록 반환
    List<Map<String, Object>> interests = List.of(
        Map.of("id", 1, "name", "경제", "subscriberCount", 0),
        Map.of("id", 2, "name", "정치", "subscriberCount", 0),
        Map.of("id", 3, "name", "스포츠", "subscriberCount", 0),
        Map.of("id", 4, "name", "기술", "subscriberCount", 0),
        Map.of("id", 5, "name", "문화", "subscriberCount", 0)
    );
    
    return ResponseEntity.ok(interests);
  }

}
