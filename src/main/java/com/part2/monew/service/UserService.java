package com.part2.monew.service;

import com.part2.monew.dto.request.UserCreateRequest;
import com.part2.monew.dto.request.UserUpdateRequest;
import com.part2.monew.dto.response.UserResponse;
import com.part2.monew.entity.User;
import com.part2.monew.mapper.UserMapper;
import com.part2.monew.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(UserCreateRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse updateNickname(UUID userId, UUID requestUserId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(requestUserId)){
            throw new RuntimeException("No permission to modify this user");
        }
        user.setNickname(request.getNickname());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public void delete(UUID userId, UUID requestUserId) {
        User user = userRepository.findByIdAndActiveTrue(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(requestUserId)){
            throw new RuntimeException("No permission to delete this user");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    public void deleteHard(UUID userId, UUID requestUserId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(requestUserId)){
            throw new RuntimeException("No permission to delete this user");
        }

        userRepository.delete(user);
    }
}
