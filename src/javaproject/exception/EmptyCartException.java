package javaproject.exception;

/**
 * 빈 장바구니 예외 클래스
 * 장바구니가 비어있는 상태에서 주문하려 할 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class EmptyCartException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public EmptyCartException() {
        super("장바구니가 비어있습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public EmptyCartException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public EmptyCartException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public EmptyCartException(Throwable cause) {
        super(cause);
    }

    /**
     * 사용자 ID를 포함한 상세 메시지 생성
     *
     * @param userId 사용자 ID
     * @return 상세 예외 메시지
     */
    public static EmptyCartException withUserId(String userId) {
        return new EmptyCartException(
                String.format("사용자 %s의 장바구니가 비어있습니다.", userId)
        );
    }

    /**
     * 추천 메시지를 포함한 예외 생성
     *
     * @return 추천 메시지가 포함된 예외
     */
    public static EmptyCartException withSuggestion() {
        return new EmptyCartException(
                "장바구니가 비어있습니다. 먼저 상품을 장바구니에 담아주세요."
        );
    }
}