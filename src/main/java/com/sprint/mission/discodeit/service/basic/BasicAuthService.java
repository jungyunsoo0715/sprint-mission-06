package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.dto.authentication.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jdk.jfr.Timespan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        User user = this.userRepository.findByUsername(loginRequest.username());

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException(
                    "입력하신 비밀번호 " + loginRequest.password() + "가 옳바르지 않습니다.");
        }

        return this.authMapper.toLoginResponse(user);
    }
}
