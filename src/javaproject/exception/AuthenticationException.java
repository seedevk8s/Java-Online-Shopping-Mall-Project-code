package javaproject.exception;

/**
 * 로그인 인증이 실패했을 때 발생하는 예외
 * 비밀번호 불일치, 계정 잠금 등에 사용
 */
public class AuthenticationException extends Exception {

    // 인증 실패한 사용자 ID
    private String userId;

    // 실패 원인 코드
    private String failureCode;

    /**
     * 기본 생성자
     */
    public AuthenticationException() {
        super("인증에 실패했습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * 상세 정보를 포함한 생성자
     * @param userId 인증 실패한 사용자 ID
     * @param failureCode 실패 원인 코드
     * @param message 예외 메시지
     */
    public AuthenticationException(String userId, String failureCode, String message) {
        super(message);
        this.userId = userId;
        this.failureCode = failureCode;
    }

    // Getter 메서드들
    public String getUserId() {
        return userId;
    }

    public String getFailureCode() {
        return failureCode;
    }
}

