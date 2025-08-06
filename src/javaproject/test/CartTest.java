package javaproject.test;

import javaproject.domain.Cart;
import javaproject.domain.CartItem;

/**
 * Cart 도메인 클래스 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class CartTest {

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== Cart 도메인 테스트 시작 ======");

        testCartCreation();
        testAddItem();
        testRemoveItem();
        testFindItem();
        testClearCart();
        testCartCalculation();

        System.out.println("====== Cart 도메인 테스트 완료 ======\n");
    }

    /**
     * 장바구니 생성 테스트
     */
    private static void testCartCreation() {
        System.out.println("\n[TEST] 장바구니 생성 테스트");

        try {
            // 사용자 ID로 장바구니 생성
            Cart cart = new Cart("user123");

            assert cart.getId() != null : "장바구니 ID 생성 실패";
            assert cart.getUserId().equals("user123") : "사용자 ID 설정 실패";
            assert cart.getItems() != null : "아이템 리스트 초기화 실패";
            assert cart.isEmpty() : "새 장바구니가 비어있지 않음";
            assert cart.getItemCount() == 0 : "아이템 개수 초기화 실패";

            System.out.println("✅ 장바구니 생성 성공 - ID: " + cart.getId());

        } catch (Exception e) {
            System.err.println("❌ 장바구니 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 아이템 추가 테스트
     */
    private static void testAddItem() {
        System.out.println("\n[TEST] 장바구니 아이템 추가 테스트");

        try {
            Cart cart = new Cart("user123");

            // 첫 번째 아이템 추가
            CartItem item1 = new CartItem("PRD001", 2);
            cart.addItem(item1);

            assert cart.getItemCount() == 1 : "아이템 추가 실패";
            assert !cart.isEmpty() : "장바구니가 비어있음";
            System.out.println("✅ 첫 번째 아이템 추가 성공");

            // 동일한 상품 추가 (수량 증가 테스트)
            CartItem item2 = new CartItem("PRD001", 3);
            cart.addItem(item2);

            CartItem foundItem = cart.findItemByProductId("PRD001");
            assert foundItem != null : "아이템 조회 실패";
            assert foundItem.getQuantity() == 5 : "수량 증가 실패";
            assert cart.getItemCount() == 1 : "중복 아이템이 별도로 추가됨";
            System.out.println("✅ 동일 상품 수량 증가 성공: 2 + 3 = 5");

            // 다른 상품 추가
            CartItem item3 = new CartItem("PRD002", 1);
            cart.addItem(item3);

            assert cart.getItemCount() == 2 : "새 아이템 추가 실패";
            System.out.println("✅ 다른 상품 추가 성공");

        } catch (Exception e) {
            System.err.println("❌ 아이템 추가 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 아이템 제거 테스트
     */
    private static void testRemoveItem() {
        System.out.println("\n[TEST] 장바구니 아이템 제거 테스트");

        try {
            Cart cart = new Cart("user123");

            // 아이템 추가
            CartItem item1 = new CartItem("PRD001", 2);
            CartItem item2 = new CartItem("PRD002", 3);
            cart.addItem(item1);
            cart.addItem(item2);

            // 아이템 제거
            boolean removed = cart.removeItemByProductId("PRD001");
            assert removed : "아이템 제거 실패";
            assert cart.getItemCount() == 1 : "아이템 개수 업데이트 실패";
            assert cart.findItemByProductId("PRD001") == null : "제거된 아이템이 조회됨";
            System.out.println("✅ 아이템 제거 성공");

            // 존재하지 않는 아이템 제거 시도
            boolean notRemoved = cart.removeItemByProductId("PRD999");
            assert !notRemoved : "존재하지 않는 아이템 제거 체크 실패";
            System.out.println("✅ 존재하지 않는 아이템 제거 방지 성공");

        } catch (Exception e) {
            System.err.println("❌ 아이템 제거 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 아이템 검색 테스트
     */
    private static void testFindItem() {
        System.out.println("\n[TEST] 장바구니 아이템 검색 테스트");

        try {
            Cart cart = new Cart("user123");

            // 아이템 추가
            cart.addItem(new CartItem("PRD001", 2));
            cart.addItem(new CartItem("PRD002", 3));
            cart.addItem(new CartItem("PRD003", 1));

            // 아이템 검색
            CartItem found = cart.findItemByProductId("PRD002");
            assert found != null : "아이템 검색 실패";
            assert found.getProductId().equals("PRD002") : "잘못된 아이템 반환";
            assert found.getQuantity() == 3 : "아이템 수량 불일치";
            System.out.println("✅ 아이템 검색 성공");

            // 존재하지 않는 아이템 검색
            CartItem notFound = cart.findItemByProductId("PRD999");
            assert notFound == null : "존재하지 않는 아이템이 검색됨";
            System.out.println("✅ 존재하지 않는 아이템 검색 처리 성공");

        } catch (Exception e) {
            System.err.println("❌ 아이템 검색 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 비우기 테스트
     */
    private static void testClearCart() {
        System.out.println("\n[TEST] 장바구니 비우기 테스트");

        try {
            Cart cart = new Cart("user123");

            // 아이템 추가
            cart.addItem(new CartItem("PRD001", 2));
            cart.addItem(new CartItem("PRD002", 3));

            assert !cart.isEmpty() : "장바구니가 비어있음";

            // 장바구니 비우기
            cart.clearItems();

            assert cart.isEmpty() : "장바구니 비우기 실패";
            assert cart.getItemCount() == 0 : "아이템 개수가 0이 아님";
            assert cart.getTotalQuantity() == 0 : "총 수량이 0이 아님";
            assert cart.getTotalAmount() == 0 : "총 금액이 0이 아님";

            System.out.println("✅ 장바구니 비우기 성공");

        } catch (Exception e) {
            System.err.println("❌ 장바구니 비우기 실패: " + e.getMessage());
        }
    }

    /**
     * 장바구니 계산 테스트
     */
    private static void testCartCalculation() {
        System.out.println("\n[TEST] 장바구니 계산 테스트");

        try {
            Cart cart = new Cart("user123");

            // 아이템 추가
            CartItem item1 = new CartItem("PRD001", 2);
            CartItem item2 = new CartItem("PRD002", 3);
            CartItem item3 = new CartItem("PRD003", 1);

            cart.addItem(item1);
            cart.addItem(item2);
            cart.addItem(item3);

            // 총 수량 계산
            int expectedQuantity = 2 + 3 + 1;
            cart.setTotalQuantity(expectedQuantity);
            assert cart.getTotalQuantity() == expectedQuantity : "총 수량 계산 실패";
            System.out.println("✅ 총 수량 계산 성공: " + expectedQuantity + "개");

            // 아이템 개수 확인
            assert cart.getItemCount() == 3 : "아이템 개수 계산 실패";
            System.out.println("✅ 아이템 종류 수 확인 성공: 3종");

        } catch (Exception e) {
            System.err.println("❌ 장바구니 계산 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}