package javaproject.service;

import javaproject.domain.User;
import javaproject.repository.UserRepository;
import javaproject.exception.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UserService {

    // 싱글톤 인스턴스
    private static final UserService instance = new UserService();

    // Repository 인스턴스
    private final UserRepository userRepository;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private UserService() {
        this.userRepository = UserRepository.getInstance();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return UserService의 유일한 인스턴스
     */
    public static UserService getInstance() {
        return instance;
    }

    /**
     * 회원가입
     *
     * @param user 가입할 사용자 정보
     * @return 가입된 사용자
     * @throws DuplicateUserException ID가 이미 존재하는 경우
     */
    public User register(User user) throws DuplicateUserException {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 null입니다.");
        }

        // ID 중복 확인
        if (userRepository.existsById(user.getId())) {
            throw new DuplicateUserException("이미 사용 중인 아이디입니다: " + user.getId());
        }

        // 이메일 중복 확인
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("이미 사용 중인 이메일입니다: " + user.getEmail());
        }

        // 가입일시 설정
        user.setCreatedDate(LocalDateTime.now());

        // 저장
        return userRepository.save(user);
    }

    /**
     * 로그인
     *
     * @param id 사용자 ID
     * @param password 비밀번호
     * @return 로그인된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws InvalidPasswordException 비밀번호가 일치하지 않는 경우
     */
    public User login(String id, String password)
            throws UserNotFoundException, InvalidPasswordException {

        if (id == null || password == null) {
            throw new IllegalArgumentException("ID와 비밀번호를 입력해주세요.");
        }

        // 사용자 조회
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 아이디입니다: " + id);
        }

        // 비밀번호 확인
        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        // 마지막 로그인 시간 업데이트
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.update(user);

        return user;
    }

    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 조회된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User getUserById(String id) throws UserNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }

        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + id);
        }

        return user;
    }

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 조회된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User getUserByEmail(String email) throws UserNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일이 유효하지 않습니다.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: " + email);
        }

        return user;
    }

    /**
     * 모든 사용자 조회
     *
     * @return 전체 사용자 목록
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 사용자 정보 수정
     *
     * @param user 수정할 사용자 정보
     * @return 수정된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User updateUser(User user) throws UserNotFoundException {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("사용자 정보가 유효하지 않습니다.");
        }

        // 사용자 존재 확인
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + user.getId());
        }

        // 수정일시 업데이트
        user.setModifiedDate(LocalDateTime.now());

        return userRepository.update(user);
    }

    /**
     * 비밀번호 변경
     *
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws InvalidPasswordException 현재 비밀번호가 일치하지 않는 경우
     */
    public void changePassword(String userId, String currentPassword, String newPassword)
            throws UserNotFoundException, InvalidPasswordException {

        User user = getUserById(userId);

        // 현재 비밀번호 확인
        if (!user.getPassword().equals(currentPassword)) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 설정
        user.setPassword(newPassword);
        user.setModifiedDate(LocalDateTime.now());

        userRepository.update(user);
    }

    /**
     * 사용자 삭제
     *
     * @param userId 삭제할 사용자 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public void deleteUser(String userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        userRepository.delete(userId);
    }

    /**
     * 사용자 ID 존재 여부 확인
     *
     * @param userId 확인할 ID
     * @return 존재하면 true
     */
    public boolean isUserIdExists(String userId) {
        return userRepository.existsById(userId);
    }

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 확인할 이메일
     * @return 존재하면 true
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 관리자 목록 조회
     *
     * @return 관리자 목록
     */
    public List<User> getAdmins() {
        return userRepository.findAdmins();
    }

    /**
     * 일반 사용자 목록 조회
     *
     * @return 일반 사용자 목록
     */
    public List<User> getRegularUsers() {
        return userRepository.findRegularUsers();
    }

    /**
     * 이름으로 사용자 검색
     *
     * @param name 검색할 이름
     * @return 검색된 사용자 목록
     */
    public List<User> searchUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("검색할 이름을 입력해주세요.");
        }

        return userRepository.findByName(name);
    }

    /**
     * 사용자 권한 변경
     *
     * @param userId 사용자 ID
     * @param isAdmin 관리자 여부
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public void changeUserRole(String userId, boolean isAdmin)
            throws UserNotFoundException {

        User user = getUserById(userId);
        user.setAdmin(isAdmin);
        user.setModifiedDate(LocalDateTime.now());

        userRepository.update(user);
    }

    /**
     * 사용자 통계 정보 조회
     *
     * @return 통계 정보
     */
    public UserStatistics getStatistics() {
        UserStatistics stats = new UserStatistics();

        List<User> allUsers = userRepository.findAll();
        stats.setTotalUsers(allUsers.size());

        int adminCount = 0;
        for (User user : allUsers) {
            if (user.isAdmin()) {
                adminCount++;
            }
        }

        stats.setAdminCount(adminCount);
        stats.setRegularUserCount(allUsers.size() - adminCount);

        return stats;
    }

    /**
     * 사용자 통계 정보 클래스
     */
    public static class UserStatistics {
        private int totalUsers;
        private int adminCount;
        private int regularUserCount;

        // Getters and Setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getAdminCount() { return adminCount; }
        public void setAdminCount(int adminCount) {
            this.adminCount = adminCount;
        }

        public int getRegularUserCount() { return regularUserCount; }
        public void setRegularUserCount(int regularUserCount) {
            this.regularUserCount = regularUserCount;
        }
    }
}