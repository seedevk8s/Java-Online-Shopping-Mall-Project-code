package javaproject.service;

import javaproject.domain.*;
import javaproject.repository.*;
import javaproject.exception.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderService {

    // 싱글톤 인스턴스
    private static final OrderService instance = new OrderService();

    // Repository 인스턴스들
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
        this.orderItemRepository = OrderItemRepository.getInstance();
        this.cartRepository = CartRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return OrderService의 유일한 인스턴스
     */
    public static OrderService getInstance() {
        return instance;
    }

    /**
     * 장바구니에서 주문 생성
     * 장바구니의 모든 상품을 주문으로 변환
     *
     * @param userId 사용자 ID
     * @param shippingAddress 배송 주소
     * @return 생성된 주문
     * @throws InsufficientStockException 재고가 부족한 경우
     * @throws EmptyCartException 장바구니가 비어있는 경우
     */
    public Order createOrderFromCart(String userId, String shippingAddress)
            throws InsufficientStockException, EmptyCartException, UserNotFoundException, ProductNotFoundException {

        // 파라미터 검증
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("배송 주소를 입력해주세요.");
        }

        // 장바구니 조회
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("장바구니가 비어있습니다.");
        }

        // 사용자 확인
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        // 주문 생성
        Order order = new Order();
        order.setId(generateOrderId());
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);

        // 주문 항목 생성 및 재고 확인
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId());

            if (product == null) {
                throw new ProductNotFoundException(
                        "상품을 찾을 수 없습니다: " + cartItem.getProductId()
                );
            }

            // 재고 확인
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        String.format("'%s' 상품의 재고가 부족합니다. (재고: %d개, 요청: %d개)",
                                product.getName(), product.getStock(), cartItem.getQuantity())
                );
            }

            // OrderItem 생성
            OrderItem orderItem = new OrderItem();
            orderItem.setId(generateOrderItemId());
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();

            // 재고 감소
            product.reduceStock(cartItem.getQuantity());
            productRepository.update(product);
        }

        // 주문에 항목 추가
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 주문 저장
        orderRepository.save(order);

        // 주문 항목 저장
        for (OrderItem item : orderItems) {
            orderItemRepository.save(item);
        }

        return order;
    }

    /**
     * 직접 주문 생성 (장바구니를 거치지 않고)
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param quantity 수량
     * @param shippingAddress 배송 주소
     * @return 생성된 주문
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws InsufficientStockException 재고가 부족한 경우
     */
    public Order createDirectOrder(String userId, String productId, int quantity,
                                   String shippingAddress)
            throws ProductNotFoundException, InsufficientStockException {

        // 파라미터 검증
        validateOrderParameters(userId, productId, quantity, shippingAddress);

        // 상품 조회
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }

        // 재고 확인
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    String.format("재고가 부족합니다. (재고: %d개, 요청: %d개)",
                            product.getStock(), quantity)
            );
        }

        // 주문 생성
        Order order = new Order();
        order.setId(generateOrderId());
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(product.getPrice() * quantity);

        // 주문 항목 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setId(generateOrderItemId());
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);
        order.setItems(items);

        // 재고 감소
        product.reduceStock(quantity);
        productRepository.update(product);

        // 저장
        orderRepository.save(order);
        orderItemRepository.save(orderItem);

        return order;
    }

    /**
     * 주문 ID로 조회
     *
     * @param orderId 주문 ID
     * @return 조회된 주문
     * @throws OrderNotFoundException 주문을 찾을 수 없는 경우
     */
    public Order getOrderById(String orderId) throws OrderNotFoundException {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("주문 ID가 유효하지 않습니다.");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("주문을 찾을 수 없습니다: " + orderId);
        }

        // 주문 항목 로드
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        order.setItems(items);

        return order;
    }

    /**
     * 사용자의 모든 주문 조회
     *
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    public List<Order> getOrdersByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Order> orders = orderRepository.findByUserId(userId);

        // 각 주문의 항목 로드
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            order.setItems(items);
        }

        return orders;
    }

    /**
     * 모든 주문 조회 (관리자용)
     *
     * @return 전체 주문 목록
     */
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        // 각 주문의 항목 로드
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            order.setItems(items);
        }

        return orders;
    }

    /**
     * 주문 상태 변경
     *
     * @param orderId 주문 ID
     * @param newStatus 새로운 상태
     * @throws OrderNotFoundException 주문을 찾을 수 없는 경우
     */
    public void updateOrderStatus(String orderId, OrderStatus newStatus)
            throws OrderNotFoundException {

        Order order = getOrderById(orderId);

        // 상태 변경 유효성 검증
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    String.format("상태 변경이 불가능합니다: %s -> %s",
                            order.getStatus(), newStatus)
            );
        }

        order.setStatus(newStatus);

        // 상태별 추가 처리
        switch (newStatus) {
            case PAID:
                order.setPaymentDate(LocalDateTime.now());
                break;
            case SHIPPING:
                order.setShippingDate(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveryDate(LocalDateTime.now());
                break;
            case CANCELLED:
                // 재고 복구
                restoreStock(order);
                break;
        }

        orderRepository.update(order);
    }

    /**
     * 주문 취소
     *
     * @param orderId 주문 ID
     * @param userId 사용자 ID (권한 확인용)
     * @throws OrderNotFoundException 주문을 찾을 수 없는 경우
     * @throws UnauthorizedException 권한이 없는 경우
     */
    public void cancelOrder(String orderId, String userId)
            throws OrderNotFoundException, UnauthorizedException {

        Order order = getOrderById(orderId);

        // 권한 확인
        if (!order.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 주문을 취소할 권한이 없습니다.");
        }

        // 취소 가능 상태 확인
        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("이미 배송중이거나 완료된 주문은 취소할 수 없습니다.");
        }

        updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /**
     * 주문 통계 조회
     *
     * @param userId 사용자 ID (null이면 전체 통계)
     * @return 주문 통계
     */
    public OrderStatistics getStatistics(String userId) {
        List<Order> orders;

        if (userId != null) {
            orders = getOrdersByUserId(userId);
        } else {
            orders = getAllOrders();
        }

        OrderStatistics stats = new OrderStatistics();
        stats.setTotalOrders(orders.size());

        int pendingCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;
        double totalRevenue = 0;

        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING:
                case PAID:
                case SHIPPING:
                    pendingCount++;
                    break;
                case DELIVERED:
                    completedCount++;
                    totalRevenue += order.getTotalAmount();
                    break;
                case CANCELLED:
                    cancelledCount++;
                    break;
            }
        }

        stats.setPendingOrders(pendingCount);
        stats.setCompletedOrders(completedCount);
        stats.setCancelledOrders(cancelledCount);
        stats.setTotalRevenue(totalRevenue);

        if (completedCount > 0) {
            stats.setAverageOrderValue(totalRevenue / completedCount);
        }

        return stats;
    }

    /**
     * 주문 ID 생성
     *
     * @return 생성된 주문 ID
     */
    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 주문 항목 ID 생성
     *
     * @return 생성된 주문 항목 ID
     */
    private String generateOrderItemId() {
        return "ORI" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 상태 전환 유효성 검증
     *
     * @param currentStatus 현재 상태
     * @param newStatus 새로운 상태
     * @return 유효한 전환이면 true
     */
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // 취소는 대기중이나 결제완료 상태에서만 가능
        if (newStatus == OrderStatus.CANCELLED) {
            return currentStatus == OrderStatus.PENDING ||
                    currentStatus == OrderStatus.PAID;
        }

        // 정상적인 진행 순서
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.PAID ||
                        newStatus == OrderStatus.CANCELLED;
            case PAID:
                return newStatus == OrderStatus.SHIPPING ||
                        newStatus == OrderStatus.CANCELLED;
            case SHIPPING:
                return newStatus == OrderStatus.DELIVERED;
            case DELIVERED:
            case CANCELLED:
                return false; // 최종 상태는 변경 불가
            default:
                return false;
        }
    }

    /**
     * 재고 복구 (주문 취소 시)
     *
     * @param order 취소할 주문
     */
    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId());
            if (product != null) {
                product.addStock(item.getQuantity());
                productRepository.update(product);
            }
        }
    }

    /**
     * 주문 파라미터 검증
     */
    private void validateOrderParameters(String userId, String productId,
                                         int quantity, String shippingAddress) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 ID가 유효하지 않습니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("배송 주소를 입력해주세요.");
        }
    }

    /**
     * 주문 통계 정보 클래스
     */
    public static class OrderStatistics {
        private int totalOrders;
        private int pendingOrders;
        private int completedOrders;
        private int cancelledOrders;
        private double totalRevenue;
        private double averageOrderValue;

        // Getters and Setters
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public int getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(int pendingOrders) {
            this.pendingOrders = pendingOrders;
        }

        public int getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(int completedOrders) {
            this.completedOrders = completedOrders;
        }

        public int getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(int cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
        }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) {
            this.averageOrderValue = averageOrderValue;
        }
    }
}