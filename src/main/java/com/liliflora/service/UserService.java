package com.liliflora.service;

import com.liliflora.dto.UserRequestDto;
import com.liliflora.entity.User;
import com.liliflora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public String signup(UserRequestDto.signup requestDto) {

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .build();

        userRepository.save(user);  // extends 한 JpaRepository 에서 제공
        return "Success";
    }

}
