package javaproject.util;

import javaproject.domain.*;
import javaproject.repository.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 파일 관리 유틸리티 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 데이터의 파일 저장 및 로드 기능 제공
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class FileManager {

    // 싱글톤 인스턴스
    private static final FileManager instance = new FileManager();

    // 데이터 저장 디렉토리
    private static final String DATA_DIR = "data";

    // 파일 경로 상수들
    private static final String USER_FILE = DATA_DIR + "/users.dat";
    private static final String PRODUCT_FILE = DATA_DIR + "/products.dat";
    private static final String ORDER_FILE = DATA_DIR + "/orders.dat";
    private static final String ORDER_ITEM_FILE = DATA_DIR + "/order_items.dat";
    private static final String CART_FILE = DATA_DIR + "/carts.dat";

    // Repository 인스턴스들
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private FileManager() {
        this.userRepository = UserRepository.getInstance();
        this.productRepository = ProductRepository.getInstance();
        this.orderRepository = OrderRepository.getInstance();
        this.orderItemRepository = OrderItemRepository.getInstance();
        this.cartRepository = CartRepository.getInstance();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return FileManager의 유일한 인스턴스
     */
    public static FileManager getInstance() {
        return instance;
    }

    /**
     * 데이터 디렉토리 생성 (없는 경우)
     */
    public void createDataDirectoryIfNotExists() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                System.out.println("📁 데이터 디렉토리 생성: " + DATA_DIR);
            }
        }
    }

    // ===== 사용자 데이터 =====

    /**
     * 사용자 데이터 저장
     */
    public void saveUsers() {
        List<User> users = userRepository.findAll();
        saveToFile(USER_FILE, users);
    }

    /**
     * 사용자 데이터 로드
     * @return 로드된 사용자 수
     */
    @SuppressWarnings("unchecked")
    public int loadUsers() {
        List<User> users = (List<User>) loadFromFile(USER_FILE);
        if (users != null) {
            for (User user : users) {
                userRepository.save(user);
            }
            return users.size();
        }
        return 0;
    }

    // ===== 상품 데이터 =====

    /**
     * 상품 데이터 저장
     */
    public void saveProducts() {
        List<Product> products = productRepository.findAll();
        saveToFile(PRODUCT_FILE, products);
    }

    /**
     * 상품 데이터 로드
     * @return 로드된 상품 수
     */
    @SuppressWarnings("unchecked")
    public int loadProducts() {
        List<Product> products = (List<Product>) loadFromFile(PRODUCT_FILE);
        if (products != null) {
            for (Product product : products) {
                productRepository.save(product);
            }
            return products.size();
        }
        return 0;
    }

    // ===== 주문 데이터 =====

    /**
     * 주문 데이터 저장
     */
    public void saveOrders() {
        List<Order> orders = orderRepository.findAll();
        saveToFile(ORDER_FILE, orders);

        // 주문 항목도 함께 저장
        List<OrderItem> allItems = new ArrayList<>();
        for (Order order : orders) {
            if (order.getItems() != null) {
                allItems.addAll(order.getItems());
            }
        }
        saveToFile(ORDER_ITEM_FILE, allItems);
    }

    /**
     * 주문 데이터 로드
     * @return 로드된 주문 수
     */
    @SuppressWarnings("unchecked")
    public int loadOrders() {
        // 먼저 주문 항목 로드
        List<OrderItem> items = (List<OrderItem>) loadFromFile(ORDER_ITEM_FILE);
        if (items != null) {
            for (OrderItem item : items) {
                orderItemRepository.save(item);
            }
        }

        // 주문 로드
        List<Order> orders = (List<Order>) loadFromFile(ORDER_FILE);
        if (orders != null) {
            for (Order order : orders) {
                // 주문 항목 연결
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                order.setItems(orderItems);
                orderRepository.save(order);
            }
            return orders.size();
        }
        return 0;
    }

    // ===== 장바구니 데이터 =====

    /**
     * 장바구니 데이터 저장
     */
    public void saveCarts() {
        List<Cart> carts = cartRepository.findAll();
        saveToFile(CART_FILE, carts);
    }

    /**
     * 장바구니 데이터 로드
     * @return 로드된 장바구니 수
     */
    @SuppressWarnings("unchecked")
    public int loadCarts() {
        List<Cart> carts = (List<Cart>) loadFromFile(CART_FILE);
        if (carts != null) {
            for (Cart cart : carts) {
                cartRepository.save(cart);
            }
            return carts.size();
        }
        return 0;
    }

    // ===== 파일 입출력 헬퍼 메서드 =====

    /**
     * 객체를 파일에 저장
     *
     * @param filename 파일명
     * @param data 저장할 데이터
     */
    private void saveToFile(String filename, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("파일 저장 실패 (" + filename + "): " + e.getMessage());
        }
    }

    /**
     * 파일에서 객체 로드
     *
     * @param filename 파일명
     * @return 로드된 객체
     */
    private Object loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("파일 로드 실패 (" + filename + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * 모든 데이터 저장
     */
    public void saveAllData() {
        createDataDirectoryIfNotExists();
        saveUsers();
        saveProducts();
        saveOrders();
        saveCarts();
    }

    /**
     * 모든 데이터 로드
     *
     * @return 로드 성공 여부
     */
    public boolean loadAllData() {
        createDataDirectoryIfNotExists();

        int userCount = loadUsers();
        int productCount = loadProducts();
        int orderCount = loadOrders();
        int cartCount = loadCarts();

        System.out.println("📊 데이터 로드 완료:");
        System.out.println("  - 사용자: " + userCount + "명");
        System.out.println("  - 상품: " + productCount + "개");
        System.out.println("  - 주문: " + orderCount + "건");
        System.out.println("  - 장바구니: " + cartCount + "개");

        return true;
    }

    /**
     * 백업 파일 생성
     *
     * @param suffix 백업 파일 접미사
     */
    public void createBackup(String suffix) {
        createDataDirectoryIfNotExists();

        // 백업 디렉토리 생성
        String backupDir = DATA_DIR + "/backup_" + suffix;
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 각 파일 백업
        copyFile(USER_FILE, backupDir + "/users.dat");
        copyFile(PRODUCT_FILE, backupDir + "/products.dat");
        copyFile(ORDER_FILE, backupDir + "/orders.dat");
        copyFile(ORDER_ITEM_FILE, backupDir + "/order_items.dat");
        copyFile(CART_FILE, backupDir + "/carts.dat");

        System.out.println("✅ 백업 완료: " + backupDir);
    }

    /**
     * 파일 복사
     *
     * @param source 원본 파일
     * @param destination 대상 파일
     */
    private void copyFile(String source, String destination) {
        File srcFile = new File(source);
        if (!srcFile.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

        } catch (IOException e) {
            System.err.println("파일 복사 실패: " + e.getMessage());
        }
    }

    /**
     * 데이터 초기화 (모든 파일 삭제)
     * 주의: 이 메서드는 모든 데이터를 삭제합니다!
     */
    public void clearAllData() {
        File dataDir = new File(DATA_DIR);
        if (dataDir.exists()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".dat")) {
                        file.delete();
                    }
                }
            }
        }
        System.out.println("⚠️ 모든 데이터가 초기화되었습니다.");
    }

    /**
     * 파일 존재 여부 확인
     *
     * @param filename 파일명
     * @return 파일이 존재하면 true
     */
    public boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    /**
     * 데이터 파일이 하나라도 존재하는지 확인
     *
     * @return 데이터 파일이 존재하면 true
     */
    public boolean hasDataFiles() {
        return fileExists(USER_FILE) ||
                fileExists(PRODUCT_FILE) ||
                fileExists(ORDER_FILE) ||
                fileExists(CART_FILE);
    }
}