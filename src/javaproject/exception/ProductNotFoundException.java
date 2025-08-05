package javaproject.exception;

/**
 * 상품을 찾을 수 없을 때 발생하는 예외
 * 상품 조회, 장바구니 담기 시 사용
 */
public class ProductNotFoundException extends Exception {

    /**
     * 기본 생성자
     */
    public ProductNotFoundException() {
        super("상품을 찾을 수 없습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * 상품 ID를 포함한 예외 메시지 생성자
     * @param productId 찾을 수 없는 상품 ID
     */
    public ProductNotFoundException(String productId, boolean includeProductId) {
        super("상품을 찾을 수 없습니다. 상품 ID: " + productId);
    }
}
