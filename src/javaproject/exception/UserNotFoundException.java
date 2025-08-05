package javaproject.exception;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 * 로그인, 사용자 정보 조회 시 사용
 */
public class UserNotFoundException extends Exception {

    /**
     * 기본 생성자
     */
    public UserNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * 사용자 ID를 포함한 예외 메시지 생성자
     * @param userId 찾을 수 없는 사용자 ID
     */
    public UserNotFoundException(String userId, boolean includeUserId) {
        super("사용자를 찾을 수 없습니다. ID: " + userId);
    }
}

