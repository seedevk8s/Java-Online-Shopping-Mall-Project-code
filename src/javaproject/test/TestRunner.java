// =================================================================
// TestRunner.java - 모든 테스트 실행
// =================================================================
package javaproject.test;

/**
 * 모든 Repository 테스트를 실행하는 메인 클래스
 */
public class TestRunner {

    /**
     * 모든 테스트 실행
     */
    public static void main(String[] args) {
        System.out.println("🧪 Repository 테스트 시작");
        System.out.println("=======================================");

        try {
            // 각 Repository 테스트 실행
            UserRepositoryTest userTest = new UserRepositoryTest();
            userTest.runAllTests();

            ProductRepositoryTest productTest = new ProductRepositoryTest();
            productTest.runAllTests();

            CartRepositoryTest cartTest = new CartRepositoryTest();
            cartTest.runAllTests();

            System.out.println("🎉 모든 Repository 테스트 완료!");
            System.out.println("=======================================");

        } catch (Exception e) {
            System.err.println("💥 테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 개별 테스트 실행 (선택적)
     * @param testType 테스트 종류 ("user", "product", "cart", "all")
     */
    public static void runSpecificTest(String testType) {
        switch (testType.toLowerCase()) {
            case "user":
                System.out.println("🧪 UserRepository 테스트만 실행");
                new UserRepositoryTest().runAllTests();
                break;

            case "product":
                System.out.println("🧪 ProductRepository 테스트만 실행");
                new ProductRepositoryTest().runAllTests();
                break;

            case "cart":
                System.out.println("🧪 CartRepository 테스트만 실행");
                new CartRepositoryTest().runAllTests();
                break;

            case "all":
            default:
                main(new String[0]); // 모든 테스트 실행
                break;
        }
    }
}

