package com.Spring_chat.Spring_chat.service.UserService;

import com.Spring_chat.Spring_chat.dto.ApiResponse;
import com.Spring_chat.Spring_chat.dto.user.MyProfileUserDTO;
import com.Spring_chat.Spring_chat.dto.user.ProfileUserDTO;
import com.Spring_chat.Spring_chat.entity.User;
import com.Spring_chat.Spring_chat.exception.AppException;
import com.Spring_chat.Spring_chat.exception.ErrorCode;
import com.Spring_chat.Spring_chat.mappers.UserMapper;
import com.Spring_chat.Spring_chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ProfileUserDTO> getUser(Long id) {
        User user = findUserOrThrow(id);
        return ApiResponse.ok("OK", userMapper.userToUserDTO(user));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<MyProfileUserDTO> getMyProfile(Long id) {
        User user = findUserOrThrow(id);
        return ApiResponse.ok("OK", userMapper.userToMyUserDTO(user));
    }

    private User findUserOrThrow(Long id) {
        if (id == null) {
            throw new AppException(ErrorCode.MISSING_PARAMETER, "Missing user id");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }
}
