package javaproject.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 주문 항목 도메인 클래스
 * 주문에 포함된 개별 상품 정보를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderItem implements Serializable {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 주문 항목 ID (고유 식별자)
    private String id;

    // 주문 ID (어떤 주문에 속하는지)
    private String orderId;

    // 상품 ID
    private String productId;

    // 수량
    private int quantity;

    // 주문 당시 상품 가격 (단가)
    private double price;

    // 소계 (가격 * 수량)
    private double subtotal;

    // 할인 금액 (선택사항)
    private double discountAmount;

    // 생성 일시
    private LocalDateTime createdDate;

    // 배송 상태 (선택사항)
    private String shippingStatus;

    // 상품명 (스냅샷 - 주문 당시의 상품명 보존)
    private String productName;

    /**
     * 기본 생성자
     */
    public OrderItem() {
        this.createdDate = LocalDateTime.now();
        this.discountAmount = 0;
    }

    /**
     * 필수 정보를 포함한 생성자
     *
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @param quantity 수량
     * @param price 단가
     */
    public OrderItem(String orderId, String productId, int quantity, double price) {
        this();
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = calculateSubtotal();
    }

    /**
     * 전체 정보를 포함한 생성자
     *
     * @param id 주문 항목 ID
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @param quantity 수량
     * @param price 단가
     */
    public OrderItem(String id, String orderId, String productId,
                     int quantity, double price) {
        this(orderId, productId, quantity, price);
        this.id = id;
    }

    /**
     * 상품명을 포함한 생성자
     *
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @param productName 상품명
     * @param quantity 수량
     * @param price 단가
     */
//    public OrderItem(String orderId, String productId, String productName,
//                     int quantity, double price) {
//        this(orderId, productId, quantity, price);
//        this.productName = productName;
//    }

    /**
     * 소계 계산
     *
     * @return 계산된 소계 (가격 * 수량 - 할인금액)
     */
    private double calculateSubtotal() {
        return (price * quantity) - discountAmount;
    }

    /**
     * 소계 재계산
     * 수량이나 가격이 변경되었을 때 호출
     */
    public void recalculateSubtotal() {
        this.subtotal = calculateSubtotal();
    }

    /**
     * 할인 적용
     *
     * @param discountPercent 할인율 (0~100)
     */
    public void applyDiscount(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("할인율은 0~100 사이여야 합니다.");
        }

        this.discountAmount = (price * quantity) * (discountPercent / 100);
        recalculateSubtotal();
    }

    /**
     * 할인 금액 직접 설정
     *
     * @param discountAmount 할인 금액
     */
    public void setDiscountAmount(double discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }

        double maxDiscount = price * quantity;
        if (discountAmount > maxDiscount) {
            throw new IllegalArgumentException("할인 금액이 상품 가격을 초과할 수 없습니다.");
        }

        this.discountAmount = discountAmount;
        recalculateSubtotal();
    }

    /**
     * 실제 지불 금액 계산
     *
     * @return 할인이 적용된 최종 금액
     */
    public double getActualAmount() {
        return subtotal;
    }

    // Getters and Setters

    /**
     * 주문 항목 ID 반환
     * @return 주문 항목 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 주문 항목 ID 설정
     * @param id 주문 항목 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 주문 ID 반환
     * @return 주문 ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 주문 ID 설정
     * @param orderId 주문 ID
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 상품 ID 반환
     * @return 상품 ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 상품 ID 설정
     * @param productId 상품 ID
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * 수량 반환
     * @return 수량
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 수량 설정
     * @param quantity 수량
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.quantity = quantity;
        recalculateSubtotal();
    }

    /**
     * 단가 반환
     * @return 단가
     */
    public double getPrice() {
        return price;
    }

    /**
     * 단가 설정
     * @param price 단가
     */
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        this.price = price;
        recalculateSubtotal();
    }

    /**
     * 소계 반환
     * @return 소계
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * 소계 설정 (직접 설정은 권장하지 않음)
     * @param subtotal 소계
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * 할인 금액 반환
     * @return 할인 금액
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * 생성 일시 반환
     * @return 생성 일시
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * 생성 일시 설정
     * @param createdDate 생성 일시
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 배송 상태 반환
     * @return 배송 상태
     */
    public String getShippingStatus() {
        return shippingStatus;
    }

    /**
     * 배송 상태 설정
     * @param shippingStatus 배송 상태
     */
    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    /**
     * 상품명 반환
     * @return 상품명
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 상품명 설정
     * @param productName 상품명
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 주문 항목 정보를 문자열로 변환
     *
     * @return 주문 항목 정보 문자열
     */
    @Override
    public String toString() {
        return String.format(
                "OrderItem{id='%s', orderId='%s', productId='%s', quantity=%d, price=%.2f, subtotal=%.2f}",
                id, orderId, productId, quantity, price, subtotal
        );
    }

    /**
     * 주문 항목 상세 정보 출력
     *
     * @return 상세 정보 문자열
     */
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 주문 항목 상세 ===\n");
        sb.append("항목 ID: ").append(id).append("\n");
        sb.append("주문 ID: ").append(orderId).append("\n");
        sb.append("상품 ID: ").append(productId).append("\n");

        if (productName != null) {
            sb.append("상품명: ").append(productName).append("\n");
        }

        sb.append("단가: ").append(String.format("%,.0f원", price)).append("\n");
        sb.append("수량: ").append(quantity).append("개\n");

        if (discountAmount > 0) {
            sb.append("할인: ").append(String.format("%,.0f원", discountAmount)).append("\n");
        }

        sb.append("소계: ").append(String.format("%,.0f원", subtotal)).append("\n");

        if (shippingStatus != null) {
            sb.append("배송 상태: ").append(shippingStatus).append("\n");
        }

        sb.append("생성일: ").append(createdDate).append("\n");

        return sb.toString();
    }

    /**
     * 객체 동등성 비교
     * ID를 기준으로 비교
     *
     * @param obj 비교할 객체
     * @return 동일한 주문 항목이면 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        OrderItem orderItem = (OrderItem) obj;

        // ID가 모두 null이 아닌 경우 ID로 비교
        if (id != null && orderItem.id != null) {
            return Objects.equals(id, orderItem.id);
        }

        // ID가 없는 경우 orderId와 productId 조합으로 비교
        return Objects.equals(orderId, orderItem.orderId) &&
                Objects.equals(productId, orderItem.productId);
    }

    /**
     * 해시코드 생성
     * ID 또는 orderId와 productId 조합을 기준으로 생성
     *
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(orderId, productId);
    }

    /**
     * 주문 항목 복사본 생성
     *
     * @return 복사된 주문 항목 객체
     */
    public OrderItem copy() {
        OrderItem copy = new OrderItem();
        copy.id = this.id;
        copy.orderId = this.orderId;
        copy.productId = this.productId;
        copy.quantity = this.quantity;
        copy.price = this.price;
        copy.subtotal = this.subtotal;
        copy.discountAmount = this.discountAmount;
        copy.createdDate = this.createdDate;
        copy.shippingStatus = this.shippingStatus;
        copy.productName = this.productName;

        return copy;
    }

    /**
     * 주문 항목 유효성 검증
     *
     * @return 유효한 주문 항목이면 true
     */
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty() &&
                productId != null && !productId.trim().isEmpty() &&
                quantity > 0 &&
                price >= 0;
    }
}