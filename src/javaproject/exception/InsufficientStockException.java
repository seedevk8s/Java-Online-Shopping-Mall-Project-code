package javaproject.exception;

/**
 * 상품의 재고가 부족할 때 발생하는 예외
 * 장바구니 담기, 주문 시 사용
 */
public class InsufficientStockException extends Exception {

    // 요청한 수량
    private int requestedQuantity;

    // 현재 재고
    private int availableStock;

    // 상품 ID
    private String productId;

    /**
     * 기본 생성자
     */
    public InsufficientStockException() {
        super("재고가 부족합니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public InsufficientStockException(String message) {
        super(message);
    }

    /**
     * 상세 정보를 포함한 생성자
     * @param productId 상품 ID
     * @param requestedQuantity 요청한 수량
     * @param availableStock 현재 재고
     */
    public InsufficientStockException(String productId, int requestedQuantity, int availableStock) {
        super(String.format("재고가 부족합니다. 상품 ID: %s, 요청 수량: %d, 현재 재고: %d",
                productId, requestedQuantity, availableStock));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    // Getter 메서드들
    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public String getProductId() {
        return productId;
    }

    /**
     * 부족한 수량 계산
     * @return 부족한 수량
     */
    public int getShortage() {
        return requestedQuantity - availableStock;
    }
}

