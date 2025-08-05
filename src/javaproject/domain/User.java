package javaproject.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 사용자 정보를 담는 도메인 클래스
 * 회원가입, 로그인 등에서 사용되는 핵심 사용자 데이터
 */
public class User {
    // 사용자 고유 아이디 (중복 불가)
    private String userId;

    // 사용자 비밀번호 (실제로는 암호화 필요하지만 여기서는 평문)
    private String password;

    // 사용자 이름
    private String name;

    // 사용자 이메일
    private String email;

    // 관리자 여부 (true: 관리자, false: 일반회원)
    private boolean isAdmin;

    // 회원가입 날짜
    private LocalDateTime createdAt;

    /**
     * 기본 생성자 (파일에서 데이터 읽을 때 사용)
     */
    public User() {
    }

    /**
     * 일반 회원 생성자
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param name 이름
     * @param email 이메일
     */
    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.isAdmin = false; // 기본값은 일반회원
        this.createdAt = LocalDateTime.now(); // 현재 시간으로 설정
    }

    /**
     * 관리자 포함 전체 정보 생성자
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param name 이름
     * @param email 이메일
     * @param isAdmin 관리자 여부
     */
    public User(String userId, String password, String name, String email, boolean isAdmin) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
        this.createdAt = LocalDateTime.now();
    }

    // Getter 메서드들
    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter 메서드들 (필요한 경우에만)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 파일 저장용 문자열 변환
     * 각 필드를 구분자(|)로 분리하여 한 줄로 변환
     * @return 파일 저장 형식의 문자열
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return userId + "|" + password + "|" + name + "|" + email + "|" +
                isAdmin + "|" + createdAt.format(formatter);
    }

    /**
     * 파일에서 읽은 문자열로부터 User 객체 생성
     * @param fileString 파일에서 읽은 한 줄 문자열
     * @return User 객체
     */
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split("\\|"); // | 문자로 분리
        User user = new User();

        user.setUserId(parts[0]);
        user.setPassword(parts[1]);
        user.setName(parts[2]);
        user.setEmail(parts[3]);
        user.setAdmin(Boolean.parseBoolean(parts[4])); // "true"/"false" -> boolean 변환

        // 날짜 문자열을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        user.setCreatedAt(LocalDateTime.parse(parts[5], formatter));

        return user;
    }

    /**
     * 객체 정보를 보기 좋게 출력하기 위한 메서드
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * 두 User 객체가 같은지 비교 (userId 기준)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // 같은 객체 참조
        if (obj == null || getClass() != obj.getClass()) return false; // null이거나 다른 클래스

        User user = (User) obj;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    /**
     * hashCode 메서드 (equals와 함께 오버라이드)
     */
    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
