package javaproject.service;

import javaproject.domain.Cart;
import javaproject.domain.CartItem;
import javaproject.domain.Product;
import javaproject.repository.CartRepository;
import javaproject.repository.ProductRepository;
import javaproject.exception.ProductNotFoundException;
import javaproject.exception.InsufficientStockException;
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class CartService {

    // 싱글톤 인스턴스
    private static final CartService instance = new CartService();

    // Repository 인스턴스들
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private CartService() {
        this.cartRepository = CartRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return CartService의 유일한 인스턴스
     */
    public static CartService getInstance() {
        return instance;
    }

    /**
     * 사용자 ID로 장바구니 조회
     * 장바구니가 없으면 새로 생성
     *
     * @param userId 사용자 ID
     * @return 사용자의 장바구니
     */
    public Cart getCartByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }

        // Repository에서 장바구니 조회
        Cart cart = cartRepository.findByUserId(userId);

        // 장바구니가 없으면 새로 생성
        if (cart == null) {
            cart = createNewCart(userId);
        }

        return cart;
    }

    /**
     * 새 장바구니 생성
     *
     * @param userId 사용자 ID
     * @return 생성된 장바구니
     */
    private Cart createNewCart(String userId) {
        Cart newCart = new Cart(userId);
        cartRepository.save(newCart);
        return newCart;
    }

    /**
     * 장바구니에 상품 추가
     * 이미 있는 상품이면 수량 증가
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param quantity 추가할 수량
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws InsufficientStockException 재고가 부족한 경우
     */
    public void addToCart(String userId, String productId, int quantity)
            throws ProductNotFoundException, InsufficientStockException {

        // 파라미터 검증
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 ID가 유효하지 않습니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        // 상품 존재 여부 확인
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }

        // 재고 확인
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    String.format("재고가 부족합니다. (요청: %d개, 재고: %d개)",
                            quantity, product.getStock())
            );
        }

        // 장바구니 가져오기 (없으면 생성)
        Cart cart = getCartByUserId(userId);

        // 이미 장바구니에 있는 상품인지 확인
        CartItem existingItem = findCartItem(cart, productId);

        if (existingItem != null) {
            // 기존 상품의 수량 증가
            int newQuantity = existingItem.getQuantity() + quantity;

            // 재고 재확인
            if (product.getStock() < newQuantity) {
                throw new InsufficientStockException(
                        String.format("재고가 부족합니다. (요청 총합: %d개, 재고: %d개)",
                                newQuantity, product.getStock())
                );
            }

            existingItem.setQuantity(newQuantity);
        } else {
            // 새 상품 추가
            CartItem newItem = new CartItem(productId, quantity);
            cart.addItem(newItem);
        }

        // 변경사항 저장
        cartRepository.update(cart);
    }

    /**
     * 장바구니에서 상품 제거
     *
     * @param userId 사용자 ID
     * @param productId 제거할 상품 ID
     * @return 제거 성공 여부
     */
    public boolean removeFromCart(String userId, String productId) {
        if (userId == null || productId == null) {
            return false;
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return false;
        }

        // 상품 찾아서 제거
        CartItem itemToRemove = findCartItem(cart, productId);
        if (itemToRemove != null) {
            cart.removeItem(itemToRemove);
            cartRepository.update(cart);
            return true;
        }

        return false;
    }

    /**
     * 장바구니 상품 수량 변경
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param newQuantity 새로운 수량
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws InsufficientStockException 재고가 부족한 경우
     */
    public void updateCartItemQuantity(String userId, String productId, int newQuantity)
            throws ProductNotFoundException, InsufficientStockException {

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new IllegalArgumentException("장바구니를 찾을 수 없습니다.");
        }

        CartItem item = findCartItem(cart, productId);
        if (item == null) {
            throw new ProductNotFoundException("장바구니에 해당 상품이 없습니다.");
        }

        // 상품 재고 확인
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다.");
        }

        if (product.getStock() < newQuantity) {
            throw new InsufficientStockException(
                    String.format("재고가 부족합니다. (요청: %d개, 재고: %d개)",
                            newQuantity, product.getStock())
            );
        }

        // 수량 업데이트
        item.setQuantity(newQuantity);
        cartRepository.update(cart);
    }

    /**
     * 장바구니 비우기
     *
     * @param userId 사용자 ID
     */
    public void clearCart(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.clearItems();
            cartRepository.update(cart);
        }
    }

    /**
     * 장바구니 총 금액 계산
     *
     * @param userId 사용자 ID
     * @return 장바구니 총 금액
     */
    public double calculateCartTotal(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId());
            if (product != null) {
                total += product.getPrice() * item.getQuantity();
            }
        }

        return total;
    }

    /**
     * 장바구니 상품 개수 조회
     *
     * @param userId 사용자 ID
     * @return 장바구니에 담긴 상품 종류 수
     */
    public int getCartItemCount(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return 0;
        }
        return cart.getItems().size();
    }

    /**
     * 장바구니 상품 총 수량 조회
     *
     * @param userId 사용자 ID
     * @return 장바구니에 담긴 상품의 총 수량
     */
    public int getCartTotalQuantity(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return 0;
        }

        int totalQuantity = 0;
        for (CartItem item : cart.getItems()) {
            totalQuantity += item.getQuantity();
        }

        return totalQuantity;
    }

    /**
     * 장바구니 상품 목록을 상품 정보와 함께 조회
     *
     * @param userId 사용자 ID
     * @return 상품 정보가 포함된 장바구니 아이템 목록
     */
    public List<CartItemWithProduct> getCartItemsWithProducts(String userId) {
        List<CartItemWithProduct> result = new ArrayList<>();

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return result;
        }

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId());
            if (product != null) {
                result.add(new CartItemWithProduct(item, product));
            }
        }

        return result;
    }

    /**
     * 장바구니에서 특정 상품 찾기 (내부 헬퍼 메서드)
     *
     * @param cart 장바구니
     * @param productId 찾을 상품 ID
     * @return 찾은 CartItem, 없으면 null
     */
    private CartItem findCartItem(Cart cart, String productId) {
        if (cart == null || productId == null) {
            return null;
        }

        for (CartItem item : cart.getItems()) {
            if (productId.equals(item.getProductId())) {
                return item;
            }
        }

        return null;
    }

    /**
     * 장바구니 유효성 검증
     * 삭제된 상품이나 재고 부족 상품 체크
     *
     * @param userId 사용자 ID
     * @return 유효성 검증 결과
     */
    public CartValidationResult validateCart(String userId) {
        CartValidationResult result = new CartValidationResult();

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            result.setValid(true);
            result.setMessage("장바구니가 비어있습니다.");
            return result;
        }

        List<String> issues = new ArrayList<>();
        boolean isValid = true;

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId());

            if (product == null) {
                issues.add("상품 ID " + item.getProductId() + "는 더 이상 판매하지 않습니다.");
                isValid = false;
            } else if (product.getStock() < item.getQuantity()) {
                issues.add(product.getName() + "의 재고가 부족합니다. (재고: " +
                        product.getStock() + "개)");
                isValid = false;
            }
        }

        result.setValid(isValid);
        result.setIssues(issues);

        if (!isValid) {
            result.setMessage("장바구니에 문제가 있는 상품이 있습니다.");
        } else {
            result.setMessage("장바구니의 모든 상품이 구매 가능합니다.");
        }

        return result;
    }

    /**
     * 장바구니 아이템과 상품 정보를 함께 담는 DTO 클래스
     */
    public static class CartItemWithProduct {
        private final CartItem cartItem;
        private final Product product;

        public CartItemWithProduct(CartItem cartItem, Product product) {
            this.cartItem = cartItem;
            this.product = product;
        }

        public CartItem getCartItem() { return cartItem; }
        public Product getProduct() { return product; }

        public double getSubtotal() {
            return product.getPrice() * cartItem.getQuantity();
        }
    }

    /**
     * 장바구니 유효성 검증 결과 클래스
     */
    public static class CartValidationResult {
        private boolean valid;
        private String message;
        private List<String> issues;

        public CartValidationResult() {
            this.issues = new ArrayList<>();
            this.valid = true;
        }

        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }
    }
}