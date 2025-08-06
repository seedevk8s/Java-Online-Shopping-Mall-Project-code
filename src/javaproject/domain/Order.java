package javaproject.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 주문 도메인 클래스
 * 사용자의 주문 정보를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class Order implements Serializable {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 주문 ID (고유 식별자)
    private String id;

    // 주문한 사용자 ID
    private String userId;

    // 주문 일시
    private LocalDateTime orderDate;

    // 주문 상태
    private OrderStatus status;

    // 주문 항목 목록
    private List<OrderItem> items;

    // 총 금액
    private double totalAmount;

    // 배송 주소
    private String shippingAddress;

    // 결제 일시
    private LocalDateTime paymentDate;

    // 배송 시작일
    private LocalDateTime shippingDate;

    // 배송 완료일
    private LocalDateTime deliveryDate;

    // 주문 메모 (선택사항)
    private String orderNote;

    // 결제 방법 (선택사항)
    private String paymentMethod;

    /**
     * 기본 생성자
     */
    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    /**
     * 필수 정보를 포함한 생성자
     *
     * @param userId 사용자 ID
     * @param shippingAddress 배송 주소
     */
    public Order(String userId, String shippingAddress) {
        this();
        this.userId = userId;
        this.shippingAddress = shippingAddress;
    }

    /**
     * 전체 정보를 포함한 생성자
     *
     * @param id 주문 ID
     * @param userId 사용자 ID
     * @param orderDate 주문 일시
     * @param status 주문 상태
     * @param totalAmount 총 금액
     * @param shippingAddress 배송 주소
     */
    public Order(String id, String userId, LocalDateTime orderDate,
                 OrderStatus status, double totalAmount, String shippingAddress) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.items = new ArrayList<>();
    }

    /**
     * 주문 항목 추가
     *
     * @param item 추가할 주문 항목
     */
    public void addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
            recalculateTotalAmount();
        }
    }

    /**
     * 주문 항목 제거
     *
     * @param item 제거할 주문 항목
     */
    public void removeItem(OrderItem item) {
        if (item != null) {
            this.items.remove(item);
            recalculateTotalAmount();
        }
    }

    /**
     * 총 금액 재계산
     */
    private void recalculateTotalAmount() {
        this.totalAmount = 0;
        for (OrderItem item : items) {
            this.totalAmount += item.getPrice() * item.getQuantity();
        }
    }

    /**
     * 주문 상태 업데이트
     *
     * @param newStatus 새로운 상태
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;

        // 상태에 따른 날짜 업데이트
        switch (newStatus) {
            case PAID:
                this.paymentDate = LocalDateTime.now();
                break;
            case SHIPPING:
                this.shippingDate = LocalDateTime.now();
                break;
            case DELIVERED:
                this.deliveryDate = LocalDateTime.now();
                break;
        }
    }

    /**
     * 주문 취소 가능 여부 확인
     *
     * @return 취소 가능하면 true
     */
    public boolean isCancellable() {
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }

    /**
     * 주문 완료 여부 확인
     *
     * @return 완료되었으면 true
     */
    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED;
    }

    /**
     * 주문 진행중 여부 확인
     *
     * @return 진행중이면 true
     */
    public boolean isInProgress() {
        return status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED;
    }

    // Getters and Setters

    /**
     * 주문 ID 반환
     * @return 주문 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 주문 ID 설정
     * @param id 주문 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 사용자 ID 반환
     * @return 사용자 ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 사용자 ID 설정
     * @param userId 사용자 ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 주문 일시 반환
     * @return 주문 일시
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * 주문 일시 설정
     * @param orderDate 주문 일시
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * 주문 상태 반환
     * @return 주문 상태
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 주문 상태 설정
     * @param status 주문 상태
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 주문 항목 목록 반환
     * @return 주문 항목 목록
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * 주문 항목 목록 설정
     * @param items 주문 항목 목록
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * 총 금액 반환
     * @return 총 금액
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * 총 금액 설정
     * @param totalAmount 총 금액
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 배송 주소 반환
     * @return 배송 주소
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * 배송 주소 설정
     * @param shippingAddress 배송 주소
     */
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * 결제 일시 반환
     * @return 결제 일시
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * 결제 일시 설정
     * @param paymentDate 결제 일시
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * 배송 시작일 반환
     * @return 배송 시작일
     */
    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    /**
     * 배송 시작일 설정
     * @param shippingDate 배송 시작일
     */
    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    /**
     * 배송 완료일 반환
     * @return 배송 완료일
     */
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * 배송 완료일 설정
     * @param deliveryDate 배송 완료일
     */
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * 주문 메모 반환
     * @return 주문 메모
     */
    public String getOrderNote() {
        return orderNote;
    }

    /**
     * 주문 메모 설정
     * @param orderNote 주문 메모
     */
    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    /**
     * 결제 방법 반환
     * @return 결제 방법
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * 결제 방법 설정
     * @param paymentMethod 결제 방법
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * 주문 정보를 문자열로 변환
     *
     * @return 주문 정보 문자열
     */
    @Override
    public String toString() {
        return String.format(
                "Order{id='%s', userId='%s', orderDate=%s, status=%s, totalAmount=%.2f, itemCount=%d}",
                id, userId, orderDate, status, totalAmount, items.size()
        );
    }

    /**
     * 주문 상세 정보 출력
     *
     * @return 상세 정보 문자열
     */
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 주문 상세 정보 ===\n");
        sb.append("주문 번호: ").append(id).append("\n");
        sb.append("주문자: ").append(userId).append("\n");
        sb.append("주문 일시: ").append(orderDate).append("\n");
        sb.append("주문 상태: ").append(status.getDescription()).append("\n");
        sb.append("배송 주소: ").append(shippingAddress).append("\n");

        if (paymentDate != null) {
            sb.append("결제 일시: ").append(paymentDate).append("\n");
        }
        if (shippingDate != null) {
            sb.append("배송 시작: ").append(shippingDate).append("\n");
        }
        if (deliveryDate != null) {
            sb.append("배송 완료: ").append(deliveryDate).append("\n");
        }

        sb.append("\n--- 주문 항목 ---\n");
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            sb.append(String.format("%d. 상품ID: %s, 수량: %d개, 단가: %,.0f원\n",
                    i + 1, item.getProductId(), item.getQuantity(), item.getPrice()));
        }

        sb.append("\n총 금액: ").append(String.format("%,.0f원", totalAmount));

        return sb.toString();
    }

    /**
     * 객체 동등성 비교
     * ID를 기준으로 비교
     *
     * @param obj 비교할 객체
     * @return 동일한 주문이면 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Order order = (Order) obj;
        return Objects.equals(id, order.id);
    }

    /**
     * 해시코드 생성
     * ID를 기준으로 생성
     *
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}