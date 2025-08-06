package javaproject.exception;

/**
 * 중복된 사용자 예외 클래스
 * 이미 존재하는 사용자 ID로 가입하려 할 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class DuplicateUserException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public DuplicateUserException() {
        super("이미 존재하는 사용자입니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public DuplicateUserException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public DuplicateUserException(Throwable cause) {
        super(cause);
    }

    /**
     * 사용자 ID를 포함한 상세 메시지 생성
     *
     * @param userId 중복된 사용자 ID
     * @return 상세 예외 메시지
     */
    public static DuplicateUserException withUserId(String userId) {
        return new DuplicateUserException(
                String.format("이미 사용 중인 아이디입니다: %s", userId)
        );
    }

    /**
     * 이메일을 포함한 상세 메시지 생성
     *
     * @param email 중복된 이메일
     * @return 상세 예외 메시지
     */
    public static DuplicateUserException withEmail(String email) {
        return new DuplicateUserException(
                String.format("이미 등록된 이메일입니다: %s", email)
        );
    }

    /**
     * 전화번호를 포함한 상세 메시지 생성
     *
     * @param phone 중복된 전화번호
     * @return 상세 예외 메시지
     */
    public static DuplicateUserException withPhone(String phone) {
        return new DuplicateUserException(
                String.format("이미 등록된 전화번호입니다: %s", phone)
        );
    }
}