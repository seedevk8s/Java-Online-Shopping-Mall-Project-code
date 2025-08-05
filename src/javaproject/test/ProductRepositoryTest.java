// =================================================================
// ProductRepositoryTest.java - 상품 Repository 테스트
// =================================================================
package javaproject.test;

import javaproject.domain.Product;
import javaproject.exception.FileIOException;
import javaproject.exception.InsufficientStockException;
import javaproject.exception.ProductNotFoundException;
import javaproject.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository 기능을 테스트하는 클래스
 */
public class ProductRepositoryTest {

    private ProductRepository productRepository;

    /**
     * 테스트 초기화
     */
    public ProductRepositoryTest() {
        this.productRepository = ProductRepository.getInstance();
    }

    /**
     * 모든 테스트 실행
     */
    public void runAllTests() {
        System.out.println("========== ProductRepository 테스트 시작 ==========");

        try {
            testSaveProduct();
            testFindProductById();
            testSearchProducts();
            testUpdateStock();
            testCategorySearch();
            testPriceRangeSearch();
            testProductStatistics();
            testDeleteProduct();

            System.out.println("✅ ProductRepository 모든 테스트 통과!");

        } catch (Exception e) {
            System.err.println("❌ ProductRepository 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("========== ProductRepository 테스트 완료 ==========\n");
    }

    /**
     * 상품 저장 테스트
     */
    private void testSaveProduct() throws FileIOException {
        System.out.println("📋 상품 저장 테스트...");

        // 테스트 상품 생성
        Product testProduct = new Product("테스트상품", 50000, "테스트카테고리", 100, "테스트용 상품입니다.");

        // 저장 테스트
        boolean saved = productRepository.save(testProduct);
        assert saved : "상품 저장 실패";

        System.out.println("   ✅ 상품 저장 테스트 통과 (상품 ID: " + testProduct.getProductId() + ")");
    }

    /**
     * 상품 ID로 조회 테스트
     */
    private void testFindProductById() throws FileIOException {
        System.out.println("📋 상품 ID 조회 테스트...");

        // 모든 상품 조회해서 첫 번째 상품 사용
        List<Product> products = productRepository.findAll();
        assert !products.isEmpty() : "저장된 상품이 없음";

        Product firstProduct = products.get(0);
        String productId = firstProduct.getProductId();

        // 존재하는 상품 조회
        Optional<Product> foundProduct = productRepository.findById(productId);
        assert foundProduct.isPresent() : "저장된 상품을 찾을 수 없음";
        assert productId.equals(foundProduct.get().getProductId()) : "상품 ID가 일치하지 않음";

        // 존재하지 않는 상품 조회
        Optional<Product> notFoundProduct = productRepository.findById("P999");
        assert !notFoundProduct.isPresent() : "존재하지 않는 상품이 조회됨";

        System.out.println("   ✅ 상품 ID 조회 테스트 통과");
    }

    /**
     * 상품 검색 테스트
     */
    private void testSearchProducts() throws FileIOException {
        System.out.println("📋 상품 검색 테스트...");

        // 이름으로 검색
        List<Product> searchResults = productRepository.findByNameContaining("테스트");
        assert !searchResults.isEmpty() : "이름 검색 결과가 없음";

        // 검색 결과 확인
        boolean foundTestProduct = false;
        for (Product product : searchResults) {
            if (product.getName().contains("테스트")) {
                foundTestProduct = true;
                break;
            }
        }
        assert foundTestProduct : "검색 결과에 테스트 상품이 없음";

        System.out.println("   ✅ 상품 검색 테스트 통과");
    }

    /**
     * 재고 업데이트 테스트
     */
    private void testUpdateStock() throws FileIOException, ProductNotFoundException, InsufficientStockException {
        System.out.println("📋 재고 업데이트 테스트...");

        // 첫 번째 상품 사용
        List<Product> products = productRepository.findAll();
        Product testProduct = products.get(0);
        String productId = testProduct.getProductId();
        int originalStock = testProduct.getStock();

        // 재고 감소 테스트
        boolean decreased = productRepository.decreaseStock(productId, 5);
        assert decreased : "재고 감소 실패";

        // 변경 확인
        Optional<Product> updatedProductOpt = productRepository.findById(productId);
        assert updatedProductOpt.isPresent() : "재고 변경된 상품을 찾을 수 없음";
        assert updatedProductOpt.get().getStock() == originalStock - 5 : "재고가 올바르게 감소되지 않음";

        // 재고 증가 테스트
        boolean increased = productRepository.increaseStock(productId, 3);
        assert increased : "재고 증가 실패";

        // 변경 확인
        Optional<Product> finalProductOpt = productRepository.findById(productId);
        assert finalProductOpt.isPresent() : "재고 변경된 상품을 찾을 수 없음";
        assert finalProductOpt.get().getStock() == originalStock - 2 : "재고가 올바르게 증가되지 않음";

        System.out.println("   ✅ 재고 업데이트 테스트 통과");
    }

    /**
     * 카테고리 검색 테스트
     */
    private void testCategorySearch() throws FileIOException {
        System.out.println("📋 카테고리 검색 테스트...");

        // 모든 카테고리 조회
        List<String> categories = productRepository.findAllCategories();
        assert !categories.isEmpty() : "카테고리가 없음";

        // 첫 번째 카테고리로 상품 검색
        String firstCategory = categories.get(0);
        List<Product> categoryProducts = productRepository.findByCategory(firstCategory);
        assert !categoryProducts.isEmpty() : "카테고리 상품이 없음";

        // 검색 결과 확인
        for (Product product : categoryProducts) {
            assert firstCategory.equals(product.getCategory()) : "카테고리가 일치하지 않음";
        }

        System.out.println("   ✅ 카테고리 검색 테스트 통과 (카테고리 수: " + categories.size() + ")");
    }

    /**
     * 가격 범위 검색 테스트
     */
    private void testPriceRangeSearch() throws FileIOException {
        System.out.println("📋 가격 범위 검색 테스트...");

        // 가격 범위로 검색 (10,000원 ~ 100,000원)
        List<Product> priceRangeProducts = productRepository.findByPriceRange(10000, 100000);

        // 검색 결과 확인
        for (Product product : priceRangeProducts) {
            int price = product.getPrice();
            assert price >= 10000 && price <= 100000 : "가격 범위를 벗어난 상품이 검색됨: " + price;
        }

        System.out.println("   ✅ 가격 범위 검색 테스트 통과 (검색 결과: " + priceRangeProducts.size() + "개)");
    }

    /**
     * 상품 통계 테스트
     */
    private void testProductStatistics() throws FileIOException {
        System.out.println("📋 상품 통계 테스트...");

        String statistics = productRepository.getProductStatistics();
        assert statistics != null && !statistics.isEmpty() : "상품 통계가 비어있음";

        String categoryStats = productRepository.getCategoryStatistics();
        assert categoryStats != null && !categoryStats.isEmpty() : "카테고리 통계가 비어있음";

        System.out.println("   ✅ 상품 통계 테스트 통과");
        System.out.println(statistics);
        System.out.println(categoryStats);
    }

    /**
     * 상품 삭제 테스트 (마지막에 실행)
     */
    private void testDeleteProduct() throws FileIOException {
        System.out.println("📋 상품 삭제 테스트...");

        // 테스트용 상품 찾기
        List<Product> products = productRepository.findByNameContaining("테스트상품");
        if (!products.isEmpty()) {
            Product testProduct = products.get(0);
            String productId = testProduct.getProductId();

            // 삭제 전 존재 확인
            assert productRepository.existsById(productId) : "삭제할 상품이 존재하지 않음";

            // 삭제 실행
            boolean deleted = productRepository.deleteById(productId);
            assert deleted : "상품 삭제 실패";

            // 삭제 후 존재하지 않음 확인
            assert !productRepository.existsById(productId) : "삭제된 상품이 여전히 존재함";

            System.out.println("   ✅ 상품 삭제 테스트 통과");
        } else {
            System.out.println("   ⚠️ 삭제할 테스트 상품을 찾을 수 없음");
        }
    }
}