package javaproject.service;

import javaproject.domain.Product;
import javaproject.repository.ProductRepository;
import javaproject.exception.ProductNotFoundException;
import javaproject.exception.DuplicateProductException;
import javaproject.exception.InsufficientStockException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class ProductService {

    // 싱글톤 인스턴스
    private static final ProductService instance = new ProductService();

    // Repository 인스턴스
    private final ProductRepository productRepository;

    // 카테고리 목록 (하드코딩된 기본 카테고리)
    private static final List<String> CATEGORIES = List.of(
            "전자제품", "의류", "도서", "식품", "가구", "스포츠", "뷰티", "완구", "기타"
    );

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private ProductService() {
        this.productRepository = ProductRepository.getInstance();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return ProductService의 유일한 인스턴스
     */
    public static ProductService getInstance() {
        return instance;
    }

    /**
     * 상품 ID로 상품 조회
     *
     * @param productId 상품 ID
     * @return 조회된 상품, 없으면 null
     */
    public Product getProductById(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return null;
        }

        return productRepository.findById(productId);
    }

    /**
     * 상품 ID로 상품 조회 (예외 발생 버전)
     *
     * @param productId 상품 ID
     * @return 조회된 상품
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    public Product getProductByIdOrThrow(String productId) throws ProductNotFoundException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 ID가 유효하지 않습니다.");
        }

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다. ID: " + productId);
        }

        return product;
    }

    /**
     * 모든 상품 조회
     *
     * @return 전체 상품 목록
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(productRepository.findAll());
    }

    /**
     * 판매 가능한 상품만 조회 (재고가 있는 상품)
     *
     * @return 판매 가능한 상품 목록
     */
    public List<Product> getAvailableProducts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 상품 조회
     *
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return productRepository.findAll().stream()
                .filter(product -> category.equalsIgnoreCase(product.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * 상품명으로 검색
     * 부분 일치 검색 지원
     *
     * @param keyword 검색 키워드
     * @return 검색된 상품 목록
     */
    public List<Product> searchProductsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        return productRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 가격 범위로 상품 검색
     *
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위에 해당하는 상품 목록
     */
    public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < minPrice) {
            throw new IllegalArgumentException("유효하지 않은 가격 범위입니다.");
        }

        return productRepository.findAll().stream()
                .filter(product -> product.getPrice() >= minPrice &&
                        product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * 새 상품 등록
     *
     * @param product 등록할 상품
     * @return 등록된 상품
     * @throws DuplicateProductException 동일한 ID의 상품이 이미 존재하는 경우
     */
    public Product addProduct(Product product) throws DuplicateProductException {
        if (product == null) {
            throw new IllegalArgumentException("상품 정보가 null입니다.");
        }

        // 상품 정보 유효성 검증
        validateProduct(product);

        // 중복 확인
        if (productRepository.findById(product.getId()) != null) {
            throw new DuplicateProductException("이미 존재하는 상품 ID입니다: " + product.getId());
        }

        // 상품 저장
        productRepository.save(product);

        return product;
    }

    /**
     * 상품 정보 수정
     *
     * @param productId 수정할 상품 ID
     * @param updatedProduct 수정된 상품 정보
     * @return 수정된 상품
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    public Product updateProduct(String productId, Product updatedProduct)
            throws ProductNotFoundException {

        if (productId == null || updatedProduct == null) {
            throw new IllegalArgumentException("상품 ID와 수정 정보는 필수입니다.");
        }

        // 기존 상품 조회
        Product existingProduct = getProductByIdOrThrow(productId);

        // 상품 정보 업데이트
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setCategory(updatedProduct.getCategory());

        // 유효성 검증
        validateProduct(existingProduct);

        // 저장
        productRepository.update(existingProduct);

        return existingProduct;
    }

    /**
     * 상품 삭제
     *
     * @param productId 삭제할 상품 ID
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    public void deleteProduct(String productId) throws ProductNotFoundException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 ID가 유효하지 않습니다.");
        }

        Product product = getProductByIdOrThrow(productId);
        productRepository.delete(product.getId());
    }

    /**
     * 재고 추가
     *
     * @param productId 상품 ID
     * @param quantity 추가할 수량
     * @return 업데이트된 상품
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    public Product addStock(String productId, int quantity)
            throws ProductNotFoundException {

        if (quantity <= 0) {
            throw new IllegalArgumentException("추가 수량은 1개 이상이어야 합니다.");
        }

        Product product = getProductByIdOrThrow(productId);
        product.setStock(product.getStock() + quantity);
        productRepository.update(product);

        return product;
    }

    /**
     * 재고 감소 (주문 시 사용)
     *
     * @param productId 상품 ID
     * @param quantity 감소시킬 수량
     * @return 업데이트된 상품
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws InsufficientStockException 재고가 부족한 경우
     */
    public Product reduceStock(String productId, int quantity)
            throws ProductNotFoundException, InsufficientStockException {

        if (quantity <= 0) {
            throw new IllegalArgumentException("감소 수량은 1개 이상이어야 합니다.");
        }

        Product product = getProductByIdOrThrow(productId);

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    String.format("재고가 부족합니다. 상품: %s, 재고: %d, 요청: %d",
                            product.getName(), product.getStock(), quantity)
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.update(product);

        return product;
    }

    /**
     * 재고 확인
     *
     * @param productId 상품 ID
     * @param requiredQuantity 필요한 수량
     * @return 재고가 충분하면 true, 부족하면 false
     */
    public boolean checkStock(String productId, int requiredQuantity) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        return product.getStock() >= requiredQuantity;
    }

    /**
     * 카테고리 목록 조회
     *
     * @return 카테고리 목록
     */
    public List<String> getCategories() {
        return new ArrayList<>(CATEGORIES);
    }

    /**
     * 베스트셀러 상품 조회 (더미 구현)
     * 실제로는 주문 데이터를 기반으로 계산해야 함
     *
     * @param limit 조회할 개수
     * @return 베스트셀러 상품 목록
     */
    public List<Product> getBestSellers(int limit) {
        List<Product> allProducts = getAllProducts();

        // 가격이 높은 순으로 정렬 (실제로는 판매량 기준이어야 함)
        allProducts.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));

        return allProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 신상품 조회 (더미 구현)
     * 실제로는 등록일자를 기반으로 조회해야 함
     *
     * @param limit 조회할 개수
     * @return 신상품 목록
     */
    public List<Product> getNewProducts(int limit) {
        List<Product> allProducts = getAllProducts();

        // 역순으로 정렬 (최근 추가된 상품이 먼저)
        List<Product> reversed = new ArrayList<>(allProducts);
        java.util.Collections.reverse(reversed);

        return reversed.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 재고 부족 상품 조회
     *
     * @param threshold 재고 임계값
     * @return 재고가 임계값 이하인 상품 목록
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findAll().stream()
                .filter(product -> product.getStock() <= threshold)
                .collect(Collectors.toList());
    }

    /**
     * 상품 정보 유효성 검증
     *
     * @param product 검증할 상품
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }

        if (product.getName().length() > 100) {
            throw new IllegalArgumentException("상품명은 100자를 초과할 수 없습니다.");
        }

        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }

        if (product.getPrice() > 1000000000) {
            throw new IllegalArgumentException("가격은 10억원을 초과할 수 없습니다.");
        }

        if (product.getStock() < 0) {
            throw new IllegalArgumentException("재고는 0개 이상이어야 합니다.");
        }

        if (product.getStock() > 10000) {
            throw new IllegalArgumentException("재고는 10,000개를 초과할 수 없습니다.");
        }

        if (product.getCategory() != null &&
                !CATEGORIES.contains(product.getCategory())) {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다: " +
                    product.getCategory());
        }
    }

    /**
     * 상품 통계 정보
     */
    public static class ProductStatistics {
        private int totalProducts;
        private int availableProducts;
        private int outOfStockProducts;
        private double totalInventoryValue;
        private double averagePrice;

        // Getters and Setters
        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) {
            this.totalProducts = totalProducts;
        }

        public int getAvailableProducts() { return availableProducts; }
        public void setAvailableProducts(int availableProducts) {
            this.availableProducts = availableProducts;
        }

        public int getOutOfStockProducts() { return outOfStockProducts; }
        public void setOutOfStockProducts(int outOfStockProducts) {
            this.outOfStockProducts = outOfStockProducts;
        }

        public double getTotalInventoryValue() { return totalInventoryValue; }
        public void setTotalInventoryValue(double totalInventoryValue) {
            this.totalInventoryValue = totalInventoryValue;
        }

        public double getAveragePrice() { return averagePrice; }
        public void setAveragePrice(double averagePrice) {
            this.averagePrice = averagePrice;
        }
    }

    /**
     * 상품 통계 조회
     *
     * @return 상품 통계 정보
     */
    public ProductStatistics getStatistics() {
        ProductStatistics stats = new ProductStatistics();
        List<Product> allProducts = getAllProducts();

        stats.setTotalProducts(allProducts.size());

        int available = 0;
        int outOfStock = 0;
        double totalValue = 0;
        double totalPrice = 0;

        for (Product product : allProducts) {
            if (product.getStock() > 0) {
                available++;
            } else {
                outOfStock++;
            }

            totalValue += product.getPrice() * product.getStock();
            totalPrice += product.getPrice();
        }

        stats.setAvailableProducts(available);
        stats.setOutOfStockProducts(outOfStock);
        stats.setTotalInventoryValue(totalValue);

        if (!allProducts.isEmpty()) {
            stats.setAveragePrice(totalPrice / allProducts.size());
        }

        return stats;
    }
}