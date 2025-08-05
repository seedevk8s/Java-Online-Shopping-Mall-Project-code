// =================================================================
// ProductRepository.java - 상품 데이터 저장소
// =================================================================
package javaproject.repository;

import javaproject.domain.Product;
import javaproject.exception.FileIOException;
import javaproject.exception.InsufficientStockException;
import javaproject.exception.ProductNotFoundException;
import javaproject.util.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 상품 데이터를 파일에 저장하고 조회하는 Repository 클래스
 * 상품 등록, 조회, 검색, 재고 관리 등에 사용
 */
public class ProductRepository implements BaseRepository<Product, String> {

    // 상품 데이터를 저장할 파일명
    private static final String PRODUCT_FILE_NAME = "products.txt";

    // 싱글톤 인스턴스
    private static ProductRepository instance;

    /**
     * private 생성자 (싱글톤 패턴)
     */
    private ProductRepository() {
        // 초기화 시 데이터 디렉토리 생성
        FileManager.initializeDataDirectory();

        // 샘플 데이터 초기화 (최초 실행 시)
        initializeSampleProducts();
    }

    /**
     * ProductRepository 인스턴스 반환 (싱글톤)
     * @return ProductRepository 인스턴스
     */
    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    /**
     * 샘플 상품 데이터 초기화
     * 최초 실행 시 몇 개의 샘플 상품을 생성
     */
    private void initializeSampleProducts() {
        try {
            // 기존 상품이 있는지 확인
            if (count() == 0) {
                // 샘플 상품들 생성
                Product[] sampleProducts = {
                        new Product("노트북", 1200000, "전자제품", 10, "고성능 게이밍 노트북"),
                        new Product("무선마우스", 45000, "전자제품", 50, "블루투스 무선 마우스"),
                        new Product("키보드", 89000, "전자제품", 30, "기계식 키보드"),
                        new Product("스마트폰", 800000, "전자제품", 20, "최신 스마트폰"),
                        new Product("청바지", 59000, "의류", 25, "편안한 데님 청바지"),
                        new Product("운동화", 120000, "신발", 15, "러닝용 운동화"),
                        new Product("자바 프로그래밍", 25000, "도서", 100, "자바 입문서"),
                        new Product("커피원두", 18000, "식품", 80, "프리미엄 원두")
                };

                // 샘플 상품들 저장
                for (Product product : sampleProducts) {
                    save(product);
                }

                System.out.println("샘플 상품 " + sampleProducts.length + "개가 생성되었습니다.");
            }
        } catch (FileIOException e) {
            System.err.println("샘플 상품 초기화 실패: " + e.getMessage());
        }
    }

    // ================= BaseRepository 인터페이스 구현 =================

    /**
     * 새로운 상품 저장
     * @param product 저장할 상품 객체
     * @return 저장 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean save(Product product) throws FileIOException {
        if (product == null) {
            return false;
        }

        // 기존 상품 목록 조회
        List<Product> products = findAll();

        // 새 상품 추가
        products.add(product);

        // 파일에 저장
        saveAllProducts(products);

        return true;
    }

    /**
     * 상품 ID로 상품 조회
     * @param productId 조회할 상품 ID
     * @return 조회된 상품 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public Optional<Product> findById(String productId) throws FileIOException {
        if (productId == null || productId.trim().isEmpty()) {
            return Optional.empty();
        }

        List<Product> products = findAll();

        // 상품 ID로 검색
        return products.stream()
                .filter(product -> productId.equals(product.getProductId()))
                .findFirst();
    }

    /**
     * 모든 상품 조회
     * @return 전체 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public List<Product> findAll() throws FileIOException {
        List<String> lines = FileManager.readLines(PRODUCT_FILE_NAME);
        List<Product> products = new ArrayList<>();

        // 각 줄을 Product 객체로 변환
        for (String line : lines) {
            try {
                Product product = Product.fromFileString(line);
                products.add(product);
            } catch (Exception e) {
                // 잘못된 형식의 줄은 무시하고 로그만 출력
                System.err.println("상품 데이터 파싱 오류: " + line + " - " + e.getMessage());
            }
        }

        return products;
    }

    /**
     * 상품 정보 수정
     * @param product 수정할 상품 객체
     * @return 수정 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean update(Product product) throws FileIOException {
        if (product == null || product.getProductId() == null) {
            return false;
        }

        List<Product> products = findAll();
        boolean found = false;

        // 해당 상품을 찾아서 정보 업데이트
        for (int i = 0; i < products.size(); i++) {
            if (product.getProductId().equals(products.get(i).getProductId())) {
                products.set(i, product); // 기존 상품 정보를 새 정보로 교체
                found = true;
                break;
            }
        }

        if (found) {
            saveAllProducts(products); // 전체 목록을 파일에 다시 저장
            return true;
        }

        return false; // 해당 상품이 없음
    }

    /**
     * 상품 ID로 상품 삭제
     * @param productId 삭제할 상품 ID
     * @return 삭제 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    @Override
    public boolean deleteById(String productId) throws FileIOException {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }

        List<Product> products = findAll();
        boolean removed = products.removeIf(product -> productId.equals(product.getProductId()));

        if (removed) {
            saveAllProducts(products); // 변경된 목록을 파일에 저장
            return true;
        }

        return false; // 해당 상품이 없음
    }

    /**
     * 상품 존재 여부 확인
     * @param productId 확인할 상품 ID
     * @return 존재하면 true
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public boolean existsById(String productId) throws FileIOException {
        return findById(productId).isPresent();
    }

    /**
     * 전체 상품 수
     * @return 총 상품 수
     * @throws FileIOException 파일 읽기 실패 시
     */
    @Override
    public long count() throws FileIOException {
        return findAll().size();
    }

    // ================= 상품 특화 메서드들 =================

    /**
     * 상품명으로 검색 (부분 일치)
     * @param name 검색할 상품명 (부분 일치)
     * @return 검색된 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findByNameContaining(String name) throws FileIOException {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = findAll();
        List<Product> results = new ArrayList<>();

        String searchName = name.trim().toLowerCase(); // 대소문자 구분 없이 검색

        for (Product product : products) {
            if (product.getName() != null &&
                    product.getName().toLowerCase().contains(searchName)) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * 카테고리별 상품 조회
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findByCategory(String category) throws FileIOException {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = findAll();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (category.equals(product.getCategory())) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * 가격 범위로 상품 검색
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위 내의 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findByPriceRange(int minPrice, int maxPrice) throws FileIOException {
        List<Product> products = findAll();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            int price = product.getPrice();
            if (price >= minPrice && price <= maxPrice) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * 재고가 있는 상품만 조회
     * @return 재고가 있는 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findAvailableProducts() throws FileIOException {
        List<Product> products = findAll();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getStock() > 0) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * 재고 부족 상품 조회 (재고 10개 이하)
     * @return 재고 부족 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findLowStockProducts() throws FileIOException {
        List<Product> products = findAll();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getStock() <= 10 && product.getStock() > 0) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * 품절 상품 조회
     * @return 품절 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findOutOfStockProducts() throws FileIOException {
        List<Product> products = findAll();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getStock() == 0) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * 모든 카테고리 목록 조회
     * @return 중복 제거된 카테고리 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<String> findAllCategories() throws FileIOException {
        List<Product> products = findAll();
        List<String> categories = new ArrayList<>();

        for (Product product : products) {
            String category = product.getCategory();
            if (category != null && !categories.contains(category)) {
                categories.add(category);
            }
        }

        return categories;
    }

    /**
     * 상품 재고 업데이트
     * @param productId 상품 ID
     * @param newStock 새로운 재고 수량
     * @return 업데이트 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     * @throws ProductNotFoundException 상품을 찾을 수 없을 시
     */
    public boolean updateStock(String productId, int newStock) throws FileIOException, ProductNotFoundException {
        Optional<Product> productOpt = findById(productId);

        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }

        Product product = productOpt.get();
        product.setStock(newStock); // 재고 업데이트

        return update(product); // 변경된 정보 저장
    }

    /**
     * 상품 재고 차감 (주문 시 사용)
     * @param productId 상품 ID
     * @param quantity 차감할 수량
     * @return 차감 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     * @throws ProductNotFoundException 상품을 찾을 수 없을 시
     * @throws InsufficientStockException 재고 부족 시
     */
    public boolean decreaseStock(String productId, int quantity)
            throws FileIOException, ProductNotFoundException, InsufficientStockException {

        Optional<Product> productOpt = findById(productId);

        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }

        Product product = productOpt.get();

        // 재고 부족 확인
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }

        // 재고 차감
        boolean success = product.decreaseStock(quantity);

        if (success) {
            return update(product); // 변경된 정보 저장
        }

        return false;
    }

    /**
     * 상품 재고 증가 (반품, 입고 시 사용)
     * @param productId 상품 ID
     * @param quantity 증가할 수량
     * @return 증가 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     * @throws ProductNotFoundException 상품을 찾을 수 없을 시
     */
    public boolean increaseStock(String productId, int quantity)
            throws FileIOException, ProductNotFoundException {

        Optional<Product> productOpt = findById(productId);

        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }

        Product product = productOpt.get();
        product.increaseStock(quantity); // 재고 증가

        return update(product); // 변경된 정보 저장
    }

    /**
     * 상품 가격 업데이트
     * @param productId 상품 ID
     * @param newPrice 새로운 가격
     * @return 업데이트 성공 여부
     * @throws FileIOException 파일 처리 실패 시
     * @throws ProductNotFoundException 상품을 찾을 수 없을 시
     */
    public boolean updatePrice(String productId, int newPrice)
            throws FileIOException, ProductNotFoundException {

        Optional<Product> productOpt = findById(productId);

        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }

        Product product = productOpt.get();
        product.setPrice(newPrice); // 가격 업데이트

        return update(product); // 변경된 정보 저장
    }

    // ================= 내부 헬퍼 메서드 =================

    /**
     * 모든 상품을 파일에 저장
     * @param products 저장할 상품 목록
     * @throws FileIOException 파일 저장 실패 시
     */
    private void saveAllProducts(List<Product> products) throws FileIOException {
        List<String> lines = new ArrayList<>();

        // 각 상품을 파일 저장 형식의 문자열로 변환
        for (Product product : products) {
            lines.add(product.toFileString());
        }

        // 파일에 저장
        FileManager.writeLines(PRODUCT_FILE_NAME, lines);
    }

    /**
     * 상품 데이터 백업
     * @return 백업 성공 여부
     */
    public boolean backupProductData() {
        return FileManager.backupFile(PRODUCT_FILE_NAME);
    }

    /**
     * 상품 통계 정보 조회
     * @return 상품 통계 문자열
     * @throws FileIOException 파일 읽기 실패 시
     */
    public String getProductStatistics() throws FileIOException {
        List<Product> allProducts = findAll();
        List<Product> availableProducts = findAvailableProducts();
        List<Product> lowStockProducts = findLowStockProducts();
        List<Product> outOfStockProducts = findOutOfStockProducts();
        List<String> categories = findAllCategories();

        // 총 상품 가치 계산
        long totalValue = 0;
        for (Product product : allProducts) {
            totalValue += (long) product.getPrice() * product.getStock();
        }

        StringBuilder stats = new StringBuilder();
        stats.append("=== 상품 통계 ===\n");
        stats.append("전체 상품: ").append(allProducts.size()).append("개\n");
        stats.append("판매 가능: ").append(availableProducts.size()).append("개\n");
        stats.append("재고 부족: ").append(lowStockProducts.size()).append("개\n");
        stats.append("품절: ").append(outOfStockProducts.size()).append("개\n");
        stats.append("카테고리 수: ").append(categories.size()).append("개\n");
        stats.append("총 재고 가치: ").append(String.format("%,d", totalValue)).append("원\n");
        stats.append("==================");

        return stats.toString();
    }

    /**
     * 카테고리별 상품 통계
     * @return 카테고리별 통계 문자열
     * @throws FileIOException 파일 읽기 실패 시
     */
    public String getCategoryStatistics() throws FileIOException {
        List<String> categories = findAllCategories();
        StringBuilder stats = new StringBuilder();

        stats.append("=== 카테고리별 통계 ===\n");

        for (String category : categories) {
            List<Product> categoryProducts = findByCategory(category);
            int totalStock = 0;
            long totalValue = 0;

            for (Product product : categoryProducts) {
                totalStock += product.getStock();
                totalValue += (long) product.getPrice() * product.getStock();
            }

            stats.append(category).append(": ")
                    .append(categoryProducts.size()).append("개, ")
                    .append("재고 ").append(totalStock).append("개, ")
                    .append("가치 ").append(String.format("%,d", totalValue)).append("원\n");
        }

        stats.append("========================");

        return stats.toString();
    }

    /**
     * 인기 상품 조회 (재고가 적게 남은 순서대로)
     * 실제로는 판매량 데이터가 있어야 하지만, 여기서는 재고 기준으로 추정
     * @param limit 조회할 상품 수
     * @return 인기 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> findPopularProducts(int limit) throws FileIOException {
        List<Product> allProducts = findAll();
        List<Product> popularProducts = new ArrayList<>();

        // 재고가 적은 순서로 정렬 (재고가 적다 = 많이 팔렸다고 가정)
        allProducts.sort((p1, p2) -> Integer.compare(p1.getStock(), p2.getStock()));

        // 상위 limit개만 선택
        int count = Math.min(limit, allProducts.size());
        for (int i = 0; i < count; i++) {
            popularProducts.add(allProducts.get(i));
        }

        return popularProducts;
    }

    /**
     * 복합 검색 (상품명, 카테고리, 가격 범위 조건)
     * @param name 상품명 (부분 일치, null 가능)
     * @param category 카테고리 (정확 일치, null 가능)
     * @param minPrice 최소 가격 (-1이면 무시)
     * @param maxPrice 최대 가격 (-1이면 무시)
     * @return 검색 조건에 맞는 상품 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public List<Product> searchProducts(String name, String category, int minPrice, int maxPrice)
            throws FileIOException {

        List<Product> allProducts = findAll();
        List<Product> results = new ArrayList<>();

        for (Product product : allProducts) {
            boolean matches = true;

            // 상품명 조건 확인
            if (name != null && !name.trim().isEmpty()) {
                if (product.getName() == null ||
                        !product.getName().toLowerCase().contains(name.trim().toLowerCase())) {
                    matches = false;
                }
            }

            // 카테고리 조건 확인
            if (matches && category != null && !category.trim().isEmpty()) {
                if (!category.equals(product.getCategory())) {
                    matches = false;
                }
            }

            // 가격 범위 조건 확인
            if (matches && minPrice > -1) {
                if (product.getPrice() < minPrice) {
                    matches = false;
                }
            }

            if (matches && maxPrice > -1) {
                if (product.getPrice() > maxPrice) {
                    matches = false;
                }
            }

            // 모든 조건을 만족하면 결과에 추가
            if (matches) {
                results.add(product);
            }
        }

        return results;
    }
}