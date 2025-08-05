package javaproject.exception;

/**
 * 이미 존재하는 사용자 ID로 회원가입을 시도할 때 발생하는 예외
 */
public class DuplicateUserException extends Exception {

    // 중복된 사용자 ID
    private String duplicateUserId;

    /**
     * 기본 생성자
     */
    public DuplicateUserException() {
        super("이미 존재하는 사용자 ID입니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public DuplicateUserException(String message) {
        super(message);
    }

    /**
     * 중복된 사용자 ID를 포함한 생성자
     * @param duplicateUserId 중복된 사용자 ID
     */
    public DuplicateUserException(String duplicateUserId, boolean includeUserId) {
        super("이미 존재하는 사용자 ID입니다: " + duplicateUserId);
        this.duplicateUserId = duplicateUserId;
    }

    // Getter 메서드
    public String getDuplicateUserId() {
        return duplicateUserId;
    }
}
