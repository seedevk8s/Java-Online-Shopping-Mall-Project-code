// =================================================================
// UserRepository.java - 사용자 데이터 저장소
// =================================================================
package javaproject.repository;

import javaproject.domain.User;
import javaproject.exception.AuthenticationException;
import javaproject.exception.FileIOException;
import javaproject.exception.UserNotFoundException;
import javaproject.util.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 데이터를 파일에 저장하고 조회하는 Repository 클래스
 * 회원가입, 로그인, 사용자 정보 관리 등에 사용
 */
public class UserRepository implements BaseRepository<User, String> {

    // 사용자 데이터를 저장할 파일명
    private static final String USER_FILE_NAME = "users.txt";

    // 싱글톤 인스턴스 (메모리 절약을 위해)
    private static UserRepository instance;

    /**
     * private 생성자 (싱글톤 패턴)
     */
    private UserRepository() {
        // 초기화 시 데이터 디렉토리 생성
        FileManager.initializeDataDirectory();

        // 기본 관리자 계정이 없으면 생성
        initializeDefaultAdmin();
    }

    /**
     * UserRepository 인스턴스 반환 (싱글톤)
     * @return UserRepository 인스턴스
     */
    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /**
     * 기본 관리자 계정 초기화
     * 시스템 시작 시 admin 계정이 없으면 자동 생성
     */
    private void initializeDefaultAdmin() {
        try {
            // admin 계정이 이미 존재하는지 확인
            if (!existsById("admin")) {
                // 기본 관리자 계정 생성
                User adminUser = new User("admin", "admin123", "관리자", "admin@shop.com", true);
                save(adminUser);
                System.out.println("기본 관리자 계정이 생성되었습니다. (ID: admin, PW: admin123)");
            }
        } catch (FileIOException e) {
            System.err.println("기본 관리자 계정 초기화 실패: " + e.getMessage());
        }
    }

    // ================= BaseRepository 인터페이스 구현 =================

    /**
     * 새로운 사용자 저장 (회원가입)
     * @param user 저장할 사용자 객체
     * @return 저장 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean save(User user) throws FileIOException {
        if (user == null || user.getUserId() == null) {
            return false;
        }

        // 중복 ID 검사
        if (existsById(user.getUserId())) {
            return false; // 이미 존재하는 사용자 ID
        }

        // 기존 사용자 목록 조회
        List<User> users = findAll();

        // 새 사용자 추가
        users.add(user);

        // 파일에 저장
        saveAllUsers(users);

        return true;
    }

    /**
     * 사용자 ID로 사용자 조회
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public Optional<User> findById(String userId) throws FileIOException {
        if (userId == null || userId.trim().isEmpty()) {
            return Optional.empty();
        }

        List<User> users = findAll();

        // 사용자 ID로 검색
        return users.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst();
    }

    /**
     * 모든 사용자 조회
     * @return 전체 사용자 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public List<User> findAll() throws FileIOException {
        List<String> lines = FileManager.readLines(USER_FILE_NAME);
        List<User> users = new ArrayList<>();

        // 각 줄을 User 객체로 변환
        for (String line : lines) {
            try {
                User user = User.fromFileString(line);
                users.add(user);
            } catch (Exception e) {
                // 잘못된 형식의 줄은 무시하고 로그만 출력
                System.err.println("사용자 데이터 파싱 오류: " + line + " - " + e.getMessage());
            }
        }

        return users;
    }

    /**
     * 사용자 정보 수정
     * @param user 수정할 사용자 객체
     * @return 수정 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean update(User user) throws FileIOException {
        if (user == null || user.getUserId() == null) {
            return false;
        }

        List<User> users = findAll();
        boolean found = false;

        // 해당 사용자를 찾아서 정보 업데이트
        for (int i = 0; i < users.size(); i++) {
            if (user.getUserId().equals(users.get(i).getUserId())) {
                users.set(i, user); // 기존 사용자 정보를 새 정보로 교체
                found = true;
                break;
            }
        }

        if (found) {
            saveAllUsers(users); // 전체 목록을 파일에 다시 저장
            return true;
        }

        return false; // 해당 사용자가 없음
    }

    /**
     * 사용자 ID로 사용자 삭제
     * @param userId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean deleteById(String userId) throws FileIOException {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        List<User> users = findAll();
        boolean removed = users.removeIf(user -> userId.equals(user.getUserId()));

        if (removed) {
            saveAllUsers(users); // 변경된 목록을 파일에 저장
            return true;
        }

        return false; // 해당 사용자가 없음
    }

    /**
     * 사용자 존재 여부 확인
     * @param userId 확인할 사용자 ID
     * @return 존재하면 true
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public boolean existsById(String userId) throws FileIOException {
        return findById(userId).isPresent();
    }

    /**
     * 전체 사용자 수
     * @return 총 사용자 수
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public long count() throws FileIOException {
        return findAll().size();
    }

    // ================= 사용자 특화 메서드들 =================

    /**
     * 로그인 처리 (아이디와 비밀번호 확인)
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @return 로그인 성공한 사용자 객체 (실패 시 null)
     * @throws FileIOException 파일 읽기 실패 시
     * @throws AuthenticationException 인증 실패 시
     */
    public User login(String userId, String password) throws FileIOException, AuthenticationException {
        if (userId == null || password == null) {
            throw new AuthenticationException("아이디와 비밀번호를 입력해주세요.");
        }

        Optional<User> userOpt = findById(userId);

        if (!userOpt.isPresent()) {
            throw new AuthenticationException("존재하지 않는 사용자입니다.");
        }

        User user = userOpt.get();

        // 비밀번호 확인
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }

        return user; // 로그인 성공
    }

    /**
     * 이메일로 사용자 찾기
     * @param email 검색할 이메일
     * @return 해당 이메일의 사용자 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    public Optional<User> findByEmail(String email) throws FileIOException {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        List<User> users = findAll();

        return users.stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    /**
     * 이메일 중복 확인
     * @param email 확인할 이메일
     * @return 이미 존재하면 true
     * @throws FileIOException 파일 읽기 실패 시
     */
    public boolean existsByEmail(String email) throws FileIOException {
        return findByEmail(email).isPresent();
    }

    /**
     * 관리자 계정 목록 조회
     * @return 관리자 사용자 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<User> findAllAdmins() throws FileIOException {
        List<User> users = findAll();
        List<User> admins = new ArrayList<>();

        for (User user : users) {
            if (user.isAdmin()) {
                admins.add(user);
            }
        }

        return admins;
    }

    /**
     * 일반 사용자 계정 목록 조회
     * @return 일반 사용자 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<User> findAllRegularUsers() throws FileIOException {
        List<User> users = findAll();
        List<User> regularUsers = new ArrayList<>();

        for (User user : users) {
            if (!user.isAdmin()) {
                regularUsers.add(user);
            }
        }

        return regularUsers;
    }

    /**
     * 사용자 비밀번호 변경
     * @param userId 사용자 ID
     * @param newPassword 새 비밀번호
     * @return 변경 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     * @throws UserNotFoundException 사용자를 찾을 수 없을 시
     */
    public boolean changePassword(String userId, String newPassword) throws FileIOException, UserNotFoundException {
        Optional<User> userOpt = findById(userId);

        if (!userOpt.isPresent()) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        User user = userOpt.get();
        user.setPassword(newPassword); // 비밀번호 변경

        return update(user); // 변경된 정보 저장
    }

    /**
     * 사용자 이름 변경
     * @param userId 사용자 ID
     * @param newName 새 이름
     * @return 변경 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     * @throws UserNotFoundException 사용자를 찾을 수 없을 시
     */
    public boolean changeName(String userId, String newName) throws FileIOException, UserNotFoundException {
        Optional<User> userOpt = findById(userId);

        if (!userOpt.isPresent()) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        User user = userOpt.get();
        user.setName(newName); // 이름 변경

        return update(user); // 변경된 정보 저장
    }

    // ================= 내부 헬퍼 메서드 =================

    /**
     * 모든 사용자를 파일에 저장
     * @param users 저장할 사용자 목록
     * @throws FileIOException 파일 저장 실패 시
     */
    private void saveAllUsers(List<User> users) throws FileIOException {
        List<String> lines = new ArrayList<>();

        // 각 사용자를 파일 저장 형식의 문자열로 변환
        for (User user : users) {
            lines.add(user.toFileString());
        }

        // 파일에 저장
        FileManager.writeLines(USER_FILE_NAME, lines);
    }

    /**
     * 사용자 데이터 백업
     * @return 백업 성공 여부
     */
    public boolean backupUserData() {
        return FileManager.backupFile(USER_FILE_NAME);
    }

    /**
     * 사용자 통계 정보 조회
     * @return 사용자 통계 문자열
     * @throws FileIOException 파일 읽기 실패 시
     */
    public String getUserStatistics() throws FileIOException {
        List<User> allUsers = findAll();
        List<User> admins = findAllAdmins();
        List<User> regularUsers = findAllRegularUsers();

        StringBuilder stats = new StringBuilder();
        stats.append("=== 사용자 통계 ===\n");
        stats.append("전체 사용자: ").append(allUsers.size()).append("명\n");
        stats.append("관리자: ").append(admins.size()).append("명\n");
        stats.append("일반 사용자: ").append(regularUsers.size()).append("명\n");
        stats.append("==================");

        return stats.toString();
    }
}