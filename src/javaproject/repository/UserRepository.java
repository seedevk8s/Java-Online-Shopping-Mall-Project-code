package javaproject.repository;

import javaproject.domain.User;
import java.util.*;

/**
 * 사용자 저장소 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 메모리에 사용자 데이터를 저장하고 관리
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UserRepository {

    // 싱글톤 인스턴스
    private static final UserRepository instance = new UserRepository();

    // 사용자 데이터 저장소 (ID를 키로 사용)
    private final Map<String, User> users;

    // 이메일 인덱스 (이메일로 빠른 검색을 위함)
    private final Map<String, User> emailIndex;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private UserRepository() {
        this.users = new HashMap<>();
        this.emailIndex = new HashMap<>();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return UserRepository의 유일한 인스턴스
     */
    public static UserRepository getInstance() {
        return instance;
    }

    /**
     * 사용자 저장
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    public User save(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("사용자 정보가 유효하지 않습니다.");
        }

        // 기존 사용자가 있으면 이메일 인덱스에서 제거
        User existing = users.get(user.getId());
        if (existing != null && existing.getEmail() != null) {
            emailIndex.remove(existing.getEmail());
        }

        // 사용자 저장
        users.put(user.getId(), user);

        // 이메일 인덱스 업데이트
        if (user.getEmail() != null) {
            emailIndex.put(user.getEmail(), user);
        }

        return user;
    }

    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 조회된 사용자, 없으면 null
     */
    public User findById(String id) {
        if (id == null) {
            return null;
        }
        return users.get(id);
    }

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 조회된 사용자, 없으면 null
     */
    public User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return emailIndex.get(email);
    }

    /**
     * 모든 사용자 조회
     *
     * @return 전체 사용자 목록
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * 사용자 수정
     *
     * @param user 수정할 사용자
     * @return 수정된 사용자
     */
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("사용자 정보가 유효하지 않습니다.");
        }

        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + user.getId());
        }

        return save(user);
    }

    /**
     * 사용자 삭제
     *
     * @param id 삭제할 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(String id) {
        if (id == null) {
            return false;
        }

        User removed = users.remove(id);
        if (removed != null) {
            // 이메일 인덱스에서도 제거
            if (removed.getEmail() != null) {
                emailIndex.remove(removed.getEmail());
            }
            return true;
        }

        return false;
    }

    /**
     * 사용자 ID 존재 여부 확인
     *
     * @param id 확인할 ID
     * @return 존재하면 true
     */
    public boolean existsById(String id) {
        return id != null && users.containsKey(id);
    }

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 확인할 이메일
     * @return 존재하면 true
     */
    public boolean existsByEmail(String email) {
        return email != null && emailIndex.containsKey(email);
    }

    /**
     * 사용자 수 반환
     *
     * @return 전체 사용자 수
     */
    public int count() {
        return users.size();
    }

    /**
     * 모든 데이터 삭제
     */
    public void deleteAll() {
        users.clear();
        emailIndex.clear();
    }

    /**
     * 관리자 사용자 조회
     *
     * @return 관리자 목록
     */
    public List<User> findAdmins() {
        List<User> admins = new ArrayList<>();
        for (User user : users.values()) {
            if (user.isAdmin()) {
                admins.add(user);
            }
        }
        return admins;
    }

    /**
     * 일반 사용자 조회
     *
     * @return 일반 사용자 목록
     */
    public List<User> findRegularUsers() {
        List<User> regularUsers = new ArrayList<>();
        for (User user : users.values()) {
            if (!user.isAdmin()) {
                regularUsers.add(user);
            }
        }
        return regularUsers;
    }

    /**
     * 이름으로 사용자 검색
     *
     * @param name 검색할 이름
     * @return 검색된 사용자 목록
     */
    public List<User> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<User> result = new ArrayList<>();
        String searchName = name.toLowerCase().trim();

        for (User user : users.values()) {
            if (user.getName() != null &&
                    user.getName().toLowerCase().contains(searchName)) {
                result.add(user);
            }
        }

        return result;
    }

    /**
     * 전화번호로 사용자 조회
     *
     * @param phone 전화번호
     * @return 조회된 사용자, 없으면 null
     */
    public User findByPhone(String phone) {
        if (phone == null) {
            return null;
        }

        for (User user : users.values()) {
            if (phone.equals(user.getPhone())) {
                return user;
            }
        }

        return null;
    }

    /**
     * 주소에 특정 문자열이 포함된 사용자 검색
     *
     * @param keyword 검색 키워드
     * @return 검색된 사용자 목록
     */
    public List<User> findByAddressContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<User> result = new ArrayList<>();
        String searchKeyword = keyword.toLowerCase().trim();

        for (User user : users.values()) {
            if (user.getAddress() != null &&
                    user.getAddress().toLowerCase().contains(searchKeyword)) {
                result.add(user);
            }
        }

        return result;
    }

    /**
     * 저장소 상태 정보 반환
     *
     * @return 상태 정보 문자열
     */
    public String getStatistics() {
        int totalUsers = users.size();
        int adminCount = findAdmins().size();
        int regularCount = totalUsers - adminCount;

        return String.format(
                "=== 사용자 저장소 통계 ===\n" +
                        "전체 사용자: %d명\n" +
                        "관리자: %d명\n" +
                        "일반 사용자: %d명",
                totalUsers, adminCount, regularCount
        );
    }

    /**
     * 페이징 처리된 사용자 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 사용자 목록
     */
    public List<User> findAllPaged(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("유효하지 않은 페이징 파라미터입니다.");
        }

        List<User> allUsers = findAll();
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());

        if (start >= allUsers.size()) {
            return new ArrayList<>();
        }

        return allUsers.subList(start, end);
    }
}