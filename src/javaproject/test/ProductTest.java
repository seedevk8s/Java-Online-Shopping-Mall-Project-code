package javaproject.test;

import javaproject.domain.Product;

/**
 * Product 도메인 클래스 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class ProductTest {

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== Product 도메인 테스트 시작 ======");

        testProductCreation();
        testStockManagement();
        testPriceCalculation();
        testProductAvailability();
        testProductValidation();

        System.out.println("====== Product 도메인 테스트 완료 ======\n");
    }

    /**
     * 상품 생성 테스트
     */
    private static void testProductCreation() {
        System.out.println("\n[TEST] 상품 생성 테스트");

        try {
            // 상품 생성
            Product product = new Product("노트북", "고성능 게이밍 노트북",
                    1500000, 10, "전자제품");

            assert product.getId() != null : "ID 자동 생성 실패";
            assert product.getId().startsWith("PRD") : "ID 형식 오류";
            assert product.getName().equals("노트북") : "상품명 설정 실패";
            assert product.getPrice() == 1500000 : "가격 설정 실패";
            assert product.getStock() == 10 : "재고 설정 실패";
            assert product.getCategory().equals("전자제품") : "카테고리 설정 실패";

            System.out.println("✅ 상품 생성 성공 - ID: " + product.getId());

        } catch (Exception e) {
            System.err.println("❌ 상품 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 재고 관리 테스트
     */
    private static void testStockManagement() {
        System.out.println("\n[TEST] 재고 관리 테스트");

        try {
            Product product = new Product("테스트상품", "설명", 10000, 20, "테스트");

            // 재고 추가
            product.addStock(10);
            assert product.getStock() == 30 : "재고 추가 실패";
            System.out.println("✅ 재고 추가 성공: 20 -> 30");

            // 재고 감소 (성공 케이스)
            boolean reduced = product.reduceStock(5);
            assert reduced : "재고 감소 실패";
            assert product.getStock() == 25 : "재고 감소 계산 오류";
            System.out.println("✅ 재고 감소 성공: 30 -> 25");

            // 재고 감소 (실패 케이스 - 재고 부족)
            boolean failedReduce = product.reduceStock(30);
            assert !failedReduce : "재고 부족 체크 실패";
            assert product.getStock() == 25 : "재고가 잘못 감소됨";
            System.out.println("✅ 재고 부족 체크 성공");

            // 재고 확인
            assert product.hasStock(20) : "재고 확인 실패";
            assert !product.hasStock(30) : "재고 부족 확인 실패";
            System.out.println("✅ 재고 확인 기능 성공");

        } catch (Exception e) {
            System.err.println("❌ 재고 관리 실패: " + e.getMessage());
        }
    }

    /**
     * 가격 계산 테스트
     */
    private static void testPriceCalculation() {
        System.out.println("\n[TEST] 가격 계산 테스트");

        try {
            Product product = new Product("테스트상품", "설명", 100000, 10, "테스트");

            // 10% 할인
            double discounted10 = product.getDiscountedPrice(10);
            assert discounted10 == 90000 : "10% 할인 계산 오류";
            System.out.println("✅ 10% 할인 계산 성공: 100,000 -> 90,000");

            // 50% 할인
            double discounted50 = product.getDiscountedPrice(50);
            assert discounted50 == 50000 : "50% 할인 계산 오류";
            System.out.println("✅ 50% 할인 계산 성공: 100,000 -> 50,000");

            // 0% 할인
            double discounted0 = product.getDiscountedPrice(0);
            assert discounted0 == 100000 : "0% 할인 계산 오류";
            System.out.println("✅ 0% 할인 계산 성공: 100,000 -> 100,000");

            // 잘못된 할인율 테스트
            try {
                product.getDiscountedPrice(-10);
                assert false : "음수 할인율 체크 실패";
            } catch (IllegalArgumentException e) {
                System.out.println("✅ 잘못된 할인율 체크 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 가격 계산 실패: " + e.getMessage());
        }
    }

    /**
     * 상품 가용성 테스트
     */
    private static void testProductAvailability() {
        System.out.println("\n[TEST] 상품 가용성 테스트");

        try {
            // 재고가 있는 상품
            Product availableProduct = new Product("상품1", "설명", 10000, 10, "테스트");
            assert availableProduct.isAvailable() : "재고 있는 상품 가용성 체크 실패";
            System.out.println("✅ 재고 있는 상품 가용성 확인");

            // 재고가 없는 상품
            Product outOfStock = new Product("상품2", "설명", 10000, 0, "테스트");
            assert !outOfStock.isAvailable() : "재고 없는 상품 가용성 체크 실패";
            System.out.println("✅ 재고 없는 상품 비가용성 확인");

            // 판매 중지된 상품
            Product unavailable = new Product("상품3", "설명", 10000, 10, "테스트");
            unavailable.setAvailable(false);
            assert !unavailable.isAvailable() : "판매 중지 상품 가용성 체크 실패";
            System.out.println("✅ 판매 중지 상품 비가용성 확인");

        } catch (Exception e) {
            System.err.println("❌ 상품 가용성 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 상품 유효성 검증 테스트
     */
    private static void testProductValidation() {
        System.out.println("\n[TEST] 상품 유효성 검증 테스트");

        try {
            // 유효한 상품
            Product validProduct = new Product("상품", "설명", 10000, 10, "카테고리");
            assert validProduct.isValid() : "유효한 상품 검증 실패";
            System.out.println("✅ 유효한 상품 검증 성공");

            // 유효하지 않은 상품 (이름 없음)
            Product invalidProduct = new Product();
            assert !invalidProduct.isValid() : "유효하지 않은 상품 검증 실패";
            System.out.println("✅ 유효하지 않은 상품 검증 성공");

            // 가격 음수 테스트
            try {
                Product negativePrice = new Product("상품", "설명", -1000, 10, "카테고리");
                negativePrice.setPrice(-1000);
                assert false : "음수 가격 체크 실패";
            } catch (IllegalArgumentException e) {
                System.out.println("✅ 음수 가격 체크 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 상품 유효성 검증 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}