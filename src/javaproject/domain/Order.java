package javaproject.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보를 담는 도메인 클래스
 * 사용자의 주문 내역과 상태를 관리
 */
public class Order {
    // 주문 고유 ID
    private String orderId;

    // 주문한 사용자 ID
    private String userId;

    // 주문 상품 목록
    private List<OrderItem> orderItems;

    // 총 주문 금액
    private int totalAmount;

    // 배송 주소
    private String deliveryAddress;

    // 연락처
    private String phoneNumber;

    // 주문 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
    private OrderStatus status;

    // 주문 일시
    private LocalDateTime orderDate;

    // 주문 ID 자동 생성을 위한 카운터
    private static int orderCounter = 1;

    /**
     * 주문 상태를 나타내는 열거형
     */
    public enum OrderStatus {
        PENDING("주문접수"),      // 주문 접수됨
        CONFIRMED("주문확인"),    // 주문 확인됨
        SHIPPED("배송중"),       // 배송 시작
        DELIVERED("배송완료"),   // 배송 완료
        CANCELLED("주문취소");   // 주문 취소됨

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 기본 생성자
     */
    public Order() {
        this.orderItems = new ArrayList<>();
    }

    /**
     * 새 주문 생성용 생성자
     * @param userId 주문 사용자 ID
     * @param deliveryAddress 배송 주소
     * @param phoneNumber 연락처
     */
    public Order(String userId, String deliveryAddress, String phoneNumber) {
        this.orderId = generateOrderId();
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
        this.orderItems = new ArrayList<>();
        this.totalAmount = 0;
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
    }

    /**
     * 전체 정보 포함 생성자 (파일에서 읽을 때 사용)
     */
    public Order(String orderId, String userId, int totalAmount, String deliveryAddress,
                 String phoneNumber, OrderStatus status, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.orderDate = orderDate;
        this.orderItems = new ArrayList<>();

        // 기존 주문 ID에서 숫자 부분을 추출하여 카운터 업데이트
        updateOrderCounter(orderId);
    }

    /**
     * 주문 ID 자동 생성
     * 형식: ORD001, ORD002, ORD003...
     */
    private String generateOrderId() {
        return String.format("ORD%03d", orderCounter++);
    }

    /**
     * 기존 주문 ID로부터 카운터 업데이트
     */
    private void updateOrderCounter(String orderId) {
        if (orderId != null && orderId.startsWith("ORD")) {
            try {
                int idNumber = Integer.parseInt(orderId.substring(3)); // ORD001 -> 001 -> 1
                if (idNumber >= orderCounter) {
                    orderCounter = idNumber + 1;
                }
            } catch (NumberFormatException e) {
                // ID 형식이 잘못된 경우 무시
            }
        }
    }

    /**
     * 주문에 상품 추가
     * @param product 상품
     * @param quantity 수량
     */
    public void addOrderItem(Product product, int quantity) {
        OrderItem orderItem = new OrderItem(product, quantity);
        orderItems.add(orderItem);
        calculateTotalAmount(); // 총액 재계산
    }

    /**
     * 주문 아이템 추가 (OrderItem 객체로)
     * @param orderItem 주문 아이템
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        calculateTotalAmount(); // 총액 재계산
    }

    /**
     * 총 주문 금액 계산
     */
    public void calculateTotalAmount() {
        totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.getTotalPrice();
        }
    }

    /**
     * 주문 취소 가능 여부 확인
     * @return 취소 가능하면 true
     */
    public boolean isCancellable() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    /**
     * 주문 취소
     * @return 취소 성공 여부
     */
    public boolean cancel() {
        if (isCancellable()) {
            this.status = OrderStatus.CANCELLED;
            return true;
        }
        return false;
    }

    /**
     * 주문 확인 처리
     */
    public void confirm() {
        if (status == OrderStatus.PENDING) {
            this.status = OrderStatus.CONFIRMED;
        }
    }

    /**
     * 배송 시작 처리
     */
    public void ship() {
        if (status == OrderStatus.CONFIRMED) {
            this.status = OrderStatus.SHIPPED;
        }
    }

    /**
     * 배송 완료 처리
     */
    public void deliver() {
        if (status == OrderStatus.SHIPPED) {
            this.status = OrderStatus.DELIVERED;
        }
    }

    // Getter/Setter 메서드들
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculateTotalAmount(); // 아이템 설정 시 총액 재계산
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * 총 금액을 원화 형식으로 반환
     */
    public String getFormattedTotalAmount() {
        return String.format("%,d원", totalAmount);
    }

    /**
     * 주문 일시를 문자열로 반환
     */
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderDate.format(formatter);
    }

    /**
     * 파일 저장용 문자열 변환
     * 주문 기본 정보만 저장 (주문 아이템은 별도 파일)
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderId + "|" + userId + "|" + totalAmount + "|" + deliveryAddress + "|" +
                phoneNumber + "|" + status.name() + "|" + orderDate.format(formatter);
    }

    /**
     * 파일에서 읽은 문자열로부터 Order 객체 생성
     */
    public static Order fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime orderDate = LocalDateTime.parse(parts[6], formatter);
        OrderStatus status = OrderStatus.valueOf(parts[5]);

        return new Order(
                parts[0], // orderId
                parts[1], // userId
                Integer.parseInt(parts[2]), // totalAmount
                parts[3], // deliveryAddress
                parts[4], // phoneNumber
                status,   // status
                orderDate // orderDate
        );
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", orderDate=" + orderDate +
                ", itemCount=" + (orderItems != null ? orderItems.size() : 0) +
                '}';
    }
}
