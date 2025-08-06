package javaproject.test;

import javaproject.domain.*;
import javaproject.service.CartService;
import javaproject.repository.*;
import javaproject.exception.*;

/**
 * CartService 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class CartServiceTest {

    private static CartService service;
    private static ProductRepository productRepository;
    private static CartRepository cartRepository;

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== CartService 테스트 시작 ======");

        service = CartService.getInstance();
        productRepository = ProductRepository.getInstance();
        cartRepository = CartRepository.getInstance();

        // 초기화
        productRepository.deleteAll();
        cartRepository.deleteAll();

        // 테스트용 상품 준비
        setupTestProducts();

        testGetOrCreateCart();
        testAddToCart();
        testRemoveFromCart();
        testUpdateQuantity();
        testCartCalculation();
        testCartValidation();

        // 정리
        productRepository.deleteAll();
        cartRepository.deleteAll();

        System.out.println("====== CartService 테스트 완료 ======\n");
    }

    /**
     * 테스트용 상품 데이터 준비
     */
    private static void setupTestProducts() {
        productRepository.save(new Product("PRD001", "노트북", "게이밍 노트북",
                1500000, 10, "전자제품"));
        productRepository.save(new Product("PRD002", "마우스", "무선 마우스",
                30000, 50, "전자제품"));
        productRepository.save(new Product("PRD003", "키보드", "기계식 키보드",
                120000, 5, "전자제품"));
        productRepository.save(new Product("PRD004", "품절상품", "재고없음",
                50000, 0, "테스트"));
    }

    /**
     * 장바구니 생성 또는 조회 테스트
     */
    private static void testGetOrCreateCart() {
        System.out.println("\n[TEST] 장바구니 생성/조회 테스트");

        try {
            // 첫 번째 조회 - 새 장바구니 생성
            Cart cart1 = service.getCartByUserId("testuser1");
            assert cart1 != null : "장바구니 생성 실패";
            assert cart1.getUserId().equals("testuser1") : "사용자 ID 불일치";
            System.out.println("✅ 새 장바구니 생성 성공");

            // 두 번째 조회 - 기존 장바구니 반환
            Cart cart2 = service.getCartByUserId("testuser1");
            assert cart2.getId().equals(cart1.getId()) : "동일한 장바구니 반환 실패";
            System.out.println("✅ 기존 장바구니 조회 성공");

        } catch (Exception e) {
            System.err.println("❌ 장바구니 생성/조회 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니에 상품 추가 테스트
     */
    private static void testAddToCart() {
        System.out.println("\n[TEST] 장바구니 상품 추가 테스트");

        try {
            String userId = "testuser2";

            // 정상적인 상품 추가
            service.addToCart(userId, "PRD001", 2);
            Cart cart = service.getCartByUserId(userId);
            assert cart.getItemCount() == 1 : "상품 추가 실패";
            System.out.println("✅ 상품 추가 성공");

            // 동일 상품 추가 (수량 증가)
            service.addToCart(userId, "PRD001", 3);
            CartItem item = cart.findItemByProductId("PRD001");
            assert item.getQuantity() == 5 : "수량 증가 실패";
            System.out.println("✅ 동일 상품 수량 증가 성공: 2 + 3 = 5");

            // 재고 초과 테스트
            try {
                service.addToCart(userId, "PRD001", 10); // 총 15개 (재고 10개)
                assert false : "재고 초과 체크 실패";
            } catch (InsufficientStockException e) {
                System.out.println("✅ 재고 초과 방지 성공");
            }

            // 품절 상품 추가 테스트
            try {
                service.addToCart(userId, "PRD004", 1);
                assert false : "품절 상품 체크 실패";
            } catch (InsufficientStockException e) {
                System.out.println("✅ 품절 상품 추가 방지 성공");
            }

            // 존재하지 않는 상품 추가 테스트
            try {
                service.addToCart(userId, "NOTEXIST", 1);
                assert false : "존재하지 않는 상품 체크 실패";
            } catch (ProductNotFoundException e) {
                System.out.println("✅ 존재하지 않는 상품 추가 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 상품 추가 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니에서 상품 제거 테스트
     */
    private static void testRemoveFromCart() {
        System.out.println("\n[TEST] 장바구니 상품 제거 테스트");

        try {
            String userId = "testuser3";

            // 상품 추가
            service.addToCart(userId, "PRD001", 2);
            service.addToCart(userId, "PRD002", 3);

            Cart cart = service.getCartByUserId(userId);
            assert cart.getItemCount() == 2 : "초기 상품 개수 오류";

            // 상품 제거
            boolean removed = service.removeFromCart(userId, "PRD001");
            assert removed : "상품 제거 실패";
            assert cart.getItemCount() == 1 : "제거 후 상품 개수 오류";
            assert cart.findItemByProductId("PRD001") == null : "제거된 상품이 남아있음";
            System.out.println("✅ 상품 제거 성공");

            // 존재하지 않는 상품 제거 시도
            boolean notRemoved = service.removeFromCart(userId, "PRD999");
            assert !notRemoved : "존재하지 않는 상품 제거 체크 실패";
            System.out.println("✅ 존재하지 않는 상품 제거 방지 성공");

        } catch (Exception e) {
            System.err.println("❌ 상품 제거 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 상품 수량 변경 테스트
     */
    private static void testUpdateQuantity() {
        System.out.println("\n[TEST] 장바구니 수량 변경 테스트");

        try {
            String userId = "testuser4";

            // 상품 추가
            service.addToCart(userId, "PRD003", 2); // 키보드, 재고 5개

            // 정상적인 수량 변경
            service.updateCartItemQuantity(userId, "PRD003", 4);
            Cart cart = service.getCartByUserId(userId);
            CartItem item = cart.findItemByProductId("PRD003");
            assert item.getQuantity() == 4 : "수량 변경 실패";
            System.out.println("✅ 수량 변경 성공: 2 → 4");

            // 재고 초과 수량 변경 시도
            try {
                service.updateCartItemQuantity(userId, "PRD003", 10); // 재고 5개
                assert false : "재고 초과 수량 변경 체크 실패";
            } catch (InsufficientStockException e) {
                System.out.println("✅ 재고 초과 수량 변경 방지 성공");
            }

            // 0 이하 수량 변경 시도
            try {
                service.updateCartItemQuantity(userId, "PRD003", 0);
                assert false : "잘못된 수량 체크 실패";
            } catch (IllegalArgumentException e) {
                System.out.println("✅ 잘못된 수량 변경 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 수량 변경 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 금액 계산 테스트
     */
    private static void testCartCalculation() {
        System.out.println("\n[TEST] 장바구니 금액 계산 테스트");

        try {
            String userId = "testuser5";

            // 여러 상품 추가
            service.addToCart(userId, "PRD001", 1);  // 1,500,000원
            service.addToCart(userId, "PRD002", 2);  // 30,000 * 2 = 60,000원
            service.addToCart(userId, "PRD003", 1);  // 120,000원

            // 총 금액 계산
            double total = service.calculateCartTotal(userId);
            double expected = 1500000 + 60000 + 120000;
            assert total == expected : "총 금액 계산 실패";
            System.out.println("✅ 총 금액 계산 성공: " + expected + "원");

            // 총 수량 계산
            int totalQuantity = service.getCartTotalQuantity(userId);
            assert totalQuantity == 4 : "총 수량 계산 실패";
            System.out.println("✅ 총 수량 계산 성공: 4개");

            // 빈 장바구니 계산
            service.clearCart(userId);
            double emptyTotal = service.calculateCartTotal(userId);
            assert emptyTotal == 0 : "빈 장바구니 금액 계산 실패";
            System.out.println("✅ 빈 장바구니 금액 계산 성공: 0원");

        } catch (Exception e) {
            System.err.println("❌ 금액 계산 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 유효성 검증 테스트
     */
    private static void testCartValidation() {
        System.out.println("\n[TEST] 장바구니 유효성 검증 테스트");

        try {
            String userId = "testuser6";

            // 정상 상품 추가
            service.addToCart(userId, "PRD001", 2);
            service.addToCart(userId, "PRD002", 3);

            // 유효성 검증
            CartService.CartValidationResult result = service.validateCart(userId);
            assert result.isValid() : "정상 장바구니 검증 실패";
            System.out.println("✅ 정상 장바구니 검증 성공");

            // 재고 부족 상황 시뮬레이션
            Product product = productRepository.findById("PRD001");
            product.setStock(1); // 재고를 1개로 변경
            productRepository.update(product);

            // 재검증
            CartService.CartValidationResult invalidResult = service.validateCart(userId);
            assert !invalidResult.isValid() : "재고 부족 검증 실패";
            assert !invalidResult.getIssues().isEmpty() : "문제 목록이 비어있음";
            System.out.println("✅ 재고 부족 상황 검증 성공");
            System.out.println("   문제: " + invalidResult.getIssues().get(0));

        } catch (Exception e) {
            System.err.println("❌ 유효성 검증 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}