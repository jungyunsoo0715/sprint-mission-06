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
    // 메서드 안의 DB 작업을 하나의 트랜잭션으로 묶습니다.
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile profile)
            throws IOException {

        // 입력 받은 username이 이미 사용중인지 검사합니다.
        this.validateUniqueUsername(request.username());
        // 입력 받은 email이 이미 사용중인지 검사합니다.
        this.validateUniqueEmail(request.email());

        // profileImage는 선택 사항입니다.
        // User를 생성할 때 프로필 이미지를 같이 업로드 하지 않았을 때를 대비하여
        // profileImage를 null로 할당합니다.
        BinaryContent profileImage = null;

        // 업로드한 프로필 이미지가 존재한다면
        // 프로필 이미지를 로컬 디렉터리에 저장하고,
        // 그 메타데이터는 DB에 저장합니다
        if (profile != null) {
            profileImage = this.createProfileImage(profile);
        }

        // User객체를 생성합니다.
        User user = new User(profileImage,
                request.username(),
                request.email(),
                request.password()
        );

        // UserStatus 객체를 생성합니다.
        UserStatus userStatus = new UserStatus(user);
        // User가 생성되었음으로
        // 사용자의 온라인 상태를 확인하는 메서드를 호출해
        // 사용자의 온라인 여부를 갱신합니다.
        userStatus.updateOnlineStatus();

        // User에 UserStatus객체를 주입합니다.
        user.setUserStatus(userStatus);

        // User객체를 영속화합니다.
        // 이때, User는 새로 생성된 객체임으로, save()시에 persist()가 호출됩니다.
        // persist()는 인자로 받은 객체를 영속화합니다. persist() 자체의 반환 타입은 void입니다
        // save()는 영속성 컨텍스트에 등록되어 관리되는 객체를 반환합니다.
        user = this.userRepository.save(user);
        /*
        객체 생성 후 저장하는 경우에는 save()의 반환 값을 사용하지 않아도 무방합니다.
        이는 this.userRepository.save()가 persist()를 호출하기 때문인데,
        persist()의 인자로 전달된 객체를 영속화하기 때문입니다.

        만약 save()가 merge()를 호출한다면, save()의 반환값을 사용하는 것이 적절합니다.
        merge()는 인자로 전달된 객체가 영속 상태라면 영속 상태인 객체를 참조하는 변수를 반환하고
        준영속 상태인 객체를 인자로 전달 받았다면, 객체를 복사하여 영속상태로 만든 뒤 반환합니다.
        때문에 merge()에 전달되는 인자가 준영속 상태라면, 전달된 인자를 사용하지않고,
        영속화된 반환값을 사용하여 @LastModifiedDate 가 붙은 updatedAt 필드가 갱신된 객체를
        사용할 수 있습니다.
         */
        // User객체를 매핑하여 DTO를 반환합니다.
        return this.userMapper.toDto(user);
    }

    // 업로드한 파일의 바이트값을 읽는 과정(profile.getBytes())에서 IOException이 발생할 수 있습니다.
    // @Transactional이 필요하지 않은 이유는,
    // 이미 이 메서드가 @Transactional이 적용된 메서드 내부에서 호출되기 때문입니다.
    private BinaryContent createProfileImage(MultipartFile profile) throws IOException {
        // 업로드한 이미지의 byte값을 가져옵니다.
        byte[] bytes = profile.getBytes();

        // BinaryContent 객체를 생성합니다.
        BinaryContent profileImage = new BinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
        );

        // 생성된 BinaryContent 객체를 영속화하고, 영속화 된 객체의 참조를 반환합니다.
        profileImage = this.binaryContentRepository.save(profileImage);
        /*
        영속화 한다는 것은 영속성 컨텍스트에 객체를 등록한다는 것입니다.
        save()시에 DB에 INSERT() 쿼리가 반드시 호출된다는 보장은 없습니다.
         */

        // 이미지를 저장합니다, BinaryContent의 ID를 키로 이미지 바이트를 저정합니다.
        this.binaryContentStorage.put(profileImage.getId(), bytes);

        // BinaryContent 객체를 반환합니다.
        return profileImage;
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
    // readOnly = true는 Dirty Checking 기능을 사용하지않습니다.
    /*
    영속성 컨텍스트를 유지한다는 것은 엔티티 객체의 상태가 관리중이라는 것을 의미합니다.
    영속성 컨텍스트를 유지하는 경우는 추가 쿼리에 필요한 정보를
    영속성 컨텍스트에 등록된 엔티티 객체로부터 가져올 수 있습니다.
    따라서 지연 로딩일 경우 @Transactional이 필요할 수도 있지만,
    이미 필요한 정보를 다 불러왔을 경우,
    추가 쿼리 없이 데이터를 읽을 수 있으므로 영속성 컨텍스트를 유지할 필요가 없습니다.
     */
    @Transactional(readOnly = true)
    public UserResponse find(UUID userId) {
        // User객체를 가져옵니다.
        User user = findUserEntity(userId);
        // User가 참조하는 UserStatus객체를 가져옵니다.
        UserStatus userStatus = user.getUserStatus();

        // User가 참조하는 UserStatus가 존재하지 않는다면, 예외를 발생시킵니다.
        if (userStatus == null) {
            throw new UserStatusNotFoundException(
                    "사용자 ID " + user.getId() + "에 해당하는 UserStatus를 찾을 수 없습니다."
            );
        }

        // userStatus의 온라인 여부를 갱신합니다.
        userStatus.updateOnlineStatus();

        // User와 UserStatus를 매핑해 DTO를 반환합니다.
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

        // 업데이트시 프로필 이미지를 업로드 하지 않았을 때
        if (profile != null) {
            // 현재 프로필 이미지가 있다면, 기존의 프로필 이미지에 덮어씌웁니다.
            if (profileImage != null) {
                byte[] bytes = profile.getBytes();
                profileImage.setFileName(profile.getOriginalFilename());
                profileImage.setSize(profile.getSize());
                profileImage.setContentType(profile.getContentType());
                this.binaryContentStorage.put(profileImage.getId(), bytes);
            // 현재 프로필 이미지가 없다면, 새로운 프로필 이미지를 생성합니다.
            } else {
                profileImage = this.createProfileImage(profile);
            }
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                profileImage
        );

        // user.update()는 user의 필드값을 변경(영속성 컨텍스트에 변경사항이 생김)하지만
        // DB에는 반영하지 않습니다
        // flush()를 호출하여 DB에 엔티티의 변경사항을 저정합니다.
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
