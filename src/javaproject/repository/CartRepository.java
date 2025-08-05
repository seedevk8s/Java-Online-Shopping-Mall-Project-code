// =================================================================
// CartRepository.java - 장바구니 데이터 저장소
// =================================================================
package javaproject.repository;

import javaproject.domain.Cart;
import javaproject.domain.CartItem;
import javaproject.domain.Product;
import javaproject.exception.FileIOException;
import javaproject.util.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 장바구니 데이터를 파일에 저장하고 조회하는 Repository 클래스
 * 장바구니 아이템 추가, 제거, 수량 변경 등에 사용
 */
public class CartRepository {

    // 장바구니 아이템 데이터를 저장할 파일명
    private static final String CART_ITEM_FILE_NAME = "cart_items.txt";

    // 싱글톤 인스턴스
    private static CartRepository instance;

    // ProductRepository 참조 (상품 정보 조회용)
    private ProductRepository productRepository;

    /**
     * private 생성자 (싱글톤 패턴)
     */
    private CartRepository() {
        // 초기화 시 데이터 디렉토리 생성
        FileManager.initializeDataDirectory();

        // ProductRepository 초기화
        this.productRepository = ProductRepository.getInstance();
    }

    /**
     * CartRepository 인스턴스 반환 (싱글톤)
     * @return CartRepository 인스턴스
     */
    public static CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    /**
     * 사용자의 장바구니 조회
     * @param userId 사용자 ID
     * @return 사용자의 장바구니
     * @throws FileIOException 파일 읽기 실패 시
     */
    public Cart getCart(String userId) throws FileIOException {
        if (userId == null || userId.trim().isEmpty()) {
            return new Cart();
        }

        Cart cart = new Cart(userId);
        List<CartItem> cartItems = findCartItemsByUserId(userId);

        // 각 CartItem에 Product 정보 설정
        for (CartItem item : cartItems) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isPresent()) {
                item.setProduct(productOpt.get());
            }
        }

        cart.setItems(cartItems);
        return cart;
    }

    /**
     * 장바구니에 상품 추가
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param quantity 수량
     * @return 추가 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean addToCart(String userId, String productId, int quantity) throws FileIOException {
        if (userId == null || productId == null || quantity <= 0) {
            return false;
        }

        // 해당 상품이 이미 장바구니에 있는지 확인
        Optional<CartItem> existingItemOpt = findCartItem(userId, productId);

        if (existingItemOpt.isPresent()) {
            // 기존 상품의 수량 증가
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            return updateCartItemQuantity(userId, productId, newQuantity);
        } else {
            // 새 상품 추가
            CartItem newItem = new CartItem(productId, quantity);
            return saveCartItem(userId, newItem);
        }
    }

    /**
     * 장바구니에서 상품 제거
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @return 제거 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean removeFromCart(String userId, String productId) throws FileIOException {
        if (userId == null || productId == null) {
            return false;
        }

        List<String> lines = FileManager.readLines(CART_ITEM_FILE_NAME);
        List<String> newLines = new ArrayList<>();
        boolean removed = false;

        // 해당 사용자의 해당 상품을 제외하고 나머지만 보존
        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                String lineUserId = parts[0];
                String lineProductId = parts[1];

                if (userId.equals(lineUserId) && productId.equals(lineProductId)) {
                    removed = true; // 해당 라인 제거
                } else {
                    newLines.add(line); // 나머지 라인 보존
                }
            } catch (Exception e) {
                newLines.add(line); // 파싱 오류 시 그대로 보존
            }
        }

        if (removed) {
            FileManager.writeLines(CART_ITEM_FILE_NAME, newLines);
        }

        return removed;
    }

    /**
     * 장바구니 상품 수량 변경
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param newQuantity 새로운 수량
     * @return 변경 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean updateCartItemQuantity(String userId, String productId, int newQuantity) throws FileIOException {
        if (userId == null || productId == null || newQuantity < 0) {
            return false;
        }

        // 수량이 0이면 상품 제거
        if (newQuantity == 0) {
            return removeFromCart(userId, productId);
        }

        List<String> lines = FileManager.readLines(CART_ITEM_FILE_NAME);
        List<String> newLines = new ArrayList<>();
        boolean updated = false;

        // 해당 상품의 수량만 변경
        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                String lineUserId = parts[0];
                String lineProductId = parts[1];

                if (userId.equals(lineUserId) && productId.equals(lineProductId)) {
                    // 수량 변경
                    newLines.add(userId + "|" + productId + "|" + newQuantity);
                    updated = true;
                } else {
                    newLines.add(line); // 다른 라인은 그대로 보존
                }
            } catch (Exception e) {
                newLines.add(line); // 파싱 오류 시 그대로 보존
            }
        }

        if (updated) {
            FileManager.writeLines(CART_ITEM_FILE_NAME, newLines);
        }

        return updated;
    }

    /**
     * 사용자의 장바구니 비우기
     * @param userId 사용자 ID
     * @return 비우기 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean clearCart(String userId) throws FileIOException {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        List<String> lines = FileManager.readLines(CART_ITEM_FILE_NAME);
        List<String> newLines = new ArrayList<>();

        // 해당 사용자의 모든 장바구니 아이템 제거
        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                String lineUserId = parts[0];

                if (!userId.equals(lineUserId)) {
                    newLines.add(line); // 다른 사용자의 아이템은 보존
                }
            } catch (Exception e) {
                newLines.add(line); // 파싱 오류 시 그대로 보존
            }
        }

        FileManager.writeLines(CART_ITEM_FILE_NAME, newLines);
        return true;
    }

    // ================= 내부 헬퍼 메서드 =================

    /**
     * 사용자 ID로 장바구니 아이템 목록 조회
     * @param userId 사용자 ID
     * @return 장바구니 아이템 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    private List<CartItem> findCartItemsByUserId(String userId) throws FileIOException {
        List<String> lines = FileManager.readLines(CART_ITEM_FILE_NAME);
        List<CartItem> cartItems = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                String lineUserId = parts[0];

                if (userId.equals(lineUserId)) {
                    String productId = parts[1];
                    int quantity = Integer.parseInt(parts[2]);

                    CartItem item = new CartItem(productId, quantity);
                    cartItems.add(item);
                }
            } catch (Exception e) {
                System.err.println("장바구니 데이터 파싱 오류: " + line + " - " + e.getMessage());
            }
        }

        return cartItems;
    }

    /**
     * 특정 사용자의 특정 상품 장바구니 아이템 조회
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @return 장바구니 아이템 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    private Optional<CartItem> findCartItem(String userId, String productId) throws FileIOException {
        List<CartItem> cartItems = findCartItemsByUserId(userId);

        return cartItems.stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst();
    }

    /**
     * 장바구니 아이템 저장
     * @param userId 사용자 ID
     * @param cartItem 장바구니 아이템
     * @return 저장 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    private boolean saveCartItem(String userId, CartItem cartItem) throws FileIOException {
        String line = userId + "|" + cartItem.getProductId() + "|" + cartItem.getQuantity();
        FileManager.appendLine(CART_ITEM_FILE_NAME, line);
        return true;
    }

    /**
     * 장바구니 데이터 백업
     * @return 백업 성공 여부
     */
    public boolean backupCartData() {
        return FileManager.backupFile(CART_ITEM_FILE_NAME);
    }

    /**
     * 장바구니 통계 정보 조회
     * @return 장바구니 통계 문자열
     * @throws FileIOException 파일 읽기 실패 시
     */
    public String getCartStatistics() throws FileIOException {
        List<String> lines = FileManager.readLines(CART_ITEM_FILE_NAME);
        java.util.Set<String> uniqueUsers = new java.util.HashSet<>();
        int totalItems = 0;

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                uniqueUsers.add(parts[0]); // 사용자 ID 수집
                totalItems += Integer.parseInt(parts[2]); // 수량 합계
            } catch (Exception e) {
                // 파싱 오류 무시
            }
        }

        StringBuilder stats = new StringBuilder();
        stats.append("=== 장바구니 통계 ===\n");
        stats.append("활성 사용자: ").append(uniqueUsers.size()).append("명\n");
        stats.append("총 아이템: ").append(lines.size()).append("종류\n");
        stats.append("총 수량: ").append(totalItems).append("개\n");
        stats.append("===================");

        return stats.toString();
    }
}