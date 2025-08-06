package javaproject.controller;

import javaproject.domain.*;
import javaproject.service.*;
import javaproject.util.SessionManager;
import javaproject.util.InputValidator;
import javaproject.exception.*;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 주문 관리 컨트롤러
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 주문 생성, 조회, 장바구니 관리 등의 기능 제공
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderController {

    // 싱글톤 인스턴스
    private static final OrderController instance = new OrderController();

    // 서비스 레이어 인스턴스들
    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;

    // 유틸리티 인스턴스들
    private final SessionManager sessionManager;
    private final InputValidator inputValidator;

    // 스캐너 객체
    private final Scanner scanner;

    // 날짜 포맷터
    private final DateTimeFormatter dateFormatter;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private OrderController() {
        this.orderService = OrderService.getInstance();
        this.cartService = CartService.getInstance();
        this.productService = ProductService.getInstance();
        this.sessionManager = SessionManager.getInstance();
        this.inputValidator = InputValidator.getInstance();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return OrderController의 유일한 인스턴스
     */
    public static OrderController getInstance() {
        return instance;
    }

    /**
     * 주문 관리 메인 메뉴 표시 및 처리
     */
    public void showOrderMenu() {
        // 로그인 확인 - 인스턴스 메서드로 호출
        if (!sessionManager.isLoggedIn()) {
            System.out.println("\n⚠️ 로그인이 필요한 서비스입니다.");
            return;
        }

        while (true) {
            try {
                printOrderMenu();
                System.out.print("메뉴를 선택하세요: ");
                String input = scanner.nextLine().trim();

                int choice = inputValidator.validateMenuChoice(input, 1, 6);

                switch (choice) {
                    case 1:
                        viewCart();
                        break;
                    case 2:
                        addToCart();
                        break;
                    case 3:
                        removeFromCart();
                        break;
                    case 4:
                        createOrder();
                        break;
                    case 5:
                        viewMyOrders();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }

    /**
     * 주문 메뉴 출력
     */
    private void printOrderMenu() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         📦 주문 관리                │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 장바구니 보기                  │");
        System.out.println("│  2. 장바구니에 상품 추가           │");
        System.out.println("│  3. 장바구니에서 상품 삭제         │");
        System.out.println("│  4. 주문하기                       │");
        System.out.println("│  5. 내 주문 내역 보기              │");
        System.out.println("│  6. 이전 메뉴로                    │");
        System.out.println("└────────────────────────────────────┘");
    }

    /**
     * 장바구니 조회
     */
    public void viewCart() {
        try {
            // 현재 사용자의 장바구니 가져오기
            String userId = sessionManager.getCurrentUserId();
            Cart cart = cartService.getCartByUserId(userId);

            if (cart == null || cart.getItems().isEmpty()) {
                System.out.println("\n📭 장바구니가 비어있습니다.");
                return;
            }

            // 장바구니 내용 출력
            System.out.println("\n🛒 장바구니 내용");
            System.out.println("=" .repeat(60));

            double totalAmount = 0;
            int index = 1;

            for (CartItem item : cart.getItems()) {
                Product product = productService.getProductById(item.getProductId());
                if (product != null) {
                    double subtotal = product.getPrice() * item.getQuantity();
                    totalAmount += subtotal;

                    System.out.printf("%d. %s\n", index++, product.getName());
                    System.out.printf("   가격: %,d원 | 수량: %d개 | 소계: %,.0f원\n",
                            (int)product.getPrice(), item.getQuantity(), subtotal);
                    System.out.println("-" .repeat(60));
                }
            }

            System.out.printf("\n💰 총 금액: %,.0f원\n", totalAmount);

        } catch (Exception e) {
            System.out.println("❌ 장바구니 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 장바구니에 상품 추가
     */
    public void addToCart() {
        try {
            // 상품 목록 표시
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("\n📭 등록된 상품이 없습니다.");
                return;
            }

            System.out.println("\n📋 상품 목록");
            System.out.println("=" .repeat(60));
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                System.out.printf("%d. %s - %,d원 (재고: %d개)\n",
                        i + 1, p.getName(), (int)p.getPrice(), p.getStock());
            }

            // 상품 선택
            System.out.print("\n장바구니에 추가할 상품 번호: ");
            String productChoice = scanner.nextLine();
            int productIndex = inputValidator.validateMenuChoice(
                    productChoice, 1, products.size()) - 1;
            Product selectedProduct = products.get(productIndex);

            // 수량 입력
            System.out.print("수량을 입력하세요: ");
            String quantityStr = scanner.nextLine();
            int quantity = inputValidator.validateQuantity(quantityStr);

            // 재고 확인
            if (quantity > selectedProduct.getStock()) {
                System.out.println("❌ 재고가 부족합니다. (현재 재고: " +
                        selectedProduct.getStock() + "개)");
                return;
            }

            // 장바구니에 추가
            String userId = sessionManager.getCurrentUserId();
            cartService.addToCart(userId, selectedProduct.getId(), quantity);

            System.out.println("✅ 장바구니에 상품을 추가했습니다.");

        } catch (Exception e) {
            System.out.println("❌ 상품 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 장바구니에서 상품 삭제
     */
    public void removeFromCart() {
        try {
            // 장바구니 조회
            String userId = sessionManager.getCurrentUserId();
            Cart cart = cartService.getCartByUserId(userId);

            if (cart == null || cart.getItems().isEmpty()) {
                System.out.println("\n📭 장바구니가 비어있습니다.");
                return;
            }

            // 장바구니 아이템 표시
            System.out.println("\n🛒 장바구니 상품");
            System.out.println("=" .repeat(60));

            List<CartItem> items = cart.getItems();
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                Product product = productService.getProductById(item.getProductId());
                if (product != null) {
                    System.out.printf("%d. %s (수량: %d개)\n",
                            i + 1, product.getName(), item.getQuantity());
                }
            }

            // 삭제할 상품 선택
            System.out.print("\n삭제할 상품 번호 (0: 취소): ");
            String choice = scanner.nextLine();

            if ("0".equals(choice.trim())) {
                System.out.println("삭제를 취소했습니다.");
                return;
            }

            int itemIndex = inputValidator.validateMenuChoice(choice, 1, items.size()) - 1;
            CartItem itemToRemove = items.get(itemIndex);

            // 삭제
            cartService.removeFromCart(userId, itemToRemove.getProductId());
            System.out.println("✅ 장바구니에서 상품을 삭제했습니다.");

        } catch (Exception e) {
            System.out.println("❌ 상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 장바구니 상품 수량 변경
     */
    public void updateCartItemQuantity() {
        try {
            // 장바구니 조회
            String userId = sessionManager.getCurrentUserId();
            Cart cart = cartService.getCartByUserId(userId);

            if (cart == null || cart.getItems().isEmpty()) {
                System.out.println("\n📭 장바구니가 비어있습니다.");
                return;
            }

            // 장바구니 아이템 표시
            System.out.println("\n🛒 장바구니 상품");
            System.out.println("=" .repeat(60));

            List<CartItem> items = cart.getItems();
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                Product product = productService.getProductById(item.getProductId());
                if (product != null) {
                    System.out.printf("%d. %s (현재 수량: %d개, 재고: %d개)\n",
                            i + 1, product.getName(), item.getQuantity(), product.getStock());
                }
            }

            // 수정할 상품 선택
            System.out.print("\n수량을 변경할 상품 번호 (0: 취소): ");
            String choice = scanner.nextLine();

            if ("0".equals(choice.trim())) {
                System.out.println("수량 변경을 취소했습니다.");
                return;
            }

            int itemIndex = inputValidator.validateMenuChoice(choice, 1, items.size()) - 1;
            CartItem itemToUpdate = items.get(itemIndex);
            Product product = productService.getProductById(itemToUpdate.getProductId());

            // 새 수량 입력
            System.out.print("새로운 수량을 입력하세요: ");
            String quantityStr = scanner.nextLine();
            int newQuantity = inputValidator.validateQuantity(quantityStr);

            // 재고 확인
            if (newQuantity > product.getStock()) {
                System.out.println("❌ 재고가 부족합니다. (현재 재고: " +
                        product.getStock() + "개)");
                return;
            }

            // 수량 업데이트
            cartService.updateCartItemQuantity(userId, itemToUpdate.getProductId(), newQuantity);
            System.out.println("✅ 수량을 변경했습니다.");

        } catch (Exception e) {
            System.out.println("❌ 수량 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 주문 생성
     */
    public void createOrder() {
        try {
            // 장바구니 확인
            String userId = sessionManager.getCurrentUserId();
            Cart cart = cartService.getCartByUserId(userId);

            if (cart == null || cart.getItems().isEmpty()) {
                System.out.println("\n📭 장바구니가 비어있습니다. 먼저 상품을 담아주세요.");
                return;
            }

            // 장바구니 내용 표시
            viewCart();

            // 주문 확인
            System.out.print("\n이대로 주문하시겠습니까? (Y/N): ");
            String confirm = scanner.nextLine();

            if (!inputValidator.validateYesNo(confirm)) {
                System.out.println("주문을 취소했습니다.");
                return;
            }

            // 배송 주소 입력
            System.out.print("배송 주소를 입력하세요: ");
            String shippingAddress = scanner.nextLine();
            shippingAddress = inputValidator.validateAddress(shippingAddress);

            // 주문 생성
            Order order = orderService.createOrderFromCart(userId, shippingAddress);

            // 장바구니 비우기
            cartService.clearCart(userId);

            // 주문 완료 메시지
            System.out.println("\n✅ 주문이 완료되었습니다!");
            System.out.println("주문 번호: " + order.getId());
            System.out.println("주문 일시: " + order.getOrderDate().format(dateFormatter));
            System.out.printf("총 결제 금액: %,.0f원\n", order.getTotalAmount());
            System.out.println("배송 주소: " + order.getShippingAddress());

        } catch (InsufficientStockException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 주문 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 내 주문 내역 조회
     */
    public void viewMyOrders() {
        try {
            // 현재 사용자의 주문 조회
            String userId = sessionManager.getCurrentUserId();
            List<Order> orders = orderService.getOrdersByUserId(userId);

            if (orders.isEmpty()) {
                System.out.println("\n📭 주문 내역이 없습니다.");
                return;
            }

            // 주문 목록 출력
            System.out.println("\n📋 주문 내역");
            System.out.println("=" .repeat(80));

            for (Order order : orders) {
                System.out.printf("\n주문번호: %s | 주문일시: %s | 상태: %s\n",
                        order.getId(),
                        order.getOrderDate().format(dateFormatter),
                        order.getStatus());
                System.out.printf("배송지: %s\n", order.getShippingAddress());

                // 주문 상품 목록
                System.out.println("주문 상품:");
                for (OrderItem item : order.getItems()) {
                    Product product = productService.getProductById(item.getProductId());
                    if (product != null) {
                        System.out.printf("  - %s: %d개 × %,d원 = %,.0f원\n",
                                product.getName(),
                                item.getQuantity(),
                                (int)item.getPrice(),
                                item.getQuantity() * item.getPrice());
                    }
                }

                System.out.printf("총 금액: %,.0f원\n", order.getTotalAmount());
                System.out.println("-" .repeat(80));
            }

            // 상세 조회 옵션
            System.out.print("\n특정 주문의 상세 정보를 보시겠습니까? (Y/N): ");
            String viewDetail = scanner.nextLine();

            if (inputValidator.validateYesNo(viewDetail)) {
                viewOrderDetail();
            }

        } catch (Exception e) {
            System.out.println("❌ 주문 내역 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 주문 상세 조회
     */
    private void viewOrderDetail() {
        try {
            System.out.print("조회할 주문 번호를 입력하세요: ");
            String orderId = scanner.nextLine().trim();

            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                System.out.println("❌ 해당 주문을 찾을 수 없습니다.");
                return;
            }

            // 권한 확인 (본인 주문인지)
            if (!order.getUserId().equals(sessionManager.getCurrentUserId()) &&
                    !sessionManager.isAdmin()) {
                System.out.println("❌ 해당 주문을 조회할 권한이 없습니다.");
                return;
            }

            // 상세 정보 출력
            System.out.println("\n📦 주문 상세 정보");
            System.out.println("=" .repeat(60));
            System.out.println("주문 번호: " + order.getId());
            System.out.println("주문자 ID: " + order.getUserId());
            System.out.println("주문 일시: " + order.getOrderDate().format(dateFormatter));
            System.out.println("주문 상태: " + order.getStatus());
            System.out.println("배송 주소: " + order.getShippingAddress());

            if (order.getPaymentDate() != null) {
                System.out.println("결제 일시: " +
                        order.getPaymentDate().format(dateFormatter));
            }

            if (order.getShippingDate() != null) {
                System.out.println("배송 시작일: " +
                        order.getShippingDate().format(dateFormatter));
            }

            if (order.getDeliveryDate() != null) {
                System.out.println("배송 완료일: " +
                        order.getDeliveryDate().format(dateFormatter));
            }

            System.out.println("\n📦 주문 상품");
            System.out.println("-" .repeat(60));

            for (OrderItem item : order.getItems()) {
                Product product = productService.getProductById(item.getProductId());
                System.out.printf("상품명: %s\n",
                        product != null ? product.getName() : "삭제된 상품");
                System.out.printf("  단가: %,d원 | 수량: %d개 | 소계: %,.0f원\n",
                        (int)item.getPrice(),
                        item.getQuantity(),
                        item.getPrice() * item.getQuantity());
            }

            System.out.println("-" .repeat(60));
            System.out.printf("총 결제 금액: %,.0f원\n", order.getTotalAmount());

        } catch (Exception e) {
            System.out.println("❌ 주문 상세 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 관리자용: 모든 주문 조회
     */
    public void viewAllOrders() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        try {
            List<Order> orders = orderService.getAllOrders();

            if (orders.isEmpty()) {
                System.out.println("\n📭 주문이 없습니다.");
                return;
            }

            System.out.println("\n📋 전체 주문 목록");
            System.out.println("=" .repeat(80));

            for (Order order : orders) {
                System.out.printf("주문번호: %s | 사용자: %s | 일시: %s | 상태: %s | 금액: %,.0f원\n",
                        order.getId(),
                        order.getUserId(),
                        order.getOrderDate().format(dateFormatter),
                        order.getStatus(),
                        order.getTotalAmount());
            }

        } catch (Exception e) {
            System.out.println("❌ 주문 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 관리자용: 주문 상태 변경
     */
    public void updateOrderStatus() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        try {
            System.out.print("상태를 변경할 주문 번호: ");
            String orderId = scanner.nextLine().trim();

            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                System.out.println("❌ 해당 주문을 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n현재 상태: " + order.getStatus());
            System.out.println("\n변경 가능한 상태:");
            System.out.println("1. PENDING (주문 대기)");
            System.out.println("2. PAID (결제 완료)");
            System.out.println("3. SHIPPING (배송 중)");
            System.out.println("4. DELIVERED (배송 완료)");
            System.out.println("5. CANCELLED (주문 취소)");

            System.out.print("\n새로운 상태 번호: ");
            String statusChoice = scanner.nextLine();
            int choice = inputValidator.validateMenuChoice(statusChoice, 1, 5);

            OrderStatus newStatus;
            switch (choice) {
                case 1: newStatus = OrderStatus.PENDING; break;
                case 2: newStatus = OrderStatus.PAID; break;
                case 3: newStatus = OrderStatus.SHIPPING; break;
                case 4: newStatus = OrderStatus.DELIVERED; break;
                case 5: newStatus = OrderStatus.CANCELLED; break;
                default: return;
            }

            orderService.updateOrderStatus(orderId, newStatus);
            System.out.println("✅ 주문 상태가 변경되었습니다.");

        } catch (Exception e) {
            System.out.println("❌ 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}