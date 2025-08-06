package javaproject.repository;

import javaproject.domain.OrderItem;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 주문 항목 저장소 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 메모리에 주문 항목 데이터를 저장하고 관리
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class OrderItemRepository {

    // 싱글톤 인스턴스
    private static final OrderItemRepository instance = new OrderItemRepository();

    // 주문 항목 데이터 저장소 (ID를 키로 사용)
    private final Map<String, OrderItem> orderItems;

    // 주문별 항목 인덱스 (주문별 빠른 검색을 위함)
    private final Map<String, List<OrderItem>> orderIndex;

    // 상품별 항목 인덱스 (상품별 판매 내역 조회를 위함)
    private final Map<String, List<OrderItem>> productIndex;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private OrderItemRepository() {
        this.orderItems = new HashMap<>();
        this.orderIndex = new HashMap<>();
        this.productIndex = new HashMap<>();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return OrderItemRepository의 유일한 인스턴스
     */
    public static OrderItemRepository getInstance() {
        return instance;
    }

    /**
     * 주문 항목 저장
     *
     * @param orderItem 저장할 주문 항목
     * @return 저장된 주문 항목
     */
    public OrderItem save(OrderItem orderItem) {
        if (orderItem == null || orderItem.getId() == null) {
            throw new IllegalArgumentException("주문 항목 정보가 유효하지 않습니다.");
        }

        // 기존 항목이 있으면 인덱스에서 제거
        OrderItem existing = orderItems.get(orderItem.getId());
        if (existing != null) {
            removeFromIndexes(existing);
        }

        // 주문 항목 저장
        orderItems.put(orderItem.getId(), orderItem);

        // 인덱스 업데이트
        addToIndexes(orderItem);

        return orderItem;
    }

    /**
     * ID로 주문 항목 조회
     *
     * @param id 주문 항목 ID
     * @return 조회된 주문 항목, 없으면 null
     */
    public OrderItem findById(String id) {
        if (id == null) {
            return null;
        }
        return orderItems.get(id);
    }

    /**
     * 모든 주문 항목 조회
     *
     * @return 전체 주문 항목 목록
     */
    public List<OrderItem> findAll() {
        return new ArrayList<>(orderItems.values());
    }

    /**
     * 주문 ID로 항목 조회
     *
     * @param orderId 주문 ID
     * @return 해당 주문의 항목 목록
     */
    public List<OrderItem> findByOrderId(String orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }

        List<OrderItem> items = orderIndex.get(orderId);
        if (items == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(items);
    }

    /**
     * 상품 ID로 항목 조회
     *
     * @param productId 상품 ID
     * @return 해당 상품의 판매 항목 목록
     */
    public List<OrderItem> findByProductId(String productId) {
        if (productId == null) {
            return new ArrayList<>();
        }

        List<OrderItem> items = productIndex.get(productId);
        if (items == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(items);
    }

    /**
     * 주문 항목 수정
     *
     * @param orderItem 수정할 주문 항목
     * @return 수정된 주문 항목
     */
    public OrderItem update(OrderItem orderItem) {
        if (orderItem == null || orderItem.getId() == null) {
            throw new IllegalArgumentException("주문 항목 정보가 유효하지 않습니다.");
        }

        if (!orderItems.containsKey(orderItem.getId())) {
            throw new IllegalArgumentException("존재하지 않는 주문 항목입니다: " + orderItem.getId());
        }

        return save(orderItem);
    }

    /**
     * 주문 항목 삭제
     *
     * @param id 삭제할 주문 항목 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(String id) {
        if (id == null) {
            return false;
        }

        OrderItem removed = orderItems.remove(id);
        if (removed != null) {
            removeFromIndexes(removed);
            return true;
        }

        return false;
    }

    /**
     * 주문의 모든 항목 삭제
     *
     * @param orderId 주문 ID
     * @return 삭제된 항목 수
     */
    public int deleteByOrderId(String orderId) {
        if (orderId == null) {
            return 0;
        }

        List<OrderItem> items = findByOrderId(orderId);
        int count = 0;

        for (OrderItem item : items) {
            if (delete(item.getId())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 주문 항목 ID 존재 여부 확인
     *
     * @param id 확인할 ID
     * @return 존재하면 true
     */
    public boolean existsById(String id) {
        return id != null && orderItems.containsKey(id);
    }

    /**
     * 주문 항목 수 반환
     *
     * @return 전체 주문 항목 수
     */
    public int count() {
        return orderItems.size();
    }

    /**
     * 주문별 항목 수 반환
     *
     * @param orderId 주문 ID
     * @return 해당 주문의 항목 수
     */
    public int countByOrderId(String orderId) {
        List<OrderItem> items = orderIndex.get(orderId);
        return items != null ? items.size() : 0;
    }

    /**
     * 상품별 판매 수량 계산
     *
     * @param productId 상품 ID
     * @return 총 판매 수량
     */
    public int getTotalSoldQuantity(String productId) {
        List<OrderItem> items = findByProductId(productId);
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * 상품별 매출 계산
     *
     * @param productId 상품 ID
     * @return 총 매출액
     */
    public double getTotalRevenue(String productId) {
        List<OrderItem> items = findByProductId(productId);
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * 모든 데이터 삭제
     */
    public void deleteAll() {
        orderItems.clear();
        orderIndex.clear();
        productIndex.clear();
    }

    /**
     * 인덱스에 항목 추가
     *
     * @param orderItem 추가할 항목
     */
    private void addToIndexes(OrderItem orderItem) {
        // 주문 인덱스에 추가
        if (orderItem.getOrderId() != null) {
            orderIndex.computeIfAbsent(orderItem.getOrderId(),
                    k -> new ArrayList<>()).add(orderItem);
        }

        // 상품 인덱스에 추가
        if (orderItem.getProductId() != null) {
            productIndex.computeIfAbsent(orderItem.getProductId(),
                    k -> new ArrayList<>()).add(orderItem);
        }
    }

    /**
     * 인덱스에서 항목 제거
     *
     * @param orderItem 제거할 항목
     */
    private void removeFromIndexes(OrderItem orderItem) {
        // 주문 인덱스에서 제거
        if (orderItem.getOrderId() != null) {
            List<OrderItem> items = orderIndex.get(orderItem.getOrderId());
            if (items != null) {
                items.remove(orderItem);
                if (items.isEmpty()) {
                    orderIndex.remove(orderItem.getOrderId());
                }
            }
        }

        // 상품 인덱스에서 제거
        if (orderItem.getProductId() != null) {
            List<OrderItem> items = productIndex.get(orderItem.getProductId());
            if (items != null) {
                items.remove(orderItem);
                if (items.isEmpty()) {
                    productIndex.remove(orderItem.getProductId());
                }
            }
        }
    }

    /**
     * 베스트셀러 상품 ID 목록 조회
     *
     * @param limit 조회할 개수
     * @return 베스트셀러 상품 ID 목록
     */
    public List<String> getBestSellerProductIds(int limit) {
        Map<String, Integer> salesCount = new HashMap<>();

        // 상품별 판매 수량 집계
        for (List<OrderItem> items : productIndex.values()) {
            if (!items.isEmpty()) {
                String productId = items.get(0).getProductId();
                int totalQuantity = items.stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum();
                salesCount.put(productId, totalQuantity);
            }
        }

        // 판매량 기준 정렬
        return salesCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 저장소 상태 정보 반환
     *
     * @return 상태 정보 문자열
     */
    public String getStatistics() {
        int totalItems = orderItems.size();
        int totalOrders = orderIndex.size();
        int totalProducts = productIndex.size();

        double totalRevenue = orderItems.values().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        return String.format(
                "=== 주문 항목 저장소 통계 ===\n" +
                        "전체 항목: %d개\n" +
                        "주문 수: %d건\n" +
                        "판매 상품 종류: %d개\n" +
                        "총 매출: %,.0f원",
                totalItems, totalOrders, totalProducts, totalRevenue
        );
    }
}