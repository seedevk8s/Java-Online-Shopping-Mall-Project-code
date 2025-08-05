// =================================================================
// OrderRepository.java - 주문 데이터 저장소
// =================================================================
package javaproject.repository;

import javaproject.domain.Order;
import javaproject.domain.OrderItem;
import javaproject.exception.FileIOException;
import javaproject.util.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 주문 데이터를 파일에 저장하고 조회하는 Repository 클래스
 * 주문 생성, 조회, 취소 등에 사용
 */
public class OrderRepository implements BaseRepository<Order, String> {

    // 주문 데이터를 저장할 파일명
    private static final String ORDER_FILE_NAME = "orders.txt";

    // 싱글톤 인스턴스
    private static OrderRepository instance;

    // 주문 아이템 Repository 참조
    private OrderItemRepository orderItemRepository;

    /**
     * private 생성자 (싱글톤 패턴)
     */
    private OrderRepository() {
        // 초기화 시 데이터 디렉토리 생성
        FileManager.initializeDataDirectory();

        // 주문 아이템 Repository 초기화
        this.orderItemRepository = OrderItemRepository.getInstance();
    }

    /**
     * OrderRepository 인스턴스 반환 (싱글톤)
     * @return OrderRepository 인스턴스
     */
    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    // ================= BaseRepository 인터페이스 구현 =================

    /**
     * 새로운 주문 저장
     * @param order 저장할 주문 객체
     * @return 저장 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean save(Order order) throws FileIOException {
        if (order == null || order.getOrderId() == null) {
            return false;
        }

        // 중복 주문 ID 검사
        if (existsById(order.getOrderId())) {
            return false; // 이미 존재하는 주문 ID
        }

        // 기존 주문 목록 조회
        List<Order> orders = findAll();

        // 새 주문 추가
        orders.add(order);

        // 파일에 저장
        saveAllOrders(orders);

        // 주문 아이템들도 저장
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrderId(order.getOrderId()); // 주문 ID 설정
                orderItemRepository.save(item);
            }
        }

        return true;
    }

    /**
     * 주문 ID로 주문 조회
     * @param orderId 조회할 주문 ID
     * @return 조회된 주문 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public Optional<Order> findById(String orderId) throws FileIOException {
        if (orderId == null || orderId.trim().isEmpty()) {
            return Optional.empty();
        }

        List<Order> orders = findAll();

        // 주문 ID로 검색
        Optional<Order> orderOpt = orders.stream()
                .filter(order -> orderId.equals(order.getOrderId()))
                .findFirst();

        // 주문이 존재하면 주문 아이템들도 로드
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            order.setOrderItems(orderItems);
        }

        return orderOpt;
    }

    /**
     * 모든 주문 조회
     * @return 전체 주문 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public List<Order> findAll() throws FileIOException {
        List<String> lines = FileManager.readLines(ORDER_FILE_NAME);
        List<Order> orders = new ArrayList<>();

        // 각 줄을 Order 객체로 변환
        for (String line : lines) {
            try {
                Order order = Order.fromFileString(line);

                // 주문 아이템들 로드
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
                order.setOrderItems(orderItems);

                orders.add(order);
            } catch (Exception e) {
                // 잘못된 형식의 줄은 무시하고 로그만 출력
                System.err.println("주문 데이터 파싱 오류: " + line + " - " + e.getMessage());
            }
        }

        return orders;
    }

    /**
     * 주문 정보 수정
     * @param order 수정할 주문 객체
     * @return 수정 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean update(Order order) throws FileIOException {
        if (order == null || order.getOrderId() == null) {
            return false;
        }

        List<Order> orders = findAll();
        boolean found = false;

        // 해당 주문을 찾아서 정보 업데이트
        for (int i = 0; i < orders.size(); i++) {
            if (order.getOrderId().equals(orders.get(i).getOrderId())) {
                orders.set(i, order); // 기존 주문 정보를 새 정보로 교체
                found = true;
                break;
            }
        }

        if (found) {
            saveAllOrders(orders); // 전체 목록을 파일에 다시 저장

            // 주문 아이템들도 업데이트
            if (order.getOrderItems() != null) {
                // 기존 주문 아이템 삭제
                orderItemRepository.deleteByOrderId(order.getOrderId());

                // 새 주문 아이템 저장
                for (OrderItem item : order.getOrderItems()) {
                    item.setOrderId(order.getOrderId());
                    orderItemRepository.save(item);
                }
            }

            return true;
        }

        return false; // 해당 주문이 없음
    }

    /**
     * 주문 ID로 주문 삭제
     * @param orderId 삭제할 주문 ID
     * @return 삭제 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean deleteById(String orderId) throws FileIOException {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }

        List<Order> orders = findAll();
        boolean removed = orders.removeIf(order -> orderId.equals(order.getOrderId()));

        if (removed) {
            saveAllOrders(orders); // 변경된 목록을 파일에 저장

            // 관련 주문 아이템들도 삭제
            orderItemRepository.deleteByOrderId(orderId);

            return true;
        }

        return false; // 해당 주문이 없음
    }

    /**
     * 주문 존재 여부 확인
     * @param orderId 확인할 주문 ID
     * @return 존재하면 true
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public boolean existsById(String orderId) throws FileIOException {
        return findById(orderId).isPresent();
    }

    /**
     * 전체 주문 수
     * @return 총 주문 수
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public long count() throws FileIOException {
        return findAll().size();
    }

    // ================= 주문 특화 메서드들 =================

    /**
     * 사용자별 주문 내역 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 주문 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Order> findByUserId(String userId) throws FileIOException {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Order> orders = findAll();
        List<Order> userOrders = new ArrayList<>();

        for (Order order : orders) {
            if (userId.equals(order.getUserId())) {
                userOrders.add(order);
            }
        }

        return userOrders;
    }

    /**
     * 주문 상태별 조회
     * @param status 주문 상태
     * @return 해당 상태의 주문 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Order> findByStatus(Order.OrderStatus status) throws FileIOException {
        if (status == null) {
            return new ArrayList<>();
        }

        List<Order> orders = findAll();
        List<Order> statusOrders = new ArrayList<>();

        for (Order order : orders) {
            if (status == order.getStatus()) {
                statusOrders.add(order);
            }
        }

        return statusOrders;
    }

    /**
     * 취소 가능한 주문 조회 (사용자별)
     * @param userId 사용자 ID
     * @return 취소 가능한 주문 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Order> findCancellableOrdersByUserId(String userId) throws FileIOException {
        List<Order> userOrders = findByUserId(userId);
        List<Order> cancellableOrders = new ArrayList<>();

        for (Order order : userOrders) {
            if (order.isCancellable()) {
                cancellableOrders.add(order);
            }
        }

        return cancellableOrders;
    }

    /**
     * 특정 기간의 주문 조회
     * @param startDate 시작 날짜 (yyyy-MM-dd 형식)
     * @param endDate 종료 날짜 (yyyy-MM-dd 형식)
     * @return 해당 기간의 주문 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Order> findByDateRange(String startDate, String endDate) throws FileIOException {
        List<Order> orders = findAll();
        List<Order> rangeOrders = new ArrayList<>();

        for (Order order : orders) {
            String orderDate = order.getFormattedOrderDate().substring(0, 10); // yyyy-MM-dd 부분만 추출

            if (orderDate.compareTo(startDate) >= 0 && orderDate.compareTo(endDate) <= 0) {
                rangeOrders.add(order);
            }
        }

        return rangeOrders;
    }

    /**
     * 총 매출 계산
     * @return 총 매출 금액
     * @throws FileIOException 파일 읽기 실패 시
     */
    public long calculateTotalRevenue() throws FileIOException {
        List<Order> orders = findAll();
        long totalRevenue = 0;

        for (Order order : orders) {
            // 취소된 주문은 제외
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                totalRevenue += order.getTotalAmount();
            }
        }

        return totalRevenue;
    }

    /**
     * 사용자별 총 주문 금액 계산
     * @param userId 사용자 ID
     * @return 해당 사용자의 총 주문 금액
     * @throws FileIOException 파일 읽기 실패 시
     */
    public long calculateUserTotalAmount(String userId) throws FileIOException {
        List<Order> userOrders = findByUserId(userId);
        long totalAmount = 0;

        for (Order order : userOrders) {
            // 취소된 주문은 제외
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                totalAmount += order.getTotalAmount();
            }
        }

        return totalAmount;
    }

    // ================= 내부 헬퍼 메서드 =================

    /**
     * 모든 주문을 파일에 저장
     * @param orders 저장할 주문 목록
     * @throws FileIOException 파일 저장 실패 시
     */
    private void saveAllOrders(List<Order> orders) throws FileIOException {
        List<String> lines = new ArrayList<>();

        // 각 주문을 파일 저장 형식의 문자열로 변환
        for (Order order : orders) {
            lines.add(order.toFileString());
        }

        // 파일에 저장
        FileManager.writeLines(ORDER_FILE_NAME, lines);
    }

    /**
     * 주문 데이터 백업
     * @return 백업 성공 여부
     */
    public boolean backupOrderData() {
        boolean orderBackup = FileManager.backupFile(ORDER_FILE_NAME);
        boolean itemBackup = orderItemRepository.backupOrderItemData();

        return orderBackup && itemBackup;
    }

    /**
     * 주문 통계 정보 조회
     * @return 주문 통계 문자열
     * @throws FileIOException 파일 읽기 실패 시
     */
    public String getOrderStatistics() throws FileIOException {
        List<Order> allOrders = findAll();

        int pendingCount = 0;
        int confirmedCount = 0;
        int shippedCount = 0;
        int deliveredCount = 0;
        int cancelledCount = 0;

        for (Order order : allOrders) {
            switch (order.getStatus()) {
                case PENDING:
                    pendingCount++;
                    break;
                case CONFIRMED:
                    confirmedCount++;
                    break;
                case SHIPPED:
                    shippedCount++;
                    break;
                case DELIVERED:
                    deliveredCount++;
                    break;
                case CANCELLED:
                    cancelledCount++;
                    break;
            }
        }

        long totalRevenue = calculateTotalRevenue();

        StringBuilder stats = new StringBuilder();
        stats.append("=== 주문 통계 ===\n");
        stats.append("전체 주문: ").append(allOrders.size()).append("건\n");
        stats.append("주문접수: ").append(pendingCount).append("건\n");
        stats.append("주문확인: ").append(confirmedCount).append("건\n");
        stats.append("배송중: ").append(shippedCount).append("건\n");
        stats.append("배송완료: ").append(deliveredCount).append("건\n");
        stats.append("주문취소: ").append(cancelledCount).append("건\n");
        stats.append("총 매출: ").append(String.format("%,d", totalRevenue)).append("원\n");
        stats.append("==================");

        return stats.toString();
    }
}