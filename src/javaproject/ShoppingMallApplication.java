package javaproject;

import javaproject.controller.MainController;
import javaproject.repository.*;
import javaproject.service.*;
import javaproject.util.FileManager;
import javaproject.domain.User;
import javaproject.domain.Product;
import java.util.Arrays;
import java.util.List;

/**
 * 쇼핑몰 애플리케이션의 메인 클래스
 * 프로그램의 시작점(Entry Point)을 제공하고 초기화 작업을 수행
 *
 * 주요 기능:
 * 1. 프로그램 시작 및 초기화
 * 2. 샘플 데이터 로드
 * 3. 메인 컨트롤러 실행
 * 4. 종료 시 리소스 정리
 *
 * @author ShoppingMall Team
 * @version 1.0
 * @since 2024-01-01
 */
public class ShoppingMallApplication {

    // 애플리케이션 버전 정보 - 배포 시 업데이트
    private static final String APPLICATION_VERSION = "1.0.0";

    // 애플리케이션 이름
    private static final String APPLICATION_NAME = "Java Shopping Mall";

    // 디버그 모드 플래그 - true로 설정하면 상세 로그 출력
    private static final boolean DEBUG_MODE = false;

    /**
     * 프로그램의 메인 메서드
     * JVM이 프로그램을 시작할 때 가장 먼저 호출되는 메서드
     *
     * @param args 커맨드 라인 인자 배열 (현재는 사용하지 않음)
     */
    public static void main(String[] args) {
        try {
            // 1. 애플리케이션 시작 로그 출력
            printStartupBanner();

            // 2. 시스템 초기화
            initializeSystem();

            // 3. 데이터 로드 (파일에서 기존 데이터 불러오기)
            loadData();

            // 4. 샘플 데이터 생성 (첫 실행 시)
            createSampleDataIfNeeded();

            // 5. 종료 훅 등록 (프로그램 종료 시 자동으로 cleanup 수행)
            registerShutdownHook();

            // 6. 메인 컨트롤러 실행 (실제 프로그램 시작)
            runApplication();

        } catch (Exception e) {
            // 예상치 못한 치명적 오류 처리
            handleFatalError(e);
        }
    }

    /**
     * 시작 배너 출력 메서드
     * 프로그램 시작 시 애플리케이션 정보를 표시
     */
    private static void printStartupBanner() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║              " + APPLICATION_NAME + " v" + APPLICATION_VERSION + "              ║");
        System.out.println("║                                                        ║");
        System.out.println("║            🛍️  Shopping Made Simple 🛍️                ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();

        // 디버그 모드인 경우 추가 정보 출력
        if (DEBUG_MODE) {
            System.out.println("🔧 Debug Mode: ON");
            System.out.println("📁 Working Directory: " + System.getProperty("user.dir"));
            System.out.println("☕ Java Version: " + System.getProperty("java.version"));
            System.out.println("💻 OS: " + System.getProperty("os.name"));
            System.out.println();
        }
    }

    /**
     * 시스템 초기화 메서드
     * 필요한 디렉토리 생성, 설정 파일 로드 등의 초기화 작업 수행
     */
    private static void initializeSystem() {
        System.out.println("⚙️  시스템 초기화 중...");

        try {
            // 1. 데이터 저장 디렉토리 생성 (없는 경우)
            FileManager fileManager = FileManager.getInstance();
            fileManager.createDataDirectoryIfNotExists();

            // 2. 각 Repository 초기화
            // Repository들은 싱글톤이므로 getInstance() 호출로 초기화
            UserRepository.getInstance();
            ProductRepository.getInstance();
            OrderRepository.getInstance();
            OrderItemRepository.getInstance();
            CartRepository.getInstance();

            // 3. 각 Service 초기화
            // Service들도 싱글톤이므로 getInstance() 호출로 초기화
            UserService.getInstance();
            ProductService.getInstance();
            CartService.getInstance();
            OrderService.getInstance();

            System.out.println("✅ 시스템 초기화 완료\n");

        } catch (Exception e) {
            // 초기화 실패는 치명적 오류
            throw new RuntimeException("시스템 초기화 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 데이터 로드 메서드
     * 파일에 저장된 기존 데이터를 메모리로 불러오기
     */
    private static void loadData() {
        System.out.println("📂 데이터 로드 중...");

        try {
            // FileManager를 통해 각 Repository의 데이터 로드
            FileManager fileManager = FileManager.getInstance();

            // 1. 사용자 데이터 로드
            int userCount = fileManager.loadUsers();
            System.out.println("  • 사용자 데이터: " + userCount + "명 로드됨");

            // 2. 상품 데이터 로드
            int productCount = fileManager.loadProducts();
            System.out.println("  • 상품 데이터: " + productCount + "개 로드됨");

            // 3. 주문 데이터 로드
            int orderCount = fileManager.loadOrders();
            System.out.println("  • 주문 데이터: " + orderCount + "건 로드됨");

            // 4. 장바구니 데이터 로드
            int cartCount = fileManager.loadCarts();
            System.out.println("  • 장바구니 데이터: " + cartCount + "개 로드됨");

            System.out.println("✅ 데이터 로드 완료\n");

        } catch (Exception e) {
            // 데이터 로드 실패는 경고만 표시 (새로 시작 가능)
            System.err.println("⚠️  데이터 로드 중 오류 발생: " + e.getMessage());
            System.out.println("새로운 데이터로 시작합니다.\n");
        }
    }

    /**
     * 샘플 데이터 생성 메서드
     * 첫 실행이거나 데이터가 없을 때 테스트용 샘플 데이터 생성
     */
    private static void createSampleDataIfNeeded() {
        try {
            UserRepository userRepo = UserRepository.getInstance();
            ProductRepository productRepo = ProductRepository.getInstance();

            // 사용자가 하나도 없으면 샘플 데이터 생성
            if (userRepo.findAll().isEmpty()) {
                System.out.println("📝 샘플 데이터 생성 중...");

                // 1. 관리자 계정 생성
                User admin = new User(
                        "admin",           // ID
                        "admin123",        // Password
                        "관리자",          // Name
                        "admin@shop.com",  // Email
                        "010-0000-0000",   // Phone
                        "서울시 강남구",   // Address
                        true               // isAdmin
                );
                userRepo.save(admin);
                System.out.println("  • 관리자 계정 생성 (ID: admin, PW: admin123)");

                // 2. 일반 사용자 계정 생성
                User user1 = new User(
                        "user1",
                        "user123",
                        "홍길동",
                        "hong@shop.com",
                        "010-1111-1111",
                        "서울시 종로구",
                        false
                );
                userRepo.save(user1);
                System.out.println("  • 일반 사용자 계정 생성 (ID: user1, PW: user123)");

                User user2 = new User(
                        "user2",
                        "user123",
                        "김철수",
                        "kim@shop.com",
                        "010-2222-2222",
                        "서울시 서초구",
                        false
                );
                userRepo.save(user2);
                System.out.println("  • 일반 사용자 계정 생성 (ID: user2, PW: user123)");

                // 3. 샘플 상품 데이터 생성
                createSampleProducts();

                System.out.println("✅ 샘플 데이터 생성 완료\n");
            }

        } catch (Exception e) {
            // 샘플 데이터 생성 실패는 경고만 표시
            System.err.println("⚠️  샘플 데이터 생성 중 오류: " + e.getMessage());
        }
    }

    /**
     * 샘플 상품 데이터 생성 메서드
     * 다양한 카테고리의 테스트용 상품 데이터 생성
     */
    private static void createSampleProducts() {
        ProductRepository productRepo = ProductRepository.getInstance();

        // 이미 상품이 있으면 생성하지 않음
        if (!productRepo.findAll().isEmpty()) {
            return;
        }

        // 샘플 상품 목록 정의
        List<Product> sampleProducts = Arrays.asList(
                // 전자제품 카테고리
                new Product("노트북", "고성능 게이밍 노트북", 1500000, 10, "전자제품"),
                new Product("스마트폰", "최신 5G 스마트폰", 1200000, 15, "전자제품"),
                new Product("태블릿", "10인치 태블릿 PC", 600000, 20, "전자제품"),
                new Product("이어폰", "무선 블루투스 이어폰", 150000, 30, "전자제품"),
                new Product("스마트워치", "피트니스 추적 스마트워치", 300000, 25, "전자제품"),

                // 의류 카테고리
                new Product("티셔츠", "순면 반팔 티셔츠", 25000, 50, "의류"),
                new Product("청바지", "스트레이트 핏 청바지", 55000, 40, "의류"),
                new Product("자켓", "방풍 스포츠 자켓", 85000, 20, "의류"),
                new Product("운동화", "런닝화", 95000, 35, "의류"),
                new Product("모자", "야구 모자", 20000, 60, "의류"),

                // 도서 카테고리
                new Product("자바 프로그래밍", "초보자를 위한 자바 입문서", 28000, 30, "도서"),
                new Product("알고리즘 정복", "코딩 테스트 대비 알고리즘", 32000, 25, "도서"),
                new Product("클린 코드", "깨끗한 코드 작성법", 29000, 20, "도서"),
                new Product("디자인 패턴", "GoF 디자인 패턴", 35000, 15, "도서"),
                new Product("스프링 부트", "스프링 부트 실전 가이드", 33000, 18, "도서"),

                // 식품 카테고리
                new Product("커피", "프리미엄 원두 커피 1kg", 25000, 40, "식품"),
                new Product("초콜릿", "벨기에 수제 초콜릿", 15000, 50, "식품"),
                new Product("꿀", "천연 아카시아 꿀 500g", 20000, 30, "식품"),
                new Product("올리브오일", "엑스트라 버진 올리브오일", 18000, 25, "식품"),
                new Product("견과류", "믹스넛 500g", 12000, 45, "식품")
        );

        // 상품 저장
        for (Product product : sampleProducts) {
            productRepo.save(product);
        }

        System.out.println("  • 샘플 상품 " + sampleProducts.size() + "개 생성됨");
    }

    /**
     * 종료 훅 등록 메서드
     * JVM 종료 시 자동으로 cleanup 작업을 수행하도록 설정
     */
    private static void registerShutdownHook() {
        // Runtime.addShutdownHook()을 사용하여 종료 시 실행될 스레드 등록
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\n🔄 프로그램 종료 중...");

            try {
                // 1. 현재 데이터를 파일에 저장
                saveAllData();

                // 2. 메인 컨트롤러 cleanup
                MainController.getInstance().cleanup();

                // 3. 기타 리소스 정리
                cleanupResources();

                System.out.println("✅ 정상적으로 종료되었습니다.");

            } catch (Exception e) {
                System.err.println("⚠️  종료 처리 중 오류: " + e.getMessage());
            }
        }));

        if (DEBUG_MODE) {
            System.out.println("✅ 종료 훅 등록 완료");
        }
    }

    /**
     * 애플리케이션 실행 메서드
     * 메인 컨트롤러를 시작하여 실제 프로그램 실행
     */
    private static void runApplication() {
        try {
            System.out.println("🚀 애플리케이션 시작!\n");
            System.out.println("=" .repeat(50));
            System.out.println();

            // 메인 컨트롤러 인스턴스를 가져와서 실행
            MainController mainController = MainController.getInstance();
            mainController.run();  // start()가 아닌 run() 메서드 호출

        } catch (Exception e) {
            // 실행 중 발생한 예외 처리
            System.err.println("\n❌ 애플리케이션 실행 중 오류 발생!");
            System.err.println("오류 메시지: " + e.getMessage());

            if (DEBUG_MODE) {
                // 디버그 모드에서는 상세 스택 트레이스 출력
                e.printStackTrace();
            }
        }
    }

    /**
     * 모든 데이터를 파일에 저장하는 메서드
     * 프로그램 종료 시 메모리의 데이터를 영구 저장
     */
    private static void saveAllData() {
        System.out.println("💾 데이터 저장 중...");

        try {
            FileManager fileManager = FileManager.getInstance();

            // 1. 사용자 데이터 저장
            fileManager.saveUsers();
            System.out.println("  • 사용자 데이터 저장 완료");

            // 2. 상품 데이터 저장
            fileManager.saveProducts();
            System.out.println("  • 상품 데이터 저장 완료");

            // 3. 주문 데이터 저장
            fileManager.saveOrders();
            System.out.println("  • 주문 데이터 저장 완료");

            // 4. 장바구니 데이터 저장
            fileManager.saveCarts();
            System.out.println("  • 장바구니 데이터 저장 완료");

            System.out.println("✅ 모든 데이터 저장 완료");

        } catch (Exception e) {
            System.err.println("⚠️  데이터 저장 중 오류: " + e.getMessage());
        }
    }

    /**
     * 리소스 정리 메서드
     * 열려있는 파일, 네트워크 연결 등의 리소스 정리
     */
    private static void cleanupResources() {
        try {
            // 현재는 특별히 정리할 리소스가 없지만,
            // 향후 데이터베이스 연결, 네트워크 소켓 등이 추가될 경우
            // 여기서 정리 작업을 수행

            if (DEBUG_MODE) {
                System.out.println("✅ 리소스 정리 완료");
            }

        } catch (Exception e) {
            System.err.println("⚠️  리소스 정리 중 오류: " + e.getMessage());
        }
    }

    /**
     * 치명적 오류 처리 메서드
     * 복구 불가능한 오류 발생 시 처리
     *
     * @param e 발생한 예외
     */
    private static void handleFatalError(Exception e) {
        System.err.println("\n");
        System.err.println("╔════════════════════════════════════════════════════════╗");
        System.err.println("║                    ❌ 치명적 오류 발생                    ║");
        System.err.println("╚════════════════════════════════════════════════════════╝");
        System.err.println();
        System.err.println("오류 유형: " + e.getClass().getSimpleName());
        System.err.println("오류 메시지: " + e.getMessage());
        System.err.println();
        System.err.println("프로그램을 종료합니다.");
        System.err.println("문제가 지속되면 관리자에게 문의하세요.");

        if (DEBUG_MODE) {
            System.err.println("\n[디버그 정보]");
            e.printStackTrace();
        }

        // 비정상 종료 (exit code 1)
        System.exit(1);
    }

    /**
     * 애플리케이션 정보를 반환하는 메서드
     *
     * @return 애플리케이션 정보 문자열
     */
    public static String getApplicationInfo() {
        return APPLICATION_NAME + " v" + APPLICATION_VERSION;
    }

    /**
     * 디버그 모드 여부를 반환하는 메서드
     *
     * @return 디버그 모드 활성화 여부
     */
    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }
}