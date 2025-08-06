// =================================================================
// UserNotFoundException.java - 사용자 없음 예외 클래스
// =================================================================
package javaproject.exception;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외 클래스
 * 사용자 조회, 로그인 등에서 사용
 */
public class UserNotFoundException extends Exception {

    // 찾을 수 없는 사용자 ID
    private String userId;

    // ID로 검색했는지 여부
    private boolean searchedById;

    /**
     * 기본 생성자
     * @param message 예외 메시지
     */
    public UserNotFoundException(String message) {
        super(message);
        this.searchedById = false;
    }

    /**
     * 사용자 ID를 포함한 생성자
     * @param userId 찾을 수 없는 사용자 ID
     * @param searchedById ID로 검색했는지 여부
     */
    public UserNotFoundException(String userId, boolean searchedById) {
        super(searchedById ?
                "사용자를 찾을 수 없습니다. ID: " + userId :
                "해당 조건의 사용자를 찾을 수 없습니다: " + userId);
        this.userId = userId;
        this.searchedById = searchedById;
    }

    /**
     * 원인 예외를 포함한 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.searchedById = false;
    }

    /**
     * 사용자 ID 반환
     * @return 사용자 ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ID로 검색했는지 여부 반환
     * @return ID 검색 여부
     */
    public boolean isSearchedById() {
        return searchedById;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserNotFoundException: ");

        if (userId != null) {
            sb.append("[사용자 ID: ").append(userId).append("] ");
        }

        if (searchedById) {
            sb.append("[ID 검색] ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}