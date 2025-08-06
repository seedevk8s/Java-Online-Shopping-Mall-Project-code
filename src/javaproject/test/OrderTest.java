package javaproject.test;

import javaproject.domain.Order;
import javaproject.domain.OrderItem;
import javaproject.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order 도메인 클래스 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderTest {

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== Order 도메인 테스트 시작 ======");

        testOrderCreation();
        testAddOrderItem();
        testOrderStatusUpdate();
        testOrderCancellation();
        testOrderCalculation();

        System.out.println("====== Order 도메인 테스트 완료 ======\n");
    }

    /**
     * 주문 생성 테스트
     */
    private static void testOrderCreation() {
        System.out.println("\n[TEST] 주문 생성 테스트");

        try {
            Order order = new Order("user123", "서울시 강남구");

            assert order.getUserId().equals("user123") : "사용자 ID 설정 실패";
            assert order.getShippingAddress().equals("서울시 강남구") : "배송주소 설정 실패";
            assert order.getStatus() == OrderStatus.PENDING : "초기 상태 설정 실패";
            assert order.getOrderDate() != null : "주문일시 설정 실패";
            assert order.getItems() != null : "주문 항목 리스트 초기화 실패";

            System.out.println("✅ 주문 생성 성공");

        } catch (Exception e) {
            System.err.println("❌ 주문 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 항목 추가 테스트
     */
    private static void testAddOrderItem() {
        System.out.println("\n[TEST] 주문 항목 추가 테스트");

        try {
            Order order = new Order("user123", "서울시 강남구");

            // 주문 항목 추가
            OrderItem item1 = new OrderItem(order.getId(), "PRD001", 2, 10000);
            OrderItem item2 = new OrderItem(order.getId(), "PRD002", 3, 15000);

            order.addItem(item1);
            order.addItem(item2);

            assert order.getItems().size() == 2 : "주문 항목 추가 실패";

            // 총 금액 계산 확인
            double expectedTotal = (2 * 10000) + (3 * 15000);
            assert order.getTotalAmount() == expectedTotal : "총 금액 계산 실패";

            System.out.println("✅ 주문 항목 추가 성공");
            System.out.println("✅ 총 금액 계산 성공: " + expectedTotal + "원");

        } catch (Exception e) {
            System.err.println("❌ 주문 항목 추가 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 상태 업데이트 테스트
     */
    private static void testOrderStatusUpdate() {
        System.out.println("\n[TEST] 주문 상태 업데이트 테스트");

        try {
            Order order = new Order("user123", "서울시 강남구");

            // PENDING → PAID
            order.updateStatus(OrderStatus.PAID);
            assert order.getStatus() == OrderStatus.PAID : "결제 완료 상태 변경 실패";
            assert order.getPaymentDate() != null : "결제일시 설정 실패";
            System.out.println("✅ PENDING → PAID 상태 변경 성공");

            // PAID → SHIPPING
            order.updateStatus(OrderStatus.SHIPPING);
            assert order.getStatus() == OrderStatus.SHIPPING : "배송중 상태 변경 실패";
            assert order.getShippingDate() != null : "배송일시 설정 실패";
            System.out.println("✅ PAID → SHIPPING 상태 변경 성공");

            // SHIPPING → DELIVERED
            order.updateStatus(OrderStatus.DELIVERED);
            assert order.getStatus() == OrderStatus.DELIVERED : "배송완료 상태 변경 실패";
            assert order.getDeliveryDate() != null : "배송완료일시 설정 실패";
            System.out.println("✅ SHIPPING → DELIVERED 상태 변경 성공");

        } catch (Exception e) {
            System.err.println("❌ 주문 상태 업데이트 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 취소 가능 여부 테스트
     */
    private static void testOrderCancellation() {
        System.out.println("\n[TEST] 주문 취소 가능 여부 테스트");

        try {
            // PENDING 상태 주문
            Order pendingOrder = new Order("user123", "서울시");
            assert pendingOrder.isCancellable() : "PENDING 상태 취소 가능 체크 실패";
            System.out.println("✅ PENDING 상태 취소 가능 확인");

            // PAID 상태 주문
            Order paidOrder = new Order("user123", "서울시");
            paidOrder.updateStatus(OrderStatus.PAID);
            assert paidOrder.isCancellable() : "PAID 상태 취소 가능 체크 실패";
            System.out.println("✅ PAID 상태 취소 가능 확인");

            // SHIPPING 상태 주문
            Order shippingOrder = new Order("user123", "서울시");
            shippingOrder.setStatus(OrderStatus.SHIPPING);
            assert !shippingOrder.isCancellable() : "SHIPPING 상태 취소 불가 체크 실패";
            System.out.println("✅ SHIPPING 상태 취소 불가 확인");

            // DELIVERED 상태 주문
            Order deliveredOrder = new Order("user123", "서울시");
            deliveredOrder.setStatus(OrderStatus.DELIVERED);
            assert !deliveredOrder.isCancellable() : "DELIVERED 상태 취소 불가 체크 실패";
            assert deliveredOrder.isCompleted() : "주문 완료 상태 체크 실패";
            System.out.println("✅ DELIVERED 상태 취소 불가 확인");

        } catch (Exception e) {
            System.err.println("❌ 주문 취소 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 주문 금액 계산 테스트
     */
    private static void testOrderCalculation() {
        System.out.println("\n[TEST] 주문 금액 계산 테스트");

        try {
            Order order = new Order("user123", "서울시");

            // 여러 주문 항목 추가
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(order.getId(), "PRD001", 2, 10000));  // 20,000
            items.add(new OrderItem(order.getId(), "PRD002", 3, 15000));  // 45,000
            items.add(new OrderItem(order.getId(), "PRD003", 1, 25000));  // 25,000

            for (OrderItem item : items) {
                order.addItem(item);
            }

            // 총 금액 확인
            double expectedTotal = 20000 + 45000 + 25000;
            assert order.getTotalAmount() == expectedTotal : "총 금액 계산 실패";
            System.out.println("✅ 총 금액 계산 성공: " + expectedTotal + "원");

            // 주문 항목 제거 테스트
            OrderItem firstItem = items.get(0);
            order.removeItem(firstItem);

            double newExpectedTotal = 45000 + 25000;
            assert order.getTotalAmount() == newExpectedTotal : "항목 제거 후 금액 재계산 실패";
            System.out.println("✅ 항목 제거 후 금액 재계산 성공: " + newExpectedTotal + "원");

        } catch (Exception e) {
            System.err.println("❌ 주문 계산 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}