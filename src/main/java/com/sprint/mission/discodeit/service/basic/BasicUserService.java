package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile)
            throws IOException {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        BinaryContent profileImage = null;

        if (profile != null) {
            profileImage = this.createProfileImage(profile);
        }

        User user = new User(profileImage,
                request.username(),
                request.email(),
                request.password()
        );

        UserStatus userStatus = new UserStatus(user);
        userStatus.updateOnlineStatus();

        user.setUserStatus(userStatus);

        user = this.userRepository.save(user);

        return this.userMapper.toUserDto(user);
    }

    private BinaryContent createProfileImage(MultipartFile profile) throws IOException {
            BinaryContent profileImage = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType(),
                    profile.getBytes());

        return this.binaryContentRepository.save(profileImage);
    }

    private void validateUniqueUsername(String username) {
        boolean isUsernameTaken = this.userRepository.existsByUsername(username);

        if (isUsernameTaken) {
            throw new DuplicateResourceException(
                    "이미 사용 중인 사용자 이름입니다: " + username
            );
        }
    }

    private void validateUniqueEmail(String email) {
        boolean isEmailTaken = this.userRepository.existsByEmail(email);

        if (isEmailTaken) {
            throw new DuplicateResourceException(
                    "이미 사용 중인 이메일입니다: " + email
            );
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = findUserEntity(userId);
        UserStatus userStatus = user.getUserStatus();

        userStatus.updateOnlineStatus();

        return this.userMapper.toUserResponse(user, userStatus);
    }

    @Override
    public User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
                ));
    }

    @Override
    public UserListResponse findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserStatus userStatus = user.getUserStatus();
            userStatus.updateOnlineStatus();

            userResponses.add(this.userMapper.toUserResponse(user, userStatus));
        }

        return new UserListResponse(userResponses);
    }

    @Override
    @Transactional
    public UserDto update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    ) throws IOException {

        User user = this.findUserEntity(userId);

        if (request.newUsername() != null
                && !request.newUsername().equals(user.getUsername())) {
            this.validateUniqueUsername(request.newUsername());
        }

        if (request.newEmail() != null
                && !request.newEmail().equals(user.getEmail())) {
            this.validateUniqueEmail(request.newEmail());
        }

        BinaryContent oldProfile = user.getProfile();
        BinaryContent newProfile = null;

        if (profile != null) {
            newProfile = this.createProfileImage(profile);
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                newProfile
        );

        if (newProfile != null && oldProfile != null) {
            this.binaryContentRepository.delete(oldProfile);
        }

        return this.userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        BinaryContent profileImage = null;

        if (user.getProfile() != null) {
            profileImage = user.getProfile();
            this.binaryContentRepository.deleteById(profileImage.getId());
        }

        this.userRepository.deleteById(userId);
    }
}
