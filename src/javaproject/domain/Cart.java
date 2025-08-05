package javaproject.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 사용자의 장바구니를 나타내는 클래스
 * 여러 상품을 담고 관리하는 기능 제공
 */
public class Cart {
    // 장바구니 소유자 ID
    private String userId;

    // 장바구니에 담긴 상품 목록
    private List<CartItem> items;

    /**
     * 기본 생성자
     */
    public Cart() {
        this.items = new ArrayList<>();
    }

    /**
     * 사용자 ID를 포함한 생성자
     * @param userId 장바구니 소유자 ID
     */
    public Cart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    /**
     * 장바구니에 상품 추가
     * 이미 있는 상품이면 수량 증가, 없으면 새로 추가
     * @param product 상품
     * @param quantity 수량
     * @return 추가 성공 여부
     */
    public boolean addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        // 이미 장바구니에 있는 상품인지 확인
        Optional<CartItem> existingItem = findItemByProductId(product.getProductId());

        if (existingItem.isPresent()) {
            // 기존 상품의 수량 증가
            existingItem.get().increaseQuantity(quantity);
        } else {
            // 새 상품 추가
            CartItem newItem = new CartItem(product, quantity);
            items.add(newItem);
        }

        return true;
    }

    /**
     * 장바구니에서 상품 제거
     * @param productId 제거할 상품 ID
     * @return 제거 성공 여부
     */
    public boolean removeItem(String productId) {
        return items.removeIf(item -> item.getProductId().equals(productId));
    }

    /**
     * 특정 상품의 수량 변경
     * @param productId 상품 ID
     * @param newQuantity 새로운 수량
     * @return 변경 성공 여부
     */
    public boolean updateQuantity(String productId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItem(productId); // 수량이 0 이하면 제거
        }

        Optional<CartItem> item = findItemByProductId(productId);
        if (item.isPresent()) {
            item.get().setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    /**
     * 장바구니 비우기
     */
    public void clear() {
        items.clear();
    }

    /**
     * 장바구니가 비어있는지 확인
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * 장바구니 상품 개수 (종류)
     * @return 상품 종류 수
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * 장바구니 총 수량 (모든 상품의 수량 합)
     * @return 총 수량
     */
    public int getTotalQuantity() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * 장바구니 총 금액 계산
     * @return 총 금액
     */
    public int getTotalAmount() {
        return items.stream().mapToInt(CartItem::getTotalPrice).sum();
    }

    /**
     * 총 금액을 원화 형식으로 반환
     */
    public String getFormattedTotalAmount() {
        return String.format("%,d원", getTotalAmount());
    }

    /**
     * 상품 ID로 장바구니 아이템 찾기
     * @param productId 상품 ID
     * @return 찾은 아이템 (Optional)
     */
    public Optional<CartItem> findItemByProductId(String productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    /**
     * 특정 상품이 장바구니에 있는지 확인
     * @param productId 상품 ID
     * @return 존재하면 true
     */
    public boolean containsProduct(String productId) {
        return findItemByProductId(productId).isPresent();
    }

    /**
     * 재고 확인 (모든 상품의 재고가 충분한지)
     * @return 재고 부족한 상품이 있으면 해당 상품 ID, 모두 충분하면 null
     */
    public String checkStock() {
        for (CartItem item : items) {
            Product product = item.getProduct();
            if (product != null && !product.hasEnoughStock(item.getQuantity())) {
                return product.getProductId(); // 재고 부족한 첫 번째 상품 ID 반환
            }
        }
        return null; // 모든 상품의 재고가 충분함
    }

    // Getter/Setter 메서드들
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items); // 방어적 복사
    }

    public void setItems(List<CartItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "userId='" + userId + '\'' +
                ", itemCount=" + getItemCount() +
                ", totalQuantity=" + getTotalQuantity() +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
}
