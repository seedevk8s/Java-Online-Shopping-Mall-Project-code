// =================================================================
// AuthenticationException.java - 인증 실패 예외 클래스
// =================================================================
package javaproject.exception;

/**
 * 인증 실패 시 발생하는 예외 클래스
 * 로그인 실패, 권한 부족 등의 경우에 사용
 */
public class AuthenticationException extends Exception {

    // 사용자 ID
    private String userId;

    // 에러 코드
    private String errorCode;

    /**
     * 기본 생성자
     * @param message 예외 메시지
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * 사용자 ID와 에러 코드를 포함한 생성자
     * @param userId 사용자 ID
     * @param errorCode 에러 코드
     * @param message 예외 메시지
     */
    public AuthenticationException(String userId, String errorCode, String message) {
        super(message);
        this.userId = userId;
        this.errorCode = errorCode;
    }

    /**
     * 원인 예외를 포함한 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 전체 정보를 포함한 생성자
     * @param userId 사용자 ID
     * @param errorCode 에러 코드
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public AuthenticationException(String userId, String errorCode,
                                   String message, Throwable cause) {
        super(message, cause);
        this.userId = userId;
        this.errorCode = errorCode;
    }

    // Getter 메서드들

    /**
     * 사용자 ID 반환
     * @return 사용자 ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 에러 코드 반환
     * @return 에러 코드
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 에러 코드별 설명 반환
     * @return 에러 설명
     */
    public String getErrorDescription() {
        if (errorCode == null) {
            return "알 수 없는 인증 오류";
        }

        switch (errorCode) {
            case "WRONG_PASSWORD":
                return "비밀번호가 일치하지 않습니다";
            case "USER_NOT_FOUND":
                return "사용자를 찾을 수 없습니다";
            case "NOT_LOGGED_IN":
                return "로그인이 필요합니다";
            case "NOT_ADMIN":
                return "관리자 권한이 필요합니다";
            case "ACCESS_DENIED":
                return "접근 권한이 없습니다";
            case "ACCOUNT_LOCKED":
                return "계정이 잠겨있습니다";
            case "SESSION_EXPIRED":
                return "세션이 만료되었습니다";
            default:
                return "인증 오류: " + errorCode;
        }
    }

    /**
     * 상세 정보 문자열 반환
     * @return 상세 정보
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AuthenticationException: ");

        if (userId != null) {
            sb.append("[사용자: ").append(userId).append("] ");
        }

        if (errorCode != null) {
            sb.append("[코드: ").append(errorCode).append("] ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}