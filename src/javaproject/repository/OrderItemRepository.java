// =================================================================
// OrderItemRepository.java - 주문 상품 데이터 저장소
// =================================================================
package javaproject.repository;

import javaproject.domain.OrderItem;
import javaproject.exception.FileIOException;
import javaproject.util.FileManager;

import java.util.*;

/**
 * 주문 상품 데이터를 파일에 저장하고 조회하는 Repository 클래스
 * 주문별 상품 정보 관리에 사용
 */
public class OrderItemRepository {

    // 주문 상품 데이터를 저장할 파일명
    private static final String ORDER_ITEM_FILE_NAME = "order_items.txt";

    // 싱글톤 인스턴스
    private static OrderItemRepository instance;

    /**
     * private 생성자 (싱글톤 패턴)
     */
    private OrderItemRepository() {
        // 초기화 시 데이터 디렉토리 생성
        FileManager.initializeDataDirectory();
    }

    /**
     * OrderItemRepository 인스턴스 반환 (싱글톤)
     * @return OrderItemRepository 인스턴스
     */
    public static OrderItemRepository getInstance() {
        if (instance == null) {
            instance = new OrderItemRepository();
        }
        return instance;
    }

    /**
     * 새로운 주문 상품 저장
     * @param orderItem 저장할 주문 상품 객체
     * @return 저장 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    public boolean save(OrderItem orderItem) throws FileIOException {
        if (orderItem == null || orderItem.getOrderId() == null || orderItem.getProductId() == null) {
            return false;
        }

        // 기존 주문 상품 목록에 추가
        String itemString = orderItem.toFileString();
        FileManager.appendLine(ORDER_ITEM_FILE_NAME, itemString);

        return true;
    }

    /**
     * 주문 ID로 주문 상품 목록 조회
     * @param orderId 주문 ID
     * @return 해당 주문의 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<OrderItem> findByOrderId(String orderId) throws FileIOException {
        if (orderId == null || orderId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> lines = FileManager.readLines(ORDER_ITEM_FILE_NAME);
        List<OrderItem> orderItems = new ArrayList<>();

        // 해당 주문 ID의 상품들만 필터링
        for (String line : lines) {
            try {
                OrderItem orderItem = OrderItem.fromFileString(line);
                if (orderId.equals(orderItem.getOrderId())) {
                    orderItems.add(orderItem);
                }
            } catch (Exception e) {
                System.err.println("주문 상품 데이터 파싱 오류: " + line + " - " + e.getMessage());
            }
        }

        return orderItems;
    }

    /**
     * 모든 주문 상품 조회
     * @return 전체 주문 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<OrderItem> findAll() throws FileIOException {
        List<String> lines = FileManager.readLines(ORDER_ITEM_FILE_NAME);
        List<OrderItem> orderItems = new ArrayList<>();

        for (String line : lines) {
            try {
                OrderItem orderItem = OrderItem.fromFileString(line);
                orderItems.add(orderItem);
            } catch (Exception e) {
                System.err.println("주문 상품 데이터 파싱 오류: " + line + " - " + e.getMessage());
            }
        }

        return orderItems;
    }

    /**
     * 특정 주문의 특정 상품 조회
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @return 해당 주문 상품 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    public Optional<OrderItem> findByOrderIdAndProductId(String orderId, String productId) throws FileIOException {
        if (orderId == null || productId == null) {
            return Optional.empty();
        }

        List<OrderItem> orderItems = findByOrderId(orderId);

        return orderItems.stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst();
    }

    /**
     * 주문 ID로 주문 상품 삭제
     * @param orderId 삭제할 주문 ID
     * @return 삭제 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean deleteByOrderId(String orderId) throws FileIOException {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }

        List<OrderItem> allItems = findAll();
        List<OrderItem> remainingItems = new ArrayList<>();
        boolean hasDeleted = false;

        // 해당 주문 ID가 아닌 상품들만 남김
        for (OrderItem item : allItems) {
            if (!orderId.equals(item.getOrderId())) {
                remainingItems.add(item);
            } else {
                hasDeleted = true;
            }
        }

        if (hasDeleted) {
            // 남은 상품들을 파일에 다시 저장
            saveAllOrderItems(remainingItems);
        }

        return hasDeleted;
    }

    /**
     * 특정 주문의 특정 상품 삭제
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @return 삭제 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean deleteByOrderIdAndProductId(String orderId, String productId) throws FileIOException {
        if (orderId == null || productId == null) {
            return false;
        }

        List<OrderItem> allItems = findAll();
        List<OrderItem> remainingItems = new ArrayList<>();
        boolean hasDeleted = false;

        // 해당 주문의 해당 상품만 제외하고 나머지 보존
        for (OrderItem item : allItems) {
            if (orderId.equals(item.getOrderId()) && productId.equals(item.getProductId())) {
                hasDeleted = true; // 삭제 대상
            } else {
                remainingItems.add(item); // 보존
            }
        }

        if (hasDeleted) {
            saveAllOrderItems(remainingItems);
        }

        return hasDeleted;
    }

    /**
     * 상품별 판매 통계 (상품 ID별 총 판매량)
     * @return 상품 ID와 판매량 맵
     * @throws FileIOException 파일 읽기 실패 시
     */
    public Map<String, Integer> getProductSalesStatistics() throws FileIOException {
        List<OrderItem> allItems = findAll();
        Map<String, Integer> salesMap = new HashMap<>();

        for (OrderItem item : allItems) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            salesMap.put(productId, salesMap.getOrDefault(productId, 0) + quantity);
        }

        return salesMap;
    }

    /**
     * 상품별 매출 통계 (상품 ID별 총 매출액)
     * @return 상품 ID와 매출액 맵
     * @throws FileIOException 파일 읽기 실패 시
     */
    public Map<String, Long> getProductRevenueStatistics() throws FileIOException {
        List<OrderItem> allItems = findAll();
        Map<String, Long> revenueMap = new HashMap<>();

        for (OrderItem item : allItems) {
            String productId = item.getProductId();
            long revenue = (long) item.getProductPrice() * item.getQuantity();

            revenueMap.put(productId, revenueMap.getOrDefault(productId, 0L) + revenue);
        }

        return revenueMap;
    }

    /**
     * 베스트셀러 상품 조회 (판매량 기준)
     * @param limit 조회할 상품 수
     * @return 베스트셀러 상품 목록 (상품ID, 판매량)
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Map.Entry<String, Integer>> getBestSellingProducts(int limit) throws FileIOException {
        Map<String, Integer> salesMap = getProductSalesStatistics();

        // 판매량 기준으로 정렬
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(salesMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // 상위 limit개만 반환
        return sortedList.subList(0, Math.min(limit, sortedList.size()));
    }

    /**
     * 주문별 아이템 수 통계
     * @return 주문 ID와 아이템 수 맵
     * @throws FileIOException 파일 읽기 실패 시
     */
    public Map<String, Integer> getOrderItemCountStatistics() throws FileIOException {
        List<OrderItem> allItems = findAll();
        Map<String, Integer> countMap = new HashMap<>();

        for (OrderItem item : allItems) {
            String orderId = item.getOrderId();
            countMap.put(orderId, countMap.getOrDefault(orderId, 0) + 1);
        }

        return countMap;
    }

    /**
     * 특정 상품이 포함된 주문 목록 조회
     * @param productId 상품 ID
     * @return 해당 상품을 포함한 주문 ID 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<String> findOrderIdsByProductId(String productId) throws FileIOException {
        if (productId == null || productId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderItem> allItems = findAll();
        List<String> orderIds = new ArrayList<>();

        for (OrderItem item : allItems) {
            if (productId.equals(item.getProductId()) && !orderIds.contains(item.getOrderId())) {
                orderIds.add(item.getOrderId());
            }
        }

        return orderIds;
    }

    /**
     * 주문 상품 수정 (수량, 가격 변경)
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @param newQuantity 새로운 수량
     * @param newPrice 새로운 가격
     * @return 수정 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     */
    public boolean updateOrderItem(String orderId, String productId, int newQuantity, int newPrice) throws FileIOException {
        List<OrderItem> allItems = findAll();
        boolean found = false;

        // 해당 주문 상품 찾아서 수정
        for (OrderItem item : allItems) {
            if (orderId.equals(item.getOrderId()) && productId.equals(item.getProductId())) {
                item.setQuantity(newQuantity);
                item.setProductPrice(newPrice);
                found = true;
                break;
            }
        }

        if (found) {
            saveAllOrderItems(allItems);
        }

        return found;
    }

    // ================= 내부 헬퍼 메서드 =================

    /**
     * 모든 주문 상품을 파일에 저장
     * @param orderItems 저장할 주문 상품 목록
     * @throws FileIOException 파일 저장 실패 시
     */
    private void saveAllOrderItems(List<OrderItem> orderItems) throws FileIOException {
        List<String> lines = new ArrayList<>();

        for (OrderItem item : orderItems) {
            lines.add(item.toFileString());
        }

        FileManager.writeLines(ORDER_ITEM_FILE_NAME, lines);
    }

    /**
     * 주문 상품 데이터 백업
     * @return 백업 성공 여부
     */
    public boolean backupOrderItemData() {
        return FileManager.backupFile(ORDER_ITEM_FILE_NAME);
    }

    /**
     * 주문 상품 통계 정보 조회
     * @return 주문 상품 통계 문자열
     * @throws FileIOException 파일 읽기 실패 시
     */
    public String getOrderItemStatistics() throws FileIOException {
        List<OrderItem> allItems = findAll();
        Map<String, Integer> salesMap = getProductSalesStatistics();
        Map<String, Long> revenueMap = getProductRevenueStatistics();

        int totalQuantity = 0;
        long totalRevenue = 0;

        for (OrderItem item : allItems) {
            totalQuantity += item.getQuantity();
            totalRevenue += (long) item.getProductPrice() * item.getQuantity();
        }

        StringBuilder stats = new StringBuilder();
        stats.append("=== 주문 상품 통계 ===\n");
        stats.append("총 주문 상품 종류: ").append(allItems.size()).append("개\n");
        stats.append("총 판매 수량: ").append(totalQuantity).append("개\n");
        stats.append("총 매출액: ").append(String.format("%,d", totalRevenue)).append("원\n");
        stats.append("판매된 상품 종류: ").append(salesMap.size()).append("개\n");

        if (!salesMap.isEmpty()) {
            // 가장 많이 팔린 상품
            String bestProduct = salesMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("없음");
            int bestQuantity = salesMap.getOrDefault(bestProduct, 0);

            stats.append("베스트셀러: ").append(bestProduct)
                    .append(" (").append(bestQuantity).append("개 판매)\n");
        }

        stats.append("=====================");

        return stats.toString();
    }

    /**
     * 전체 주문 상품 개수
     * @return 총 주문 상품 개수
     * @throws FileIOException 파일 읽기 실패 시
     */
    public long count() throws FileIOException {
        return findAll().size();
    }

    /**
     * 특정 주문의 주문 상품 개수
     * @param orderId 주문 ID
     * @return 해당 주문의 상품 개수
     * @throws FileIOException 파일 읽기 실패 시
     */
    public int countByOrderId(String orderId) throws FileIOException {
        return findByOrderId(orderId).size();
    }

    /**
     * 파일 정보 출력 (디버깅용)
     */
    public void printFileInfo() {
        FileManager.printFileInfo(ORDER_ITEM_FILE_NAME);
    }
}