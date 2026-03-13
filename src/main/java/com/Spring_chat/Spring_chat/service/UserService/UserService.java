package com.Spring_chat.Spring_chat.service.UserService;

import com.Spring_chat.Spring_chat.dto.ApiResponse;
import com.Spring_chat.Spring_chat.dto.PageResponse;
import com.Spring_chat.Spring_chat.dto.user.MyProfileUserDTO;
import com.Spring_chat.Spring_chat.dto.user.ProfileUserDTO;
import com.Spring_chat.Spring_chat.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

public interface UserService  {
    ApiResponse<ProfileUserDTO> getUser(Long id);
    ApiResponse<MyProfileUserDTO> getMyProfile(Long id);
}
