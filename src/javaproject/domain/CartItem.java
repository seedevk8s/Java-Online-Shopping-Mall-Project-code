package javaproject.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 장바구니 항목 도메인 클래스
 * 장바구니에 담긴 개별 상품 정보를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class CartItem implements Serializable {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 장바구니 항목 ID (고유 식별자)
    private String id;

    // 장바구니 ID (어떤 장바구니에 속하는지)
    private String cartId;

    // 상품 ID
    private String productId;

    // 수량
    private int quantity;

    // 추가 일시
    private LocalDateTime addedDate;

    // 수정 일시
    private LocalDateTime modifiedDate;

    // 선택 여부 (주문 시 선택적으로 구매)
    private boolean selected;

    /**
     * 기본 생성자
     */
    public CartItem() {
        this.addedDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.selected = true; // 기본적으로 선택됨
    }

    /**
     * 필수 정보를 포함한 생성자
     *
     * @param productId 상품 ID
     * @param quantity 수량
     */
    public CartItem(String productId, int quantity) {
        this();
        this.productId = productId;
        this.quantity = quantity;
        this.id = generateItemId();
    }

    /**
     * 전체 정보를 포함한 생성자
     *
     * @param cartId 장바구니 ID
     * @param productId 상품 ID
     * @param quantity 수량
     */
    public CartItem(String cartId, String productId, int quantity) {
        this(productId, quantity);
        this.cartId = cartId;
    }

    /**
     * ID를 포함한 생성자
     *
     * @param id 항목 ID
     * @param cartId 장바구니 ID
     * @param productId 상품 ID
     * @param quantity 수량
     */
    public CartItem(String id, String cartId, String productId, int quantity) {
        this(cartId, productId, quantity);
        this.id = id;
    }

    /**
     * 장바구니 항목 ID 생성
     *
     * @return 생성된 ID
     */
    private String generateItemId() {
        return "CI" + System.currentTimeMillis() +
                "_" + (productId != null ? productId.substring(0, Math.min(productId.length(), 4)) : "");
    }

    /**
     * 수량 증가
     *
     * @param amount 증가시킬 수량
     */
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
            this.modifiedDate = LocalDateTime.now();
        }
    }

    /**
     * 수량 감소
     *
     * @param amount 감소시킬 수량
     * @return 감소 성공 여부
     */
    public boolean decreaseQuantity(int amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
            this.modifiedDate = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * 유효성 검증
     *
     * @return 유효한 항목이면 true
     */
    public boolean isValid() {
        return productId != null && !productId.trim().isEmpty() &&
                quantity > 0;
    }

    // Getters and Setters

    /**
     * 항목 ID 반환
     * @return 항목 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 항목 ID 설정
     * @param id 항목 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 장바구니 ID 반환
     * @return 장바구니 ID
     */
    public String getCartId() {
        return cartId;
    }

    /**
     * 장바구니 ID 설정
     * @param cartId 장바구니 ID
     */
    public void setCartId(String cartId) {
        this.cartId = cartId;
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
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 추가 일시 반환
     * @return 추가 일시
     */
    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    /**
     * 추가 일시 설정
     * @param addedDate 추가 일시
     */
    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    /**
     * 수정 일시 반환
     * @return 수정 일시
     */
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    /**
     * 수정 일시 설정
     * @param modifiedDate 수정 일시
     */
    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * 선택 여부 반환
     * @return 선택 여부
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 선택 여부 설정
     * @param selected 선택 여부
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 장바구니 항목 정보를 문자열로 변환
     *
     * @return 항목 정보 문자열
     */
    @Override
    public String toString() {
        return String.format(
                "CartItem{id='%s', productId='%s', quantity=%d, selected=%s}",
                id, productId, quantity, selected
        );
    }

    /**
     * 객체 동등성 비교
     * ID 또는 cartId와 productId 조합으로 비교
     *
     * @param obj 비교할 객체
     * @return 동일한 항목이면 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CartItem cartItem = (CartItem) obj;

        // ID가 있으면 ID로 비교
        if (id != null && cartItem.id != null) {
            return Objects.equals(id, cartItem.id);
        }

        // ID가 없으면 cartId와 productId 조합으로 비교
        return Objects.equals(cartId, cartItem.cartId) &&
                Objects.equals(productId, cartItem.productId);
    }

    /**
     * 해시코드 생성
     *
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(cartId, productId);
    }

    /**
     * 복사본 생성
     *
     * @return 복사된 객체
     */
    public CartItem copy() {
        CartItem copy = new CartItem();
        copy.id = this.id;
        copy.cartId = this.cartId;
        copy.productId = this.productId;
        copy.quantity = this.quantity;
        copy.addedDate = this.addedDate;
        copy.modifiedDate = this.modifiedDate;
        copy.selected = this.selected;

        return copy;
    }
}