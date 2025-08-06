package javaproject.test;

import javaproject.domain.Product;
import javaproject.repository.ProductRepository;
import java.util.List;

/**
 * ProductRepository 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class ProductRepositoryTest {

    private static ProductRepository repository;

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== ProductRepository 테스트 시작 ======");

        repository = ProductRepository.getInstance();
        repository.deleteAll(); // 테스트 시작 전 초기화

        testSaveProduct();
        testFindById();
        testFindByCategory();
        testFindByPriceRange();
        testUpdateProduct();
        testDeleteProduct();
        testStockManagement();

        repository.deleteAll(); // 테스트 후 정리

        System.out.println("====== ProductRepository 테스트 완료 ======\n");
    }

    /**
     * 상품 저장 테스트
     */
    private static void testSaveProduct() {
        System.out.println("\n[TEST] 상품 저장 테스트");

        try {
            Product product = new Product("노트북", "고성능 게이밍 노트북",
                    1500000, 10, "전자제품");

            Product saved = repository.save(product);

            assert saved != null : "저장된 상품이 null";
            assert saved.getId() != null : "상품 ID 생성 실패";
            assert repository.count() == 1 : "저장 후 카운트 오류";

            System.out.println("✅ 상품 저장 성공 - ID: " + saved.getId());

        } catch (Exception e) {
            System.err.println("❌ 상품 저장 실패: " + e.getMessage());
        }
    }

    /**
     * ID로 상품 조회 테스트
     */
    private static void testFindById() {
        System.out.println("\n[TEST] ID로 상품 조회 테스트");

        try {
            // 상품 저장
            Product product = new Product("마우스", "무선 마우스", 30000, 50, "전자제품");
            Product saved = repository.save(product);

            // ID로 조회
            Product found = repository.findById(saved.getId());
            assert found != null : "상품 조회 실패";
            assert found.getName().equals("마우스") : "조회된 상품 정보 불일치";

            // 존재하지 않는 ID 조회
            Product notFound = repository.findById("NOTEXIST");
            assert notFound == null : "존재하지 않는 상품 조회 오류";

            System.out.println("✅ ID로 상품 조회 성공");

        } catch (Exception e) {
            System.err.println("❌ ID로 상품 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 카테고리별 상품 조회 테스트
     */
    private static void testFindByCategory() {
        System.out.println("\n[TEST] 카테고리별 상품 조회 테스트");

        try {
            // 여러 카테고리 상품 저장
            repository.save(new Product("티셔츠", "면 티셔츠", 25000, 30, "의류"));
            repository.save(new Product("청바지", "데님 청바지", 55000, 20, "의류"));
            repository.save(new Product("키보드", "기계식 키보드", 120000, 15, "전자제품"));

            // 의류 카테고리 조회
            List<Product> clothing = repository.findByCategory("의류");
            assert clothing.size() == 2 : "의류 카테고리 조회 실패";
            System.out.println("✅ 의류 카테고리 상품 2개 조회 성공");

            // 전자제품 카테고리 조회 (이전 테스트 포함)
            List<Product> electronics = repository.findByCategory("전자제품");
            assert electronics.size() >= 2 : "전자제품 카테고리 조회 실패";
            System.out.println("✅ 전자제품 카테고리 상품 조회 성공");

            // 존재하지 않는 카테고리 조회
            List<Product> empty = repository.findByCategory("없는카테고리");
            assert empty.isEmpty() : "존재하지 않는 카테고리 조회 오류";
            System.out.println("✅ 빈 카테고리 처리 성공");

        } catch (Exception e) {
            System.err.println("❌ 카테고리별 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 가격 범위로 상품 조회 테스트
     */
    private static void testFindByPriceRange() {
        System.out.println("\n[TEST] 가격 범위로 상품 조회 테스트");

        try {
            // 다양한 가격대 상품 저장
            repository.save(new Product("상품1", "설명", 10000, 10, "테스트"));
            repository.save(new Product("상품2", "설명", 30000, 10, "테스트"));
            repository.save(new Product("상품3", "설명", 50000, 10, "테스트"));
            repository.save(new Product("상품4", "설명", 100000, 10, "테스트"));

            // 20,000 ~ 60,000원 범위 조회
            List<Product> inRange = repository.findByPriceRange(20000, 60000);
            assert inRange.size() >= 2 : "가격 범위 조회 실패";

            boolean hasInRange = inRange.stream()
                    .allMatch(p -> p.getPrice() >= 20000 && p.getPrice() <= 60000);
            assert hasInRange : "가격 범위 밖의 상품이 포함됨";

            System.out.println("✅ 가격 범위 조회 성공: " + inRange.size() + "개");

        } catch (Exception e) {
            System.err.println("❌ 가격 범위 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 상품 수정 테스트
     */
    private static void testUpdateProduct() {
        System.out.println("\n[TEST] 상품 수정 테스트");

        try {
            // 상품 저장
            Product product = new Product("수정테스트", "원본설명", 10000, 5, "테스트");
            Product saved = repository.save(product);

            // 정보 수정
            saved.setName("수정된이름");
            saved.setPrice(20000);
            saved.setStock(10);
            Product updated = repository.update(saved);

            // 수정 확인
            Product found = repository.findById(saved.getId());
            assert found.getName().equals("수정된이름") : "이름 수정 실패";
            assert found.getPrice() == 20000 : "가격 수정 실패";
            assert found.getStock() == 10 : "재고 수정 실패";

            System.out.println("✅ 상품 수정 성공");

        } catch (Exception e) {
            System.err.println("❌ 상품 수정 실패: " + e.getMessage());
        }
    }

    /**
     * 상품 삭제 테스트
     */
    private static void testDeleteProduct() {
        System.out.println("\n[TEST] 상품 삭제 테스트");

        try {
            // 상품 저장
            Product product = new Product("삭제테스트", "설명", 10000, 5, "테스트");
            Product saved = repository.save(product);
            String productId = saved.getId();

            int countBefore = repository.count();

            // 삭제
            boolean deleted = repository.delete(productId);
            assert deleted : "상품 삭제 실패";

            // 삭제 확인
            Product found = repository.findById(productId);
            assert found == null : "삭제된 상품이 조회됨";

            int countAfter = repository.count();
            assert countAfter == countBefore - 1 : "삭제 후 카운트 오류";

            System.out.println("✅ 상품 삭제 성공");

        } catch (Exception e) {
            System.err.println("❌ 상품 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 재고 관리 테스트
     */
    private static void testStockManagement() {
        System.out.println("\n[TEST] 재고 관리 테스트");

        try {
            // 재고별 상품 저장
            repository.save(new Product("재고많음", "설명", 10000, 100, "테스트"));
            repository.save(new Product("재고적음", "설명", 10000, 5, "테스트"));
            repository.save(new Product("품절", "설명", 10000, 0, "테스트"));

            // 재고 부족 상품 조회 (10개 이하)
            List<Product> lowStock = repository.findLowStockProducts(10);
            assert lowStock.size() >= 2 : "재고 부족 상품 조회 실패";
            System.out.println("✅ 재고 부족 상품 조회 성공: " + lowStock.size() + "개");

            // 품절 상품 조회
            List<Product> outOfStock = repository.findOutOfStockProducts();
            assert outOfStock.size() >= 1 : "품절 상품 조회 실패";
            System.out.println("✅ 품절 상품 조회 성공: " + outOfStock.size() + "개");

            // 판매 가능 상품 조회
            List<Product> available = repository.findAvailableProducts();
            boolean allAvailable = available.stream().allMatch(p -> p.getStock() > 0);
            assert allAvailable : "재고 없는 상품이 판매 가능으로 조회됨";
            System.out.println("✅ 판매 가능 상품 조회 성공: " + available.size() + "개");

        } catch (Exception e) {
            System.err.println("❌ 재고 관리 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}