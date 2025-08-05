// =================================================================
// CartRepositoryTest.java - 장바구니 Repository 테스트
// =================================================================
package javaproject.test;

import javaproject.domain.Cart;
import javaproject.domain.CartItem;
import javaproject.domain.Product;
import javaproject.exception.FileIOException;
import javaproject.repository.CartRepository;
import javaproject.repository.ProductRepository;

import java.util.List;

/**
 * CartRepository 기능을 테스트하는 클래스
 */
public class CartRepositoryTest {

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private final String TEST_USER_ID = "testcartuser";

    /**
     * 테스트 초기화
     */
    public CartRepositoryTest() {
        this.cartRepository = CartRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
    }

    /**
     * 모든 테스트 실행
     */
    public void runAllTests() {
        System.out.println("========== CartRepository 테스트 시작 ==========");

        try {
            // 테스트 전 장바구니 비우기
            cartRepository.clearCart(TEST_USER_ID);

            testAddToCart();
            testGetCart();
            testUpdateQuantity();
            testRemoveFromCart();
            testClearCart();
            testCartStatistics();

            System.out.println("✅ CartRepository 모든 테스트 통과!");

        } catch (Exception e) {
            System.err.println("❌ CartRepository 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("========== CartRepository 테스트 완료 ==========\n");
    }

    /**
     * 장바구니에 상품 추가 테스트
     */
    private void testAddToCart() throws FileIOException {
        System.out.println("📋 장바구니 추가 테스트...");

        // 첫 번째 상품 가져오기
        List<Product> products = productRepository.findAll();
        assert !products.isEmpty() : "테스트할 상품이 없음";

        Product testProduct = products.get(0);
        String productId = testProduct.getProductId();

        // 장바구니에 상품 추가
        boolean added = cartRepository.addToCart(TEST_USER_ID, productId, 2);
        assert added : "장바구니에 상품 추가 실패";

        // 같은 상품 추가 (수량 증가)
        boolean addedAgain = cartRepository.addToCart(TEST_USER_ID, productId, 3);
        assert addedAgain : "같은 상품 추가 실패";

        System.out.println("   ✅ 장바구니 추가 테스트 통과");
    }

    /**
     * 장바구니 조회 테스트
     */
    private void testGetCart() throws FileIOException {
        System.out.println("📋 장바구니 조회 테스트...");

        // 장바구니 조회
        Cart cart = cartRepository.getCart(TEST_USER_ID);
        assert cart != null : "장바구니 조회 실패";
        assert !cart.isEmpty() : "장바구니가 비어있음";

        // 장바구니 내용 확인
        List<CartItem> items = cart.getItems();
        assert !items.isEmpty() : "장바구니 아이템이 없음";

        CartItem firstItem = items.get(0);
        assert firstItem.getQuantity() == 5 : "상품 수량이 일치하지 않음 (예상: 5, 실제: " + firstItem.getQuantity() + ")";
        assert firstItem.getProduct() != null : "상품 정보가 로드되지 않음";

        System.out.println("   ✅ 장바구니 조회 테스트 통과 (아이템 수: " + items.size() + ")");
    }

    /**
     * 장바구니 수량 변경 테스트
     */
    private void testUpdateQuantity() throws FileIOException {
        System.out.println("📋 장바구니 수량 변경 테스트...");

        // 첫 번째 상품 ID 가져오기
        Cart cart = cartRepository.getCart(TEST_USER_ID);
        assert !cart.isEmpty() : "장바구니가 비어있음";

        CartItem firstItem = cart.getItems().get(0);
        String productId = firstItem.getProductId();

        // 수량 변경
        boolean updated = cartRepository.updateCartItemQuantity(TEST_USER_ID, productId, 10);
        assert updated : "수량 변경 실패";

        // 변경 확인
        Cart updatedCart = cartRepository.getCart(TEST_USER_ID);
        CartItem updatedItem = updatedCart.getItems().get(0);
        assert updatedItem.getQuantity() == 10 : "수량이 변경되지 않음";

        System.out.println("   ✅ 장바구니 수량 변경 테스트 통과");
    }

    /**
     * 장바구니에서 상품 제거 테스트
     */
    private void testRemoveFromCart() throws FileIOException {
        System.out.println("📋 장바구니 상품 제거 테스트...");

        // 두 번째 상품 추가 (제거 테스트용)
        List<Product> products = productRepository.findAll();
        if (products.size() > 1) {
            String secondProductId = products.get(1).getProductId();
            cartRepository.addToCart(TEST_USER_ID, secondProductId, 1);
        }

        // 제거 전 아이템 수 확인
        Cart cartBefore = cartRepository.getCart(TEST_USER_ID);
        int itemCountBefore = cartBefore.getItemCount();

        // 첫 번째 상품 제거
        CartItem firstItem = cartBefore.getItems().get(0);
        String productIdToRemove = firstItem.getProductId();

        boolean removed = cartRepository.removeFromCart(TEST_USER_ID, productIdToRemove);
        assert removed : "상품 제거 실패";

        // 제거 후 확인
        Cart cartAfter = cartRepository.getCart(TEST_USER_ID);
        assert cartAfter.getItemCount() == itemCountBefore - 1 : "아이템 수가 감소하지 않음";

        System.out.println("   ✅ 장바구니 상품 제거 테스트 통과");
    }

    /**
     * 장바구니 비우기 테스트
     */
    private void testClearCart() throws FileIOException {
        System.out.println("📋 장바구니 비우기 테스트...");

        // 장바구니 비우기
        boolean cleared = cartRepository.clearCart(TEST_USER_ID);
        assert cleared : "장바구니 비우기 실패";

        // 비우기 확인
        Cart emptyCart = cartRepository.getCart(TEST_USER_ID);
        assert emptyCart.isEmpty() : "장바구니가 비워지지 않음";

        System.out.println("   ✅ 장바구니 비우기 테스트 통과");
    }

    /**
     * 장바구니 통계 테스트
     */
    private void testCartStatistics() throws FileIOException {
        System.out.println("📋 장바구니 통계 테스트...");

        String statistics = cartRepository.getCartStatistics();
        assert statistics != null && !statistics.isEmpty() : "장바구니 통계가 비어있음";

        System.out.println("   ✅ 장바구니 통계 테스트 통과");
        System.out.println(statistics);
    }
}