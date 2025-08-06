package javaproject.exception;

/**
 * 잘못된 비밀번호 예외 클래스
 * 로그인 시 비밀번호가 일치하지 않을 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class InvalidPasswordException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public InvalidPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }

    /**
     * 로그인 실패 횟수를 포함한 상세 메시지 생성
     *
     * @param attempts 시도 횟수
     * @return 상세 예외 메시지
     */
    public static InvalidPasswordException withAttempts(int attempts) {
        return new InvalidPasswordException(
                String.format("비밀번호가 일치하지 않습니다. (시도 횟수: %d회)", attempts)
        );
    }

    /**
     * 계정 잠금 경고를 포함한 메시지 생성
     *
     * @param remainingAttempts 남은 시도 횟수
     * @return 경고 메시지를 포함한 예외
     */
    public static InvalidPasswordException withWarning(int remainingAttempts) {
        if (remainingAttempts <= 0) {
            return new InvalidPasswordException(
                    "비밀번호가 일치하지 않습니다. 계정이 잠겼습니다."
            );
        }
        return new InvalidPasswordException(
                String.format("비밀번호가 일치하지 않습니다. (남은 시도: %d회)", remainingAttempts)
        );
    }
}