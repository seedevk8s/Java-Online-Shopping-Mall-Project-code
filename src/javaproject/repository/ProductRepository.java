package javaproject.repository;

import javaproject.domain.Product;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 상품 저장소 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 메모리에 상품 데이터를 저장하고 관리
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class ProductRepository {

    // 싱글톤 인스턴스
    private static final ProductRepository instance = new ProductRepository();

    // 상품 데이터 저장소 (ID를 키로 사용)
    private final Map<String, Product> products;

    // 카테고리별 인덱스 (카테고리별 빠른 검색을 위함)
    private final Map<String, List<Product>> categoryIndex;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private ProductRepository() {
        this.products = new HashMap<>();
        this.categoryIndex = new HashMap<>();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return ProductRepository의 유일한 인스턴스
     */
    public static ProductRepository getInstance() {
        return instance;
    }

    /**
     * 상품 저장
     *
     * @param product 저장할 상품
     * @return 저장된 상품
     */
    public Product save(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("상품 정보가 유효하지 않습니다.");
        }

        // 기존 상품이 있으면 카테고리 인덱스에서 제거
        Product existing = products.get(product.getId());
        if (existing != null) {
            removeFromCategoryIndex(existing);
        }

        // 상품 저장
        products.put(product.getId(), product);

        // 카테고리 인덱스 업데이트
        addToCategoryIndex(product);

        return product;
    }

    /**
     * ID로 상품 조회
     *
     * @param id 상품 ID
     * @return 조회된 상품, 없으면 null
     */
    public Product findById(String id) {
        if (id == null) {
            return null;
        }
        return products.get(id);
    }

    /**
     * 모든 상품 조회
     *
     * @return 전체 상품 목록
     */
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * 카테고리별 상품 조회
     *
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     */
    public List<Product> findByCategory(String category) {
        if (category == null) {
            return new ArrayList<>();
        }

        List<Product> categoryProducts = categoryIndex.get(category);
        if (categoryProducts == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(categoryProducts);
    }

    /**
     * 상품명으로 검색
     *
     * @param name 검색할 상품명
     * @return 검색된 상품 목록
     */
    public List<Product> findByNameContaining(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String searchName = name.toLowerCase().trim();

        return products.values().stream()
                .filter(product -> product.getName() != null &&
                        product.getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }

    /**
     * 가격 범위로 상품 검색
     *
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위에 해당하는 상품 목록
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        return products.values().stream()
                .filter(product -> product.getPrice() >= minPrice &&
                        product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * 재고가 있는 상품만 조회
     *
     * @return 재고가 있는 상품 목록
     */
    public List<Product> findAvailableProducts() {
        return products.values().stream()
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
    }

    /**
     * 재고 부족 상품 조회
     *
     * @param threshold 재고 임계값
     * @return 재고가 임계값 이하인 상품 목록
     */
    public List<Product> findLowStockProducts(int threshold) {
        return products.values().stream()
                .filter(product -> product.getStock() <= threshold)
                .collect(Collectors.toList());
    }

    /**
     * 품절 상품 조회
     *
     * @return 재고가 0인 상품 목록
     */
    public List<Product> findOutOfStockProducts() {
        return products.values().stream()
                .filter(product -> product.getStock() == 0)
                .collect(Collectors.toList());
    }

    /**
     * 상품 수정
     *
     * @param product 수정할 상품
     * @return 수정된 상품
     */
    public Product update(Product product) {
        if (product == null || product.getId() == null) {
            throw new IllegalArgumentException("상품 정보가 유효하지 않습니다.");
        }

        if (!products.containsKey(product.getId())) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다: " + product.getId());
        }

        return save(product);
    }

    /**
     * 상품 삭제
     *
     * @param id 삭제할 상품 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(String id) {
        if (id == null) {
            return false;
        }

        Product removed = products.remove(id);
        if (removed != null) {
            removeFromCategoryIndex(removed);
            return true;
        }

        return false;
    }

    /**
     * 상품 ID 존재 여부 확인
     *
     * @param id 확인할 ID
     * @return 존재하면 true
     */
    public boolean existsById(String id) {
        return id != null && products.containsKey(id);
    }

    /**
     * 상품 수 반환
     *
     * @return 전체 상품 수
     */
    public int count() {
        return products.size();
    }

    /**
     * 모든 데이터 삭제
     */
    public void deleteAll() {
        products.clear();
        categoryIndex.clear();
    }

    /**
     * 카테고리별 상품 수 조회
     *
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 수
     */
    public int countByCategory(String category) {
        List<Product> categoryProducts = categoryIndex.get(category);
        return categoryProducts != null ? categoryProducts.size() : 0;
    }

    /**
     * 모든 카테고리 목록 조회
     *
     * @return 카테고리 목록
     */
    public List<String> findAllCategories() {
        return new ArrayList<>(categoryIndex.keySet());
    }

    /**
     * 가격 기준 상위 N개 상품 조회
     *
     * @param limit 조회할 개수
     * @return 가격이 높은 상품 목록
     */
    public List<Product> findTopByPrice(int limit) {
        return products.values().stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 재고 기준 상위 N개 상품 조회
     *
     * @param limit 조회할 개수
     * @return 재고가 많은 상품 목록
     */
    public List<Product> findTopByStock(int limit) {
        return products.values().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 인덱스에 상품 추가
     *
     * @param product 추가할 상품
     */
    private void addToCategoryIndex(Product product) {
        if (product.getCategory() != null) {
            categoryIndex.computeIfAbsent(product.getCategory(),
                    k -> new ArrayList<>()).add(product);
        }
    }

    /**
     * 카테고리 인덱스에서 상품 제거
     *
     * @param product 제거할 상품
     */
    private void removeFromCategoryIndex(Product product) {
        if (product.getCategory() != null) {
            List<Product> categoryProducts = categoryIndex.get(product.getCategory());
            if (categoryProducts != null) {
                categoryProducts.remove(product);
                if (categoryProducts.isEmpty()) {
                    categoryIndex.remove(product.getCategory());
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
        int totalProducts = products.size();
        int availableProducts = findAvailableProducts().size();
        int outOfStock = findOutOfStockProducts().size();
        int categories = categoryIndex.size();

        double totalValue = products.values().stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();

        return String.format(
                "=== 상품 저장소 통계 ===\n" +
                        "전체 상품: %d개\n" +
                        "판매 가능: %d개\n" +
                        "품절: %d개\n" +
                        "카테고리: %d개\n" +
                        "총 재고 가치: %,.0f원",
                totalProducts, availableProducts, outOfStock, categories, totalValue
        );
    }

    /**
     * 페이징 처리된 상품 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 상품 목록
     */
    public List<Product> findAllPaged(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("유효하지 않은 페이징 파라미터입니다.");
        }

        List<Product> allProducts = findAll();
        int start = page * size;
        int end = Math.min(start + size, allProducts.size());

        if (start >= allProducts.size()) {
            return new ArrayList<>();
        }

        return allProducts.subList(start, end);
    }
}