package javaproject.test;

import javaproject.domain.*;
import javaproject.service.*;
import javaproject.repository.*;
import javaproject.exception.*;

import java.util.List;

/**
 * OrderService 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderServiceTest {

    private static OrderService orderService;
    private static CartService cartService;
    private static ProductRepository productRepository;
    private static UserRepository userRepository;
    private static OrderRepository orderRepository;
    private static CartRepository cartRepository;

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== OrderService 테스트 시작 ======");

        // 서비스 및 레포지토리 초기화
        orderService = OrderService.getInstance();
        cartService = CartService.getInstance();
        productRepository = ProductRepository.getInstance();
        userRepository = UserRepository.getInstance();
        orderRepository = OrderRepository.getInstance();
        cartRepository = CartRepository.getInstance();

        // 초기화
        cleanupData();
        setupTestData();

        // 테스트 실행
        testCreateOrderFromCart();
        testCreateDirectOrder();
        testOrderStatusUpdate();
        testOrderCancellation();
        testGetOrdersByUserId();
        testOrderStatistics();

        // 정리
        cleanupData();

        System.out.println("====== OrderService 테스트 완료 ======\n");
    }

    /**
     * 테스트 데이터 초기화
     */
    private static void cleanupData() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * 테스트용 데이터 준비
     */
    private static void setupTestData() {
        // 사용자 생성
        User user1 = new User("orderuser1", "pass123", "주문테스트1",
                "order1@test.com", "010-1111-1111", "서울시", false);
        User user2 = new User("orderuser2", "pass123", "주문테스트2",
                "order2@test.com", "010-2222-2222", "부산시", false);
        userRepository.save(user1);
        userRepository.save(user2);

        // 상품 생성
        productRepository.save(new Product("ORD_PRD001", "노트북", "고성능 노트북",
                1500000, 10, "전자제품"));
        productRepository.save(new Product("ORD_PRD002", "마우스", "무선 마우스",
                30000, 50, "전자제품"));
        productRepository.save(new Product("ORD_PRD003", "키보드", "기계식 키보드",
                120000, 5, "전자제품"));
        productRepository.save(new Product("ORD_PRD004", "모니터", "4K 모니터",
                500000, 2, "전자제품"));
    }

    /**
     * 장바구니에서 주문 생성 테스트
     */
    private static void testCreateOrderFromCart() {
        System.out.println("\n[TEST] 장바구니에서 주문 생성 테스트");

        try {
            String userId = "orderuser1";

            // 장바구니에 상품 추가
            cartService.addToCart(userId, "ORD_PRD001", 1);
            cartService.addToCart(userId, "ORD_PRD002", 2);

            // 주문 생성
            Order order = orderService.createOrderFromCart(userId, "서울시 강남구");

            assert order != null : "주문 생성 실패";
            assert order.getUserId().equals(userId) : "사용자 ID 불일치";
            assert order.getItems().size() == 2 : "주문 항목 수 불일치";
            assert order.getStatus() == OrderStatus.PENDING : "초기 상태 오류";

            // 총 금액 확인
            double expectedTotal = 1500000 + (30000 * 2);
            assert order.getTotalAmount() == expectedTotal : "총 금액 계산 오류";

            // 재고 차감 확인
            Product product1 = productRepository.findById("ORD_PRD001");
            assert product1.getStock() == 9 : "재고 차감 실패";

            System.out.println("✅ 장바구니에서 주문 생성 성공");
            System.out.println("   주문 ID: " + order.getId());
            System.out.println("   총 금액: " + order.getTotalAmount() + "원");

            // 빈 장바구니로 주문 시도
            try {
                orderService.createOrderFromCart(userId, "서울시");
                assert false : "빈 장바구니 체크 실패";
            } catch (EmptyCartException e) {
                System.out.println("✅ 빈 장바구니 주문 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 장바구니 주문 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 직접 주문 생성 테스트
     */
    private static void testCreateDirectOrder() {
        System.out.println("\n[TEST] 직접 주문 생성 테스트");

        try {
            String userId = "orderuser2";

            // 직접 주문 생성
            Order order = orderService.createDirectOrder(
                    userId, "ORD_PRD003", 2, "부산시 해운대구");

            assert order != null : "직접 주문 생성 실패";
            assert order.getItems().size() == 1 : "주문 항목 수 오류";
            assert order.getTotalAmount() == 120000 * 2 : "총 금액 계산 오류";

            System.out.println("✅ 직접 주문 생성 성공");

            // 재고 초과 주문 시도
            try {
                orderService.createDirectOrder(userId, "ORD_PRD004", 5, "주소");
                assert false : "재고 초과 체크 실패";
            } catch (InsufficientStockException e) {
                System.out.println("✅ 재고 초과 주문 방지 성공");
            }

            // 존재하지 않는 상품 주문 시도
            try {
                orderService.createDirectOrder(userId, "NOTEXIST", 1, "주소");
                assert false : "존재하지 않는 상품 체크 실패";
            } catch (ProductNotFoundException e) {
                System.out.println("✅ 존재하지 않는 상품 주문 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 직접 주문 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 상태 변경 테스트
     */
    private static void testOrderStatusUpdate() {
        System.out.println("\n[TEST] 주문 상태 변경 테스트");

        try {
            // 테스트용 주문 생성
            Order order = orderService.createDirectOrder(
                    "orderuser1", "ORD_PRD002", 1, "서울시");

            String orderId = order.getId();

            // PENDING → PAID
            orderService.updateOrderStatus(orderId, OrderStatus.PAID);
            Order updated = orderService.getOrderById(orderId);
            assert updated.getStatus() == OrderStatus.PAID : "PAID 상태 변경 실패";
            assert updated.getPaymentDate() != null : "결제일시 설정 실패";
            System.out.println("✅ PENDING → PAID 상태 변경 성공");

            // PAID → SHIPPING
            orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING);
            updated = orderService.getOrderById(orderId);
            assert updated.getStatus() == OrderStatus.SHIPPING : "SHIPPING 상태 변경 실패";
            System.out.println("✅ PAID → SHIPPING 상태 변경 성공");

            // 잘못된 상태 전환 시도 (SHIPPING → PENDING)
            try {
                orderService.updateOrderStatus(orderId, OrderStatus.PENDING);
                assert false : "잘못된 상태 전환 체크 실패";
            } catch (IllegalStateException e) {
                System.out.println("✅ 잘못된 상태 전환 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 주문 상태 변경 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 취소 테스트
     */
    private static void testOrderCancellation() {
        System.out.println("\n[TEST] 주문 취소 테스트");

        try {
            String userId = "orderuser1";

            // 취소 가능한 주문 생성 (PENDING)
            Order order1 = orderService.createDirectOrder(
                    userId, "ORD_PRD001", 1, "서울시");

            // 재고 확인
            Product product = productRepository.findById("ORD_PRD001");
            int stockBefore = product.getStock();

            // 주문 취소
            orderService.cancelOrder(order1.getId(), userId);
            Order cancelled = orderService.getOrderById(order1.getId());
            assert cancelled.getStatus() == OrderStatus.CANCELLED : "주문 취소 실패";

            // 재고 복구 확인
            product = productRepository.findById("ORD_PRD001");
            assert product.getStock() == stockBefore + 1 : "재고 복구 실패";
            System.out.println("✅ 주문 취소 및 재고 복구 성공");

            // 다른 사용자가 취소 시도
            try {
                Order order2 = orderService.createDirectOrder(
                        userId, "ORD_PRD002", 1, "서울시");
                orderService.cancelOrder(order2.getId(), "otheruser");
                assert false : "권한 체크 실패";
            } catch (UnauthorizedException e) {
                System.out.println("✅ 타인 주문 취소 방지 성공");
            }

            // 배송중인 주문 취소 시도
            Order order3 = orderService.createDirectOrder(
                    userId, "ORD_PRD003", 1, "서울시");
            orderService.updateOrderStatus(order3.getId(), OrderStatus.PAID);
            orderService.updateOrderStatus(order3.getId(), OrderStatus.SHIPPING);

            try {
                orderService.cancelOrder(order3.getId(), userId);
                assert false : "배송중 주문 취소 체크 실패";
            } catch (IllegalStateException e) {
                System.out.println("✅ 배송중 주문 취소 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 주문 취소 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자별 주문 조회 테스트
     */
    private static void testGetOrdersByUserId() {
        System.out.println("\n[TEST] 사용자별 주문 조회 테스트");

        try {
            String userId1 = "orderuser1";
            String userId2 = "orderuser2";

            // 각 사용자의 기존 주문 수 확인
            int user1OrdersBefore = orderService.getOrdersByUserId(userId1).size();
            int user2OrdersBefore = orderService.getOrdersByUserId(userId2).size();

            // 사용자2의 추가 주문 생성
            orderService.createDirectOrder(userId2, "ORD_PRD001", 1, "부산시");
            orderService.createDirectOrder(userId2, "ORD_PRD002", 2, "부산시");

            // 사용자별 주문 조회
            List<Order> user1Orders = orderService.getOrdersByUserId(userId1);
            List<Order> user2Orders = orderService.getOrdersByUserId(userId2);

            assert user1Orders.size() >= user1OrdersBefore : "사용자1 주문 조회 실패";
            assert user2Orders.size() == user2OrdersBefore + 2 : "사용자2 주문 조회 실패";

            // 각 주문이 올바른 사용자의 것인지 확인
            for (Order order : user1Orders) {
                assert order.getUserId().equals(userId1) : "잘못된 사용자 주문 포함";
            }

            System.out.println("✅ 사용자별 주문 조회 성공");
            System.out.println("   사용자1 주문: " + user1Orders.size() + "건");
            System.out.println("   사용자2 주문: " + user2Orders.size() + "건");

        } catch (Exception e) {
            System.err.println("❌ 사용자별 주문 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 통계 테스트
     */
    private static void testOrderStatistics() {
        System.out.println("\n[TEST] 주문 통계 테스트");

        try {
            // 전체 통계
            OrderService.OrderStatistics totalStats = orderService.getStatistics(null);
            assert totalStats.getTotalOrders() > 0 : "전체 주문 수 오류";
            System.out.println("✅ 전체 통계 조회 성공");
            System.out.println("   총 주문: " + totalStats.getTotalOrders() + "건");
            System.out.println("   진행중: " + totalStats.getPendingOrders() + "건");
            System.out.println("   완료: " + totalStats.getCompletedOrders() + "건");
            System.out.println("   취소: " + totalStats.getCancelledOrders() + "건");

            // 특정 사용자 통계
            String userId = "orderuser2";
            OrderService.OrderStatistics userStats = orderService.getStatistics(userId);
            assert userStats.getTotalOrders() >= 2 : "사용자 주문 수 오류";
            System.out.println("✅ 사용자별 통계 조회 성공");
            System.out.println("   사용자 주문: " + userStats.getTotalOrders() + "건");

        } catch (Exception e) {
            System.err.println("❌ 주문 통계 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}