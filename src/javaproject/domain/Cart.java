package javaproject.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 장바구니 도메인 클래스
 * 사용자의 장바구니 정보를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class Cart implements Serializable {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 장바구니 ID (고유 식별자)
    private String id;

    // 사용자 ID (장바구니 소유자)
    private String userId;

    // 장바구니 항목 목록
    private List<CartItem> items;

    // 생성 일시
    private LocalDateTime createdDate;

    // 수정 일시
    private LocalDateTime modifiedDate;

    // 총 금액 (계산된 값)
    private double totalAmount;

    // 총 상품 개수 (계산된 값)
    private int totalQuantity;

    /**
     * 기본 생성자
     */
    public Cart() {
        this.items = new ArrayList<>();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.totalAmount = 0;
        this.totalQuantity = 0;
    }

    /**
     * 사용자 ID를 포함한 생성자
     *
     * @param userId 사용자 ID
     */
    public Cart(String userId) {
        this();
        this.userId = userId;
        this.id = generateCartId(userId);
    }

    /**
     * 전체 정보를 포함한 생성자
     *
     * @param id 장바구니 ID
     * @param userId 사용자 ID
     */
    public Cart(String id, String userId) {
        this();
        this.id = id;
        this.userId = userId;
    }

    /**
     * 장바구니 ID 생성
     *
     * @param userId 사용자 ID
     * @return 생성된 장바구니 ID
     */
    private String generateCartId(String userId) {
        return "CART_" + userId + "_" + System.currentTimeMillis();
    }

    /**
     * 장바구니에 항목 추가
     * 이미 있는 상품이면 수량 증가
     *
     * @param item 추가할 항목
     */
    public void addItem(CartItem item) {
        if (item == null) {
            return;
        }

        // 이미 있는 상품인지 확인
        CartItem existingItem = findItemByProductId(item.getProductId());

        if (existingItem != null) {
            // 기존 항목의 수량 증가
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            // 새 항목 추가
            this.items.add(item);
        }

        updateTotals();
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 장바구니에서 항목 제거
     *
     * @param item 제거할 항목
     */
    public void removeItem(CartItem item) {
        if (item != null && this.items.remove(item)) {
            updateTotals();
            this.modifiedDate = LocalDateTime.now();
        }
    }

    /**
     * 상품 ID로 항목 제거
     *
     * @param productId 제거할 상품 ID
     * @return 제거 성공 여부
     */
    public boolean removeItemByProductId(String productId) {
        CartItem item = findItemByProductId(productId);
        if (item != null) {
            removeItem(item);
            return true;
        }
        return false;
    }

    /**
     * 장바구니 비우기
     */
    public void clearItems() {
        this.items.clear();
        this.totalAmount = 0;
        this.totalQuantity = 0;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 상품 ID로 장바구니 항목 찾기
     *
     * @param productId 상품 ID
     * @return 찾은 항목, 없으면 null
     */
    public CartItem findItemByProductId(String productId) {
        if (productId == null) {
            return null;
        }

        for (CartItem item : items) {
            if (productId.equals(item.getProductId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 총 금액과 수량 업데이트
     * ProductService를 통해 가격 정보를 가져와야 하므로
     * 실제로는 Service 레이어에서 처리
     */
    private void updateTotals() {
        this.totalQuantity = 0;
        for (CartItem item : items) {
            this.totalQuantity += item.getQuantity();
        }
    }

    /**
     * 장바구니가 비어있는지 확인
     *
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    /**
     * 장바구니 항목 개수 반환
     *
     * @return 항목 개수
     */
    public int getItemCount() {
        return items.size();
    }

    // Getters and Setters

    /**
     * 장바구니 ID 반환
     * @return 장바구니 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 장바구니 ID 설정
     * @param id 장바구니 ID
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
     * 장바구니 항목 목록 반환
     * @return 항목 목록
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * 장바구니 항목 목록 설정
     * @param items 항목 목록
     */
    public void setItems(List<CartItem> items) {
        this.items = items;
        updateTotals();
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
     * 총 상품 개수 반환
     * @return 총 상품 개수
     */
    public int getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * 총 상품 개수 설정
     * @param totalQuantity 총 상품 개수
     */
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    /**
     * 장바구니 정보를 문자열로 변환
     *
     * @return 장바구니 정보 문자열
     */
    @Override
    public String toString() {
        return String.format(
                "Cart{id='%s', userId='%s', itemCount=%d, totalQuantity=%d}",
                id, userId, items.size(), totalQuantity
        );
    }

    /**
     * 객체 동등성 비교
     *
     * @param obj 비교할 객체
     * @return 동일한 장바구니면 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cart cart = (Cart) obj;
        return Objects.equals(id, cart.id) &&
                Objects.equals(userId, cart.userId);
    }

    /**
     * 해시코드 생성
     *
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}