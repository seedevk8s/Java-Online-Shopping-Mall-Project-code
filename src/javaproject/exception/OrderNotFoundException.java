// =================================================================
// OrderNotFoundException.java - 주문을 찾을 수 없을 때 발생하는 예외
// =================================================================
package javaproject.exception;

/**
 * 주문을 찾을 수 없을 때 발생하는 예외 클래스
 * 주문 ID로 조회 시 해당 주문이 존재하지 않는 경우
 */
public class OrderNotFoundException extends Exception {

    // 찾을 수 없는 주문 ID
    private String orderId;

    // ID로 검색했는지 여부
    private boolean searchedById;

    /**
     * 기본 생성자
     * @param message 예외 메시지
     */
    public OrderNotFoundException(String message) {
        super(message);
        this.searchedById = false;
    }

    /**
     * 주문 ID를 포함한 생성자
     * @param orderId 찾을 수 없는 주문 ID
     * @param searchedById ID로 검색했는지 여부
     */
    public OrderNotFoundException(String orderId, boolean searchedById) {
        super(createMessage(orderId, searchedById));
        this.orderId = orderId;
        this.searchedById = searchedById;
    }

    /**
     * 원인 예외를 포함한 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.searchedById = false;
    }

    /**
     * 전체 정보를 포함한 생성자
     * @param orderId 찾을 수 없는 주문 ID
     * @param searchedById ID로 검색했는지 여부
     * @param cause 원인 예외
     */
    public OrderNotFoundException(String orderId, boolean searchedById, Throwable cause) {
        super(createMessage(orderId, searchedById), cause);
        this.orderId = orderId;
        this.searchedById = searchedById;
    }

    /**
     * 예외 메시지 생성
     * @param orderId 주문 ID
     * @param searchedById ID로 검색했는지 여부
     * @return 생성된 메시지
     */
    private static String createMessage(String orderId, boolean searchedById) {
        if (searchedById && orderId != null) {
            return "주문을 찾을 수 없습니다. 주문 ID: " + orderId;
        } else if (orderId != null) {
            return "해당 조건의 주문을 찾을 수 없습니다: " + orderId;
        } else {
            return "주문을 찾을 수 없습니다.";
        }
    }

    // Getter 메서드들

    /**
     * 찾을 수 없는 주문 ID 반환
     * @return 주문 ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * ID로 검색했는지 여부 반환
     * @return ID 검색 여부
     */
    public boolean isSearchedById() {
        return searchedById;
    }

    /**
     * 상세 정보 문자열 반환
     * @return 상세 정보
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OrderNotFoundException: ");

        if (orderId != null) {
            sb.append("[주문 ID: ").append(orderId).append("] ");
        }

        if (searchedById) {
            sb.append("[ID 검색] ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}