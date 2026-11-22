package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    private final BinaryContentRepository binaryContentRepository;

    private final UserMapper userMapper;

    @Override
    // 프로필 이미지, User, UserStatus 저장을 하나의 트랜잭션으로 묶기 위함입니다.
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile)
            throws IOException {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        BinaryContent profileImage = null;
        // UUID newProfileId = null;

        if (profile != null) {
            profileImage = this.createProfileImage(profile);
            // newProfileId = profileImage.getId();
        }

        User user = new User(profileImage,
                request.username(),
                request.email(),
                request.password()
        );

        UserStatus userStatus = new UserStatus(user);
        userStatus.makeOnline();

        user.setUserStatus(userStatus);

        // this.userRepository.save(user); // 여기서 user는 비영속화된 객체를  참조합니다.
        user = this.userRepository.save(user); // 여기서 앞의 user는 영속화된 객체를 참조합니다.
        // 영속성 컨텍스트의 관리 대상입니다.
        // save()로 영속화 한다면, createdAt 같은 값을 JPA가 갱신하기 때문에, 영속화 된 객체를
        // 사용하는 것이 좋습니다.

        // cascade 설정으로 인해 같이 영속화와 관련 작업이 이루어지므로,
        // 명시적으로 save()를 호출하여 저장할 필요가 없습니다.
        // this.userStatusRepository.save(userStatus);

        return this.userMapper.toUserDto(user);
    }

    private BinaryContent createProfileImage(MultipartFile profile) throws IOException {
            BinaryContent profileImage = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType(),
                    profile.getBytes());

            // this.binaryContentRepository.save(profileImage);

            // return profileImage;

        return this.binaryContentRepository.save(profileImage);
    }

    private void validateUniqueUsername(String username) {
        /*
        boolean isUsernameTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
         */
        boolean isUsernameTaken = this.userRepository.existsByUsername(username);

        if (isUsernameTaken) {
            throw new DuplicateResourceException(
                    "이미 사용 중인 사용자 이름입니다: " + username
            );
        }
    }

    private void validateUniqueEmail(String email) {
        /*
        boolean isEmailTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
         */
        boolean isEmailTaken = this.userRepository.existsByEmail();

        if (isEmailTaken) {
            throw new DuplicateResourceException(
                    "이미 사용 중인 이메일입니다: " + email
            );
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = findUserEntity(userId);
        // UserStatus userStatus = this.findUserStatus(user.getId());
        UserStatus userStatus = user.getUserStatus();
        // 이미 전부 영속성 컨텍스트의 관리 대상이기 때문에,
        // 따로 save()로 반환한 객체를 가져올 필요가 없습니다.
        userStatus.makeOnline();

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
            /*
            UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new UserStatusNotFoundException(
                            "ID가 " + user.getId() + "인 user의 userStatus를 찾을 수 없습니다."
                    ));
             */
            UserStatus userStatus = user.getUserStatus();
            userStatus.updateOnlineStatus();

            userResponses.add(this.userMapper.toUserResponse(user, userStatus));
        }

        return new UserListResponse(userResponses);
    }

    @Override
    public UserDto update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    ) throws IOException {

        User user = this.findUserEntity(userId);

        String newUsername = null;
        String newEmail = null;
        String newPassword = null;

        if (request.newUsername() != null) {
            this.validateUniqueUsername(request.newUsername());
            newUsername = request.newUsername();
        }
        if (request.newEmail() != null) {
            this.validateUniqueEmail(request.newEmail());
            newEmail = request.newEmail();
        }
        if (request.newPassword() != null) {
            newPassword = request.newPassword();
        }

        UUID oldProfileId = user.getProfile().getId();
        UUID newProfileId = null;

        BinaryContent newProfileImage = null;

        if (profile != null) {
            newProfileImage = this.createProfileImage(profile);
            newProfileId = newProfileImage.getId();
        }

        user.update(
                newUsername,
                newEmail,
                newPassword,
                newProfileImage
                // newProfileId
        );

        userRepository.save(user);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return this.userMapper.toUserDto(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다.");
        }

        /*
        deleteMessageAttachmentsByUserId(userId);
        deleteMessagesByUserId(userId);
        deleteReadStatusesByUserId(userId);
        deleteUserStatusesByUserId(userId);
        deleteProfileImageByUserId(userId);
         */

        this.userRepository.deleteById(userId);
    }

    /*
    private void deleteMessageAttachmentsByUserId(UUID userId) {
        List<Message> messages = this.messageRepository.findAll();
        List<List<UUID>> attachmentIdsList = messages.stream()
                .filter(m -> m.getAuthor().getId().equals(userId))
                .map(Message::getAttachments)
                .toList();

        attachmentIdsList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet())
                .forEach(this.binaryContentRepository::deleteById);
    }
     */

    /*
    private void deleteMessagesByUserId(UUID userId) {
        List<Message> messages = this.messageRepository.findAll();

        messages.stream()
                .filter(m -> m.getAuthor().getId().equals(userId))
                .forEach(m -> this.messageRepository.deleteById(m.getId()));
    }
     */

    /*
    private void deleteReadStatusesByUserId(UUID userId) {
        List<ReadStatus> readStatuses = this.readStatusRepository.findAllByUserId(userId);

        readStatuses.stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .forEach(r -> this.readStatusRepository.deleteById(r.getId()));
    }
     */

    /*
    private void deleteUserStatusesByUserId(UUID userId) {
        UserStatus userStatus = this.findUserStatus(userId);

        this.userStatusRepository.deleteById(userStatus.getId());
    }
     */

    /*
    private void deleteProfileImageByUserId(UUID userId) {
        User user = this.findUserEntity(userId);
        UUID profileId = user.getProfile().getId();
        if (profileId != null) {
            this.binaryContentRepository.deleteById(profileId);
        }
    }
     */

    @Override
    public UserStatus findUserStatus(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
            );
        }

        Optional<UserStatus> userStatus = this.userStatusRepository.findByUserId(userId);

        return userStatus.orElseThrow(() ->
                new UserStatusNotFoundException(
                        "사용자 ID " + userId + "에 해당하는 UserStatus를 찾을 수 없습니다."
                )
        );
    }

    public Optional<BinaryContent> findBinaryContent(UUID userId) {
        User user = this.findUserEntity(userId);
        UUID profileId = user.getProfile().getId();

        if (profileId == null) return Optional.empty();

        return binaryContentRepository.findById(profileId);
    }
}
