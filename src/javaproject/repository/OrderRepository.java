package javaproject.repository;

import javaproject.domain.Order;
import javaproject.domain.OrderStatus;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 주문 저장소 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 메모리에 주문 데이터를 저장하고 관리
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderRepository {

    // 싱글톤 인스턴스
    private static final OrderRepository instance = new OrderRepository();

    // 주문 데이터 저장소 (ID를 키로 사용)
    private final Map<String, Order> orders;

    // 사용자별 주문 인덱스 (사용자별 빠른 검색을 위함)
    private final Map<String, List<Order>> userOrderIndex;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private OrderRepository() {
        this.orders = new HashMap<>();
        this.userOrderIndex = new HashMap<>();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return OrderRepository의 유일한 인스턴스
     */
    public static OrderRepository getInstance() {
        return instance;
    }

    /**
     * 주문 저장
     *
     * @param order 저장할 주문
     * @return 저장된 주문
     */
    public Order save(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("주문 정보가 유효하지 않습니다.");
        }

        // 기존 주문이 있으면 사용자 인덱스에서 제거
        Order existing = orders.get(order.getId());
        if (existing != null) {
            removeFromUserIndex(existing);
        }

        // 주문 저장
        orders.put(order.getId(), order);

        // 사용자 인덱스 업데이트
        addToUserIndex(order);

        return order;
    }

    /**
     * ID로 주문 조회
     *
     * @param id 주문 ID
     * @return 조회된 주문, 없으면 null
     */
    public Order findById(String id) {
        if (id == null) {
            return null;
        }
        return orders.get(id);
    }

    /**
     * 모든 주문 조회
     *
     * @return 전체 주문 목록
     */
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    /**
     * 사용자 ID로 주문 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 주문 목록
     */
    public List<Order> findByUserId(String userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Order> userOrders = userOrderIndex.get(userId);
        if (userOrders == null) {
            return new ArrayList<>();
        }

        // 최신 주문이 먼저 오도록 정렬
        return userOrders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .collect(Collectors.toList());
    }

    /**
     * 주문 상태로 조회
     *
     * @param status 주문 상태
     * @return 해당 상태의 주문 목록
     */
    public List<Order> findByStatus(OrderStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }

        return orders.values().stream()
                .filter(order -> status.equals(order.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID와 상태로 주문 조회
     *
     * @param userId 사용자 ID
     * @param status 주문 상태
     * @return 조건에 맞는 주문 목록
     */
    public List<Order> findByUserIdAndStatus(String userId, OrderStatus status) {
        return findByUserId(userId).stream()
                .filter(order -> status.equals(order.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 주문 수정
     *
     * @param order 수정할 주문
     * @return 수정된 주문
     */
    public Order update(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("주문 정보가 유효하지 않습니다.");
        }

        if (!orders.containsKey(order.getId())) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다: " + order.getId());
        }

        return save(order);
    }

    /**
     * 주문 삭제
     *
     * @param id 삭제할 주문 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(String id) {
        if (id == null) {
            return false;
        }

        Order removed = orders.remove(id);
        if (removed != null) {
            removeFromUserIndex(removed);
            return true;
        }

        return false;
    }

    /**
     * 주문 ID 존재 여부 확인
     *
     * @param id 확인할 ID
     * @return 존재하면 true
     */
    public boolean existsById(String id) {
        return id != null && orders.containsKey(id);
    }

    /**
     * 주문 수 반환
     *
     * @return 전체 주문 수
     */
    public int count() {
        return orders.size();
    }

    /**
     * 사용자별 주문 수 반환
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 주문 수
     */
    public int countByUserId(String userId) {
        List<Order> userOrders = userOrderIndex.get(userId);
        return userOrders != null ? userOrders.size() : 0;
    }

    /**
     * 모든 데이터 삭제
     */
    public void deleteAll() {
        orders.clear();
        userOrderIndex.clear();
    }

    /**
     * 날짜 범위로 주문 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 날짜 범위에 해당하는 주문 목록
     */
    public List<Order> findByDateRange(java.time.LocalDateTime startDate,
                                       java.time.LocalDateTime endDate) {
        return orders.values().stream()
                .filter(order -> !order.getOrderDate().isBefore(startDate) &&
                        !order.getOrderDate().isAfter(endDate))
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .collect(Collectors.toList());
    }

    /**
     * 진행중인 주문 조회
     *
     * @return 진행중인 주문 목록
     */
    public List<Order> findActiveOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() != OrderStatus.DELIVERED &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    /**
     * 완료된 주문 조회
     *
     * @return 완료된 주문 목록
     */
    public List<Order> findCompletedOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.toList());
    }

    /**
     * 취소된 주문 조회
     *
     * @return 취소된 주문 목록
     */
    public List<Order> findCancelledOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 인덱스에 주문 추가
     *
     * @param order 추가할 주문
     */
    private void addToUserIndex(Order order) {
        if (order.getUserId() != null) {
            userOrderIndex.computeIfAbsent(order.getUserId(),
                    k -> new ArrayList<>()).add(order);
        }
    }

    /**
     * 사용자 인덱스에서 주문 제거
     *
     * @param order 제거할 주문
     */
    private void removeFromUserIndex(Order order) {
        if (order.getUserId() != null) {
            List<Order> userOrders = userOrderIndex.get(order.getUserId());
            if (userOrders != null) {
                userOrders.remove(order);
                if (userOrders.isEmpty()) {
                    userOrderIndex.remove(order.getUserId());
                }
            }
        }
    }

    /**
     * 저장소 상태 정보 반환
     *
     * @return 상태 정보 문자열
     */
    public String getStatistics() {
        int totalOrders = orders.size();
        int activeOrders = findActiveOrders().size();
        int completedOrders = findCompletedOrders().size();
        int cancelledOrders = findCancelledOrders().size();

        double totalRevenue = orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        return String.format(
                "=== 주문 저장소 통계 ===\n" +
                        "전체 주문: %d건\n" +
                        "진행중: %d건\n" +
                        "완료: %d건\n" +
                        "취소: %d건\n" +
                        "총 매출: %,.0f원",
                totalOrders, activeOrders, completedOrders, cancelledOrders, totalRevenue
        );
    }

    /**
     * 페이징 처리된 주문 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 주문 목록
     */
    public List<Order> findAllPaged(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("유효하지 않은 페이징 파라미터입니다.");
        }

        List<Order> allOrders = findAll();
        // 최신순 정렬
        allOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));

        int start = page * size;
        int end = Math.min(start + size, allOrders.size());

        if (start >= allOrders.size()) {
            return new ArrayList<>();
        }

        return allOrders.subList(start, end);
    }
}