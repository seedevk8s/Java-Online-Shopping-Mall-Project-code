package javaproject.domain;

/**
 * 주문 상태를 나타내는 열거형
 * 주문의 생명주기를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public enum OrderStatus {

    /**
     * 주문 대기
     * 주문이 생성되었지만 아직 결제되지 않은 상태
     */
    PENDING("주문 대기", "주문이 접수되었습니다."),

    /**
     * 결제 완료
     * 결제가 완료되어 배송 준비 중인 상태
     */
    PAID("결제 완료", "결제가 완료되었습니다."),

    /**
     * 배송 중
     * 상품이 배송 중인 상태
     */
    SHIPPING("배송 중", "상품이 배송 중입니다."),

    /**
     * 배송 완료
     * 상품이 고객에게 전달된 상태
     */
    DELIVERED("배송 완료", "배송이 완료되었습니다."),

    /**
     * 주문 취소
     * 고객 또는 관리자에 의해 주문이 취소된 상태
     */
    CANCELLED("주문 취소", "주문이 취소되었습니다."),

    /**
     * 반품 요청
     * 고객이 반품을 요청한 상태 (선택적 기능)
     */
    RETURN_REQUESTED("반품 요청", "반품이 요청되었습니다."),

    /**
     * 반품 완료
     * 반품이 완료된 상태 (선택적 기능)
     */
    RETURNED("반품 완료", "반품이 완료되었습니다."),

    /**
     * 환불 처리중
     * 환불이 진행 중인 상태 (선택적 기능)
     */
    REFUND_PENDING("환불 처리중", "환불이 처리 중입니다."),

    /**
     * 환불 완료
     * 환불이 완료된 상태 (선택적 기능)
     */
    REFUNDED("환불 완료", "환불이 완료되었습니다.");

    // 상태명 (한글)
    private final String description;

    // 상태 메시지
    private final String message;

    /**
     * 생성자
     *
     * @param description 상태 설명
     * @param message 상태 메시지
     */
    OrderStatus(String description, String message) {
        this.description = description;
        this.message = message;
    }

    /**
     * 상태 설명 반환
     *
     * @return 상태 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 상태 메시지 반환
     *
     * @return 상태 메시지
     */
    public String getMessage() {
        return message;
    }

    /**
     * 다음 가능한 상태 목록 반환
     * 현재 상태에서 전환 가능한 상태들을 반환
     *
     * @return 전환 가능한 상태 배열
     */
    public OrderStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new OrderStatus[]{PAID, CANCELLED};
            case PAID:
                return new OrderStatus[]{SHIPPING, CANCELLED};
            case SHIPPING:
                return new OrderStatus[]{DELIVERED, RETURN_REQUESTED};
            case DELIVERED:
                return new OrderStatus[]{RETURN_REQUESTED};
            case CANCELLED:
                return new OrderStatus[]{}; // 최종 상태
            case RETURN_REQUESTED:
                return new OrderStatus[]{RETURNED, DELIVERED};
            case RETURNED:
                return new OrderStatus[]{REFUND_PENDING};
            case REFUND_PENDING:
                return new OrderStatus[]{REFUNDED};
            case REFUNDED:
                return new OrderStatus[]{}; // 최종 상태
            default:
                return new OrderStatus[]{};
        }
    }

    /**
     * 특정 상태로 전환 가능한지 확인
     *
     * @param targetStatus 목표 상태
     * @return 전환 가능하면 true
     */
    public boolean canTransitionTo(OrderStatus targetStatus) {
        OrderStatus[] possibleStatuses = getNextPossibleStatuses();
        for (OrderStatus status : possibleStatuses) {
            if (status == targetStatus) {
                return true;
            }
        }
        return false;
    }

    /**
     * 최종 상태인지 확인
     * 더 이상 상태 변경이 불가능한 상태
     *
     * @return 최종 상태면 true
     */
    public boolean isFinalStatus() {
        return this == CANCELLED || this == REFUNDED || this == DELIVERED;
    }

    /**
     * 취소 가능한 상태인지 확인
     *
     * @return 취소 가능하면 true
     */
    public boolean isCancellable() {
        return this == PENDING || this == PAID;
    }

    /**
     * 반품 가능한 상태인지 확인
     *
     * @return 반품 가능하면 true
     */
    public boolean isReturnable() {
        return this == DELIVERED;
    }

    /**
     * 진행 중인 상태인지 확인
     *
     * @return 진행 중이면 true
     */
    public boolean isInProgress() {
        return this == PENDING || this == PAID || this == SHIPPING;
    }

    /**
     * 완료된 상태인지 확인
     *
     * @return 완료되었으면 true
     */
    public boolean isCompleted() {
        return this == DELIVERED || this == REFUNDED;
    }

    /**
     * 상태 코드 반환 (API 연동용)
     *
     * @return 상태 코드
     */
    public int getStatusCode() {
        switch (this) {
            case PENDING: return 100;
            case PAID: return 200;
            case SHIPPING: return 300;
            case DELIVERED: return 400;
            case CANCELLED: return 500;
            case RETURN_REQUESTED: return 600;
            case RETURNED: return 610;
            case REFUND_PENDING: return 700;
            case REFUNDED: return 710;
            default: return 0;
        }
    }

    /**
     * 상태 코드로 OrderStatus 찾기
     *
     * @param code 상태 코드
     * @return 해당하는 OrderStatus, 없으면 null
     */
    public static OrderStatus fromStatusCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getStatusCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 문자열로 OrderStatus 찾기
     *
     * @param statusString 상태 문자열
     * @return 해당하는 OrderStatus, 없으면 null
     */
    public static OrderStatus fromString(String statusString) {
        if (statusString == null) {
            return null;
        }

        try {
            return OrderStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한글 설명으로도 찾기 시도
            for (OrderStatus status : OrderStatus.values()) {
                if (status.getDescription().equals(statusString)) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * 상태 정보를 문자열로 변환
     *
     * @return 상태 정보 문자열
     */
    @Override
    public String toString() {
        return String.format("%s(%s)", name(), description);
    }
}