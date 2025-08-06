package javaproject.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 사용자 도메인 클래스
 * 쇼핑몰의 사용자 정보를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class User implements Serializable {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 사용자 ID (로그인 ID)
    private String id;

    // 비밀번호
    private String password;

    // 이름
    private String name;

    // 이메일
    private String email;

    // 전화번호
    private String phone;

    // 주소
    private String address;

    // 관리자 여부
    private boolean isAdmin;

    // 가입일시
    private LocalDateTime createdDate;

    // 수정일시
    private LocalDateTime modifiedDate;

    // 마지막 로그인 일시
    private LocalDateTime lastLoginDate;

    // 계정 활성화 여부
    private boolean isActive;

    /**
     * 기본 생성자
     */
    public User() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.isActive = true;
        this.isAdmin = false;
    }

    /**
     * 필수 정보를 포함한 생성자
     *
     * @param id 사용자 ID
     * @param password 비밀번호
     * @param name 이름
     * @param email 이메일
     * @param phone 전화번호
     * @param address 주소
     * @param isAdmin 관리자 여부
     */
    public User(String id, String password, String name, String email,
                String phone, String address, boolean isAdmin) {
        this();
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isAdmin = isAdmin;
    }

    /**
     * 일반 사용자 생성자 (관리자 아님)
     *
     * @param id 사용자 ID
     * @param password 비밀번호
     * @param name 이름
     * @param email 이메일
     * @param phone 전화번호
     * @param address 주소
     */
    public User(String id, String password, String name, String email,
                String phone, String address) {
        this(id, password, name, email, phone, address, false);
    }

    /**
     * 비밀번호 일치 여부 확인
     *
     * @param password 확인할 비밀번호
     * @return 일치하면 true
     */
    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    /**
     * 정보 업데이트 시 수정일시 자동 갱신
     */
    private void updateModifiedDate() {
        this.modifiedDate = LocalDateTime.now();
    }

    // Getters and Setters

    /**
     * 사용자 ID 반환
     * @return 사용자 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 사용자 ID 설정
     * @param id 사용자 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 비밀번호 반환
     * @return 비밀번호
     */
    public String getPassword() {
        return password;
    }

    /**
     * 비밀번호 설정
     * @param password 비밀번호
     */
    public void setPassword(String password) {
        this.password = password;
        updateModifiedDate();
    }

    /**
     * 이름 반환
     * @return 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 이름 설정
     * @param name 이름
     */
    public void setName(String name) {
        this.name = name;
        updateModifiedDate();
    }

    /**
     * 이메일 반환
     * @return 이메일
     */
    public String getEmail() {
        return email;
    }

    /**
     * 이메일 설정
     * @param email 이메일
     */
    public void setEmail(String email) {
        this.email = email;
        updateModifiedDate();
    }

    /**
     * 전화번호 반환
     * @return 전화번호
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 전화번호 설정
     * @param phone 전화번호
     */
    public void setPhone(String phone) {
        this.phone = phone;
        updateModifiedDate();
    }

    /**
     * 주소 반환
     * @return 주소
     */
    public String getAddress() {
        return address;
    }

    /**
     * 주소 설정
     * @param address 주소
     */
    public void setAddress(String address) {
        this.address = address;
        updateModifiedDate();
    }

    /**
     * 관리자 여부 반환
     * @return 관리자면 true
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * 관리자 여부 설정
     * @param admin 관리자 여부
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
        updateModifiedDate();
    }

    /**
     * 가입일시 반환
     * @return 가입일시
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * 가입일시 설정
     * @param createdDate 가입일시
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 수정일시 반환
     * @return 수정일시
     */
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    /**
     * 수정일시 설정
     * @param modifiedDate 수정일시
     */
    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * 마지막 로그인 일시 반환
     * @return 마지막 로그인 일시
     */
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * 마지막 로그인 일시 설정
     * @param lastLoginDate 마지막 로그인 일시
     */
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * 계정 활성화 여부 반환
     * @return 활성화되어 있으면 true
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 계정 활성화 여부 설정
     * @param active 활성화 여부
     */
    public void setActive(boolean active) {
        isActive = active;
        updateModifiedDate();
    }

    /**
     * 사용자 정보를 문자열로 변환
     *
     * @return 사용자 정보 문자열
     */
    @Override
    public String toString() {
        return String.format(
                "User{id='%s', name='%s', email='%s', phone='%s', isAdmin=%s, isActive=%s}",
                id, name, email, phone, isAdmin, isActive
        );
    }

    /**
     * 사용자 상세 정보 출력
     *
     * @return 상세 정보 문자열
     */
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 사용자 상세 정보 ===\n");
        sb.append("아이디: ").append(id).append("\n");
        sb.append("이름: ").append(name).append("\n");
        sb.append("이메일: ").append(email).append("\n");
        sb.append("전화번호: ").append(phone).append("\n");
        sb.append("주소: ").append(address).append("\n");
        sb.append("권한: ").append(isAdmin ? "관리자" : "일반사용자").append("\n");
        sb.append("계정상태: ").append(isActive ? "활성" : "비활성").append("\n");
        sb.append("가입일: ").append(createdDate).append("\n");

        if (lastLoginDate != null) {
            sb.append("마지막 로그인: ").append(lastLoginDate).append("\n");
        }

        return sb.toString();
    }

    /**
     * 객체 동등성 비교
     * ID를 기준으로 비교
     *
     * @param obj 비교할 객체
     * @return 동일한 사용자면 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    /**
     * 해시코드 생성
     * ID를 기준으로 생성
     *
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 사용자 복사본 생성
     *
     * @return 복사된 사용자 객체
     */
    public User copy() {
        User copy = new User();
        copy.id = this.id;
        copy.password = this.password;
        copy.name = this.name;
        copy.email = this.email;
        copy.phone = this.phone;
        copy.address = this.address;
        copy.isAdmin = this.isAdmin;
        copy.createdDate = this.createdDate;
        copy.modifiedDate = this.modifiedDate;
        copy.lastLoginDate = this.lastLoginDate;
        copy.isActive = this.isActive;

        return copy;
    }

    /**
     * 사용자 유효성 검증
     *
     * @return 유효한 사용자면 true
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty();
    }

    /**
     * 마스킹된 정보 반환 (보안용)
     *
     * @return 마스킹된 정보 문자열
     */
    public String toMaskedString() {
        return String.format(
                "User{id='%s', name='%s', email='%s', phone='%s'}",
                id,
                maskName(name),
                maskEmail(email),
                maskPhone(phone)
        );
    }

    /**
     * 이름 마스킹
     */
    private String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }

    /**
     * 이메일 마스킹
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) return email;
        return parts[0].substring(0, 2) + "***@" + parts[1];
    }

    /**
     * 전화번호 마스킹
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) return phone;
        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }
}