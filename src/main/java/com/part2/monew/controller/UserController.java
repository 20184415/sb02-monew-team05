package com.part2.monew.controller;

import com.part2.monew.dto.request.UserCreateRequest;
import com.part2.monew.dto.request.UserLoginRequest;
import com.part2.monew.dto.request.UserUpdateRequest;
import com.part2.monew.dto.response.UserResponse;
import com.part2.monew.entity.User;
import com.part2.monew.mapper.UserMapper;
import com.part2.monew.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;

    @PostMapping("")
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.ok(userServiceImpl.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody @Valid UserLoginRequest request,
            HttpServletResponse response
    ) {
        User user = userServiceImpl.loginUser(request);
        response.setHeader("MoNew-Request-User-ID", user.getId().toString());
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateNickname(
        @PathVariable UUID userId,
        @RequestHeader("MoNew-Request-User-ID") UUID requestUserId,
        @RequestBody @Valid UserUpdateRequest request
    ) {
        UserResponse updatedUser = userServiceImpl.updateNickname(userId, requestUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID userId,
        @RequestHeader("MoNew-Request-User-ID") UUID requestUserId
    ) {
        userServiceImpl.delete(userId, requestUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> deleteHard(
        @PathVariable UUID userId,
        @RequestHeader("MoNew-Request-User-ID") UUID requestUserId
    ) {
        userServiceImpl.deleteHard(userId, requestUserId);
        return ResponseEntity.noContent().build();
    }
}
