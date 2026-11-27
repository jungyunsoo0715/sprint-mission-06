package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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

    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile)
            throws IOException {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        BinaryContent profileImage = null;

        if (profile != null) {
            // 아래 코드에서 profile.getBytes()는 IOException(Checked Exception)을 발생시킬 수 있기 때문에
            // 기본 @Transactional 설정에서는 자동으로 롤백되지 않습니다. 때문에
            // createProfileImage()의 save()까지는 호출되지만,
            // getBytes()에서 예외가 발생하면 put()은 호출되지 않습니다.
            // profileImage = this.createProfileImage(profile);
            // this.binaryContentStorage.put(profileImage.getId(), profile.getBytes());

            // 때문에, profile.getBytes()를 먼저 호출해서 예외가 발생하지 않으면
            // 이후 코드를 실행하게 하는 것이 적합합니다.
            byte[] bytes = profile.getBytes();
            profileImage = this.createProfileImage(profile);
            this.binaryContentStorage.put(profileImage.getId(), bytes);
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

        return this.userMapper.toDto(user);
    }

    private BinaryContent createProfileImage(MultipartFile profile) throws IOException {
            BinaryContent profileImage = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType()
            );

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
    @Transactional(readOnly = true)
    public UserResponse find(UUID userId) {
        User user = findUserEntity(userId);
        UserStatus userStatus = user.getUserStatus();

        if (userStatus == null) {
            throw new UserStatusNotFoundException(
                    "사용자 ID " + user.getId() + "에 해당하는 UserStatus를 찾을 수 없습니다."
            );
        }

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
    @Transactional(readOnly = true)
    public UserListResponse findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserStatus userStatus = user.getUserStatus();

            if (userStatus == null) {
                throw new UserStatusNotFoundException(
                        "사용자 ID " + user.getId() + "에 해당하는 UserStatus를 찾을 수 없습니다."
                );
            }
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

        BinaryContent profileImage = user.getProfile();


        if (profile != null) {
            byte[] bytes = profile.getBytes();

            if (profileImage != null) {
                profileImage.setFileName(profile.getOriginalFilename());
                profileImage.setSize(profile.getSize());
                profileImage.setContentType(profile.getContentType());
            } else {
                profileImage = this.createProfileImage(profile);
            }

            this.binaryContentStorage.put(profileImage.getId(), bytes);
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                profileImage
        );

        this.userRepository.flush();

        return this.userMapper.toDto(user);
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
