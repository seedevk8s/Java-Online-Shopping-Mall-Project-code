package javaproject.util;

import javaproject.domain.User;

/**
 * 세션 관리 클래스
 * 싱글톤 패턴을 적용하여 전역적으로 하나의 인스턴스만 존재
 * 현재 로그인한 사용자 정보를 관리
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class SessionManager {

    // 싱글톤 인스턴스 - 클래스 로딩 시점에 생성 (thread-safe)
    private static final SessionManager instance = new SessionManager();

    // 현재 로그인한 사용자 정보
    private User currentUser;

    // 로그인 시간 (밀리초)
    private long loginTime;

    // 마지막 활동 시간 (세션 타임아웃 체크용)
    private long lastActivityTime;

    // 세션 타임아웃 시간 (30분 = 1800000 밀리초)
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     * 외부에서 인스턴스를 생성할 수 없도록 제한
     */
    private SessionManager() {
        this.currentUser = null;
        this.loginTime = 0;
        this.lastActivityTime = 0;
    }

    /**
     * 싱글톤 인스턴스를 반환하는 정적 메서드
     * @return SessionManager의 유일한 인스턴스
     */
    public static SessionManager getInstance() {
        return instance;
    }

    /**
     * 사용자 로그인 처리
     * @param user 로그인할 사용자 객체
     */
    public void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 null입니다.");
        }

        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
        this.lastActivityTime = this.loginTime;

        System.out.println("✅ 세션 생성: " + user.getName() + "님 로그인");
    }

    /**
     * 사용자 로그아웃 처리
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("✅ 세션 종료: " + currentUser.getName() + "님 로그아웃");

            // 세션 정보 초기화
            this.currentUser = null;
            this.loginTime = 0;
            this.lastActivityTime = 0;
        }
    }

    /**
     * 현재 로그인한 사용자 반환
     * @return 현재 로그인한 사용자 객체 (로그인하지 않은 경우 null)
     */
    public User getCurrentUser() {
        // 세션 타임아웃 체크
        if (isSessionExpired()) {
            logout();
            return null;
        }

        // 활동 시간 업데이트
        if (currentUser != null) {
            updateActivityTime();
        }

        return currentUser;
    }

    /**
     * 로그인 여부 확인
     * @return 로그인 상태면 true, 아니면 false
     */
    public boolean isLoggedIn() {
        // 세션 타임아웃 체크
        if (isSessionExpired()) {
            logout();
            return false;
        }

        return currentUser != null;
    }

    /**
     * 관리자 권한 확인
     * @return 관리자면 true, 아니면 false
     */
    public boolean isAdmin() {
        return isLoggedIn() && currentUser.isAdmin();
    }

    /**
     * 현재 사용자의 ID 반환
     * @return 사용자 ID (로그인하지 않은 경우 null)
     */
    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * 현재 사용자의 이름 반환
     * @return 사용자 이름 (로그인하지 않은 경우 null)
     */
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : null;
    }

    /**
     * 세션 타임아웃 여부 확인
     * @return 타임아웃되었으면 true, 아니면 false
     */
    private boolean isSessionExpired() {
        if (currentUser == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long inactiveTime = currentTime - lastActivityTime;

        // 30분 이상 비활성 상태면 세션 만료
        if (inactiveTime > SESSION_TIMEOUT) {
            System.out.println("⚠️ 세션이 만료되었습니다. 다시 로그인해주세요.");
            return true;
        }

        return false;
    }

    /**
     * 마지막 활동 시간 업데이트
     */
    private void updateActivityTime() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    /**
     * 로그인 시간 반환
     * @return 로그인 시간 (밀리초)
     */
    public long getLoginTime() {
        return loginTime;
    }

    /**
     * 세션 지속 시간 반환 (분 단위)
     * @return 세션 지속 시간 (분)
     */
    public long getSessionDurationMinutes() {
        if (!isLoggedIn()) {
            return 0;
        }

        long duration = System.currentTimeMillis() - loginTime;
        return duration / (60 * 1000);
    }

    /**
     * 세션 정보 문자열로 반환
     * @return 세션 정보 문자열
     */
    @Override
    public String toString() {
        if (!isLoggedIn()) {
            return "SessionManager [로그인되지 않음]";
        }

        return String.format(
                "SessionManager [사용자=%s, 로그인시간=%d분전, 권한=%s]",
                currentUser.getName(),
                getSessionDurationMinutes(),
                currentUser.isAdmin() ? "관리자" : "일반사용자"
        );
    }

    /**
     * 세션 정보 초기화 (테스트용)
     * 주의: 실제 운영 환경에서는 사용하지 말 것
     */
    public void reset() {
        this.currentUser = null;
        this.loginTime = 0;
        this.lastActivityTime = 0;
    }
}