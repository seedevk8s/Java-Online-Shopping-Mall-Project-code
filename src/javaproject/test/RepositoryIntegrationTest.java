// =================================================================
// RepositoryIntegrationTest.java - 통합 테스트
// =================================================================
package javaproject.test;

import javaproject.domain.*;
import javaproject.exception.AuthenticationException;
import javaproject.exception.FileIOException;
import javaproject.repository.CartRepository;
import javaproject.repository.OrderRepository;
import javaproject.repository.ProductRepository;
import javaproject.repository.UserRepository;

/**
 * Repository들 간의 상호작용을 테스트하는 통합 테스트 클래스
 * 실제 사용 시나리오를 기반으로 한 테스트
 */
public class RepositoryIntegrationTest {

    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;

    private final String TEST_USER_ID = "integrationtest";
    private final String TEST_EMAIL = "integration@test.com";

    public RepositoryIntegrationTest() {
        this.userRepository = UserRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
        this.cartRepository = CartRepository.getInstance();
        this.orderRepository = OrderRepository.getInstance();
    }

    /**
     * 통합 테스트 실행
     */
    public void runIntegrationTest() {
        System.out.println("========== Repository 통합 테스트 시작 ==========");

        try {
            // 테스트 시나리오: 회원가입 → 상품 조회 → 장바구니 → 주문
            testUserRegistrationScenario();
            testShoppingScenario();
            testOrderScenario();

            // 정리
            cleanup();

            System.out.println("✅ Repository 통합 테스트 모든 시나리오 통과!");

        } catch (Exception e) {
            System.err.println("❌ 통합 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("========== Repository 통합 테스트 완료 ==========\n");
    }

    /**
     * 사용자 등록 시나리오 테스트
     */
    private void testUserRegistrationScenario() throws FileIOException, AuthenticationException {
        System.out.println("📋 사용자 등록 시나리오 테스트...");

        // 1. 새 사용자 등록
        User newUser = new User(TEST_USER_ID, "password123", "통합테스트사용자", TEST_EMAIL);
        boolean registered = userRepository.save(newUser);
        assert registered : "사용자 등록 실패";

        // 2. 로그인 확인
        User loggedInUser = userRepository.login(TEST_USER_ID, "password123");
        assert loggedInUser != null : "등록 후 로그인 실패";
        assert TEST_USER_ID.equals(loggedInUser.getUserId()) : "로그인된 사용자 ID 불일치";

        System.out.println("   ✅ 사용자 등록 시나리오 통과");
    }

    /**
     * 쇼핑 시나리오 테스트 (상품 조회 → 장바구니)
     */
    private void testShoppingScenario() throws FileIOException {
        System.out.println("📋 쇼핑 시나리오 테스트...");

        // 1. 상품 목록 조회
        java.util.List<Product> products = productRepository.findAll();
        assert !products.isEmpty() : "상품이 없음";

        // 2. 첫 번째 상품을 장바구니에 추가
        Product firstProduct = products.get(0);
        boolean addedToCart = cartRepository.addToCart(TEST_USER_ID, firstProduct.getProductId(), 2);
        assert addedToCart : "장바구니 추가 실패";

        // 3. 두 번째 상품도 장바구니에 추가 (있다면)
        if (products.size() > 1) {
            Product secondProduct = products.get(1);
            cartRepository.addToCart(TEST_USER_ID, secondProduct.getProductId(), 1);
        }

        // 4. 장바구니 확인
        Cart cart = cartRepository.getCart(TEST_USER_ID);
        assert !cart.isEmpty() : "장바구니가 비어있음";
        assert cart.getTotalQuantity() >= 2 : "장바구니 수량이 올바르지 않음";

        System.out.println("   ✅ 쇼핑 시나리오 통과 (장바구니 아이템: " + cart.getItemCount() + "개)");
    }

    /**
     * 주문 시나리오 테스트 (장바구니 → 주문)
     */
    private void testOrderScenario() throws FileIOException {
        System.out.println("📋 주문 시나리오 테스트...");

        // 1. 장바구니 조회
        Cart cart = cartRepository.getCart(TEST_USER_ID);
        assert !cart.isEmpty() : "주문할 장바구니가 비어있음";

        // 2. 주문 생성
        Order newOrder = new Order(TEST_USER_ID, "서울시 강남구 테스트동 123-45", "010-1234-5678");

        // 3. 장바구니 아이템을 주문 아이템으로 변환
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct() != null) {
                newOrder.addOrderItem(cartItem.getProduct(), cartItem.getQuantity());
            }
        }

        // 4. 주문 저장
        boolean orderSaved = orderRepository.save(newOrder);
        assert orderSaved : "주문 저장 실패";

        // 5. 주문 조회 및 확인
        java.util.Optional<Order> savedOrderOpt = orderRepository.findById(newOrder.getOrderId());
        assert savedOrderOpt.isPresent() : "저장된 주문을 찾을 수 없음";

        Order savedOrder = savedOrderOpt.get();
        assert !savedOrder.getOrderItems().isEmpty() : "주문 아이템이 없음";
        assert savedOrder.getTotalAmount() > 0 : "주문 총액이 0원";

        // 6. 주문 후 장바구니 비우기
        cartRepository.clearCart(TEST_USER_ID);
        Cart emptyCart = cartRepository.getCart(TEST_USER_ID);
        assert emptyCart.isEmpty() : "주문 후 장바구니가 비워지지 않음";

        System.out.println("   ✅ 주문 시나리오 통과 (주문 ID: " + savedOrder.getOrderId() +
                ", 총액: " + savedOrder.getFormattedTotalAmount() + ")");
    }

    /**
     * 테스트 데이터 정리
     */
    private void cleanup() throws FileIOException {
        System.out.println("📋 테스트 데이터 정리...");

        // 테스트 사용자 삭제
        userRepository.deleteById(TEST_USER_ID);

        // 테스트 사용자의 주문 삭제
        java.util.List<Order> userOrders = orderRepository.findByUserId(TEST_USER_ID);
        for (Order order : userOrders) {
            orderRepository.deleteById(order.getOrderId());
        }

        // 장바구니 비우기
        cartRepository.clearCart(TEST_USER_ID);

        System.out.println("   ✅ 테스트 데이터 정리 완료");
    }

    /**
     * 통합 테스트 실행 메인 메서드
     */
    public static void main(String[] args) {
        RepositoryIntegrationTest integrationTest = new RepositoryIntegrationTest();
        integrationTest.runIntegrationTest();
    }
}

// =================================================================
// 테스트 실행 방법 가이드
// =================================================================
/*
💡 테스트 실행 방법:

1. 전체 테스트 실행:
   java javaproject.test.TestRunner

2. 개별 테스트 실행:
   java javaproject.test.UserRepositoryTest (사용자 테스트만)
   java javaproject.test.ProductRepositoryTest (상품 테스트만)
   java javaproject.test.CartRepositoryTest (장바구니 테스트만)

3. 통합 테스트 실행:
   java javaproject.test.RepositoryIntegrationTest

📋 테스트 내용:
- UserRepository: 회원가입, 로그인, 정보수정, 삭제
- ProductRepository: 상품 CRUD, 검색, 재고관리, 통계
- CartRepository: 장바구니 추가/제거/수정, 비우기
- 통합 테스트: 실제 쇼핑 시나리오 (회원가입→쇼핑→주문)

✅ 모든 테스트가 통과하면 Repository 계층이 정상 작동하는 것입니다!
*/