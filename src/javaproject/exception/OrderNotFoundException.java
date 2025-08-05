package javaproject.exception;

/**
 * 주문을 찾을 수 없을 때 발생하는 예외
 * 주문 조회, 주문 취소 시 사용
 */
public class OrderNotFoundException extends Exception {

    // 찾을 수 없는 주문 ID
    private String orderId;

    /**
     * 기본 생성자
     */
    public OrderNotFoundException() {
        super("주문을 찾을 수 없습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public OrderNotFoundException(String message) {
        super(message);
    }

    /**
     * 주문 ID를 포함한 생성자
     * @param orderId 찾을 수 없는 주문 ID
     */
    public OrderNotFoundException(String orderId, boolean includeOrderId) {
        super("주문을 찾을 수 없습니다. 주문 ID: " + orderId);
        this.orderId = orderId;
    }

    // Getter 메서드
    public String getOrderId() {
        return orderId;
    }
}
