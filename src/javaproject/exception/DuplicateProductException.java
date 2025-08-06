package javaproject.exception;

/**
 * 중복된 상품 예외 클래스
 * 이미 존재하는 상품 ID로 새 상품을 등록하려 할 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class DuplicateProductException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public DuplicateProductException() {
        super("이미 존재하는 상품입니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public DuplicateProductException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public DuplicateProductException(Throwable cause) {
        super(cause);
    }

    /**
     * 상품 ID를 포함한 상세 메시지 생성
     *
     * @param productId 중복된 상품 ID
     * @return 상세 예외 메시지
     */
    public static DuplicateProductException withProductId(String productId) {
        return new DuplicateProductException(
                String.format("이미 존재하는 상품 ID입니다: %s", productId)
        );
    }

    /**
     * 상품명을 포함한 상세 메시지 생성
     *
     * @param productName 중복된 상품명
     * @return 상세 예외 메시지
     */
    public static DuplicateProductException withProductName(String productName) {
        return new DuplicateProductException(
                String.format("동일한 이름의 상품이 이미 존재합니다: %s", productName)
        );
    }
}