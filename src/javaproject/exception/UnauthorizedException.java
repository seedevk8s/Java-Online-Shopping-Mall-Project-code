package javaproject.exception;

/**
 * 권한 없음 예외 클래스
 * 권한이 없는 작업을 수행하려 할 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UnauthorizedException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public UnauthorizedException() {
        super("해당 작업을 수행할 권한이 없습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    /**
     * 관리자 권한 필요 메시지 생성
     *
     * @return 관리자 권한 필요 예외
     */
    public static UnauthorizedException adminRequired() {
        return new UnauthorizedException(
                "이 작업은 관리자 권한이 필요합니다."
        );
    }

    /**
     * 로그인 필요 메시지 생성
     *
     * @return 로그인 필요 예외
     */
    public static UnauthorizedException loginRequired() {
        return new UnauthorizedException(
                "로그인이 필요한 서비스입니다. 먼저 로그인해주세요."
        );
    }

    /**
     * 작업과 사용자를 포함한 상세 메시지 생성
     *
     * @param action 수행하려던 작업
     * @param userId 사용자 ID
     * @return 상세 예외 메시지
     */
    public static UnauthorizedException withDetails(String action, String userId) {
        return new UnauthorizedException(
                String.format("사용자 %s는 %s 작업을 수행할 권한이 없습니다.", userId, action)
        );
    }

    /**
     * 리소스 접근 권한 없음 메시지 생성
     *
     * @param resourceType 리소스 유형
     * @param resourceId 리소스 ID
     * @return 리소스 접근 권한 없음 예외
     */
    public static UnauthorizedException resourceAccessDenied(String resourceType, String resourceId) {
        return new UnauthorizedException(
                String.format("%s(ID: %s)에 접근할 권한이 없습니다.", resourceType, resourceId)
        );
    }

    /**
     * 소유자만 접근 가능 메시지 생성
     *
     * @return 소유자 권한 필요 예외
     */
    public static UnauthorizedException ownerOnly() {
        return new UnauthorizedException(
                "이 작업은 소유자만 수행할 수 있습니다."
        );
    }

    /**
     * 세션 만료 메시지 생성
     *
     * @return 세션 만료 예외
     */
    public static UnauthorizedException sessionExpired() {
        return new UnauthorizedException(
                "세션이 만료되었습니다. 다시 로그인해주세요."
        );
    }
}