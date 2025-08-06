package javaproject.controller;

import javaproject.domain.User;
import javaproject.service.UserService;
import javaproject.service.ProductService;
import javaproject.service.CartService;
import javaproject.service.OrderService;
import javaproject.util.SessionManager;
import javaproject.util.InputValidator;
import javaproject.exception.InvalidInputException;
import java.util.Scanner;

/**
 * 메인 메뉴 컨트롤러 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성되도록 구현
 * 메인 메뉴를 표시하고 사용자 입력에 따라 각 서브 컨트롤러를 호출
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class MainController {

    // 싱글톤 인스턴스 - 클래스 로딩 시점에 생성되는 thread-safe 방식
    private static final MainController instance = new MainController();

    // 스캐너 객체 - 사용자 입력을 받기 위한 객체
    private final Scanner scanner;

    // 각 도메인별 컨트롤러 인스턴스들
    private final UserController userController;
    private final ProductController productController;
    private final OrderController orderController;

    // 세션 관리자 - 로그인 정보 관리
    private final SessionManager sessionManager;

    // 입력 검증 유틸리티
    private final InputValidator inputValidator;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     * 외부에서 인스턴스를 생성할 수 없도록 private으로 선언
     */
    private MainController() {
        // Scanner 객체 초기화
        this.scanner = new Scanner(System.in);

        // 각 컨트롤러 인스턴스 획득 (모두 싱글톤)
        this.userController = UserController.getInstance();
        this.productController = ProductController.getInstance();
        this.orderController = OrderController.getInstance();

        // 세션 관리자 인스턴스 획득
        this.sessionManager = SessionManager.getInstance();

        // 입력 검증 유틸리티 인스턴스 획득
        this.inputValidator = InputValidator.getInstance();
    }

    /**
     * 싱글톤 인스턴스를 반환하는 정적 메서드
     * @return MainController의 유일한 인스턴스
     */
    public static MainController getInstance() {
        return instance;
    }

    /**
     * 메인 애플리케이션 실행 메서드
     * 프로그램의 메인 루프를 담당
     */
    public void run() {
        // 환영 메시지 출력
        printWelcomeMessage();

        // 메인 루프 - 사용자가 종료를 선택할 때까지 반복
        while (true) {
            try {
                // 현재 로그인 상태에 따라 다른 메뉴 표시
                if (sessionManager.isLoggedIn()) {
                    // 로그인된 상태의 메뉴 처리
                    handleLoggedInMenu();
                } else {
                    // 로그인되지 않은 상태의 메뉴 처리
                    handleGuestMenu();
                }
            } catch (Exception e) {
                // 예상치 못한 예외 처리
                System.err.println("\n❌ 오류가 발생했습니다: " + e.getMessage());
                System.out.println("다시 시도해주세요.\n");
            }
        }
    }

    /**
     * 환영 메시지 출력 메서드
     * 프로그램 시작 시 한 번만 호출
     */
    private void printWelcomeMessage() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║                                            ║");
        System.out.println("║     🛍️  Java Shopping Mall에 오신 것을     ║");
        System.out.println("║            환영합니다! 🛍️                  ║");
        System.out.println("║                                            ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * 게스트(비로그인) 상태의 메뉴 처리
     * 회원가입, 로그인, 상품 둘러보기, 종료 옵션 제공
     */
    private void handleGuestMenu() {
        // 게스트 메뉴 출력
        printGuestMenu();

        // 사용자 입력 받기
        System.out.print("메뉴를 선택하세요: ");
        String input = scanner.nextLine().trim();

        try {
            // 입력값을 정수로 변환 (유효성 검사 포함)
            int choice = inputValidator.validateMenuChoice(input, 1, 4);

            // 선택한 메뉴에 따라 처리
            switch (choice) {
                case 1:
                    // 회원가입 처리
                    userController.register();
                    break;
                case 2:
                    // 로그인 처리
                    userController.login();
                    break;
                case 3:
                    // 상품 둘러보기 (비로그인 상태에서도 가능)
                    productController.browseProducts();
                    break;
                case 4:
                    // 프로그램 종료
                    exitProgram();
                    break;
                default:
                    // 이론상 도달할 수 없는 코드 (validateMenuChoice에서 검증)
                    System.out.println("❌ 잘못된 선택입니다.");
            }
        } catch (InvalidInputException e) {
            // 입력 검증 실패 시 에러 메시지 출력
            System.out.println("❌ " + e.getMessage());
        }
    }

    /**
     * 로그인된 사용자를 위한 메뉴 처리
     * 상품 관리, 장바구니, 주문, 마이페이지 등의 기능 제공
     */
    private void handleLoggedInMenu() {
        // 현재 로그인한 사용자 정보 가져오기
        User currentUser = sessionManager.getCurrentUser();

        // 로그인 사용자 메뉴 출력
        printLoggedInMenu(currentUser);

        // 사용자 입력 받기
        System.out.print("메뉴를 선택하세요: ");
        String input = scanner.nextLine().trim();

        try {
            // 관리자와 일반 사용자의 메뉴 범위가 다름
            int maxChoice = currentUser.isAdmin() ? 9 : 7;
            int choice = inputValidator.validateMenuChoice(input, 1, maxChoice);

            // 선택한 메뉴에 따라 처리
            switch (choice) {
                case 1:
                    // 상품 둘러보기
                    productController.browseProducts();
                    break;
                case 2:
                    // 상품 검색
                    productController.searchProducts();
                    break;
                case 3:
                    // 장바구니 관리
                    handleCartMenu();
                    break;
                case 4:
                    // 주문하기
                    orderController.createOrder();
                    break;
                case 5:
                    // 주문 내역 조회
                    orderController.viewMyOrders();
                    break;
                case 6:
                    // 마이페이지
                    userController.showMyPage();
                    break;
                case 7:
                    // 로그아웃
                    logout();
                    break;
                case 8:
                    // 관리자 전용: 상품 관리
                    if (currentUser.isAdmin()) {
                        handleAdminProductMenu();
                    }
                    break;
                case 9:
                    // 관리자 전용: 사용자 관리
                    if (currentUser.isAdmin()) {
                        handleAdminUserMenu();
                    }
                    break;
                default:
                    System.out.println("❌ 잘못된 선택입니다.");
            }
        } catch (InvalidInputException e) {
            // 입력 검증 실패 시 에러 메시지 출력
            System.out.println("❌ " + e.getMessage());
        }
    }

    /**
     * 장바구니 서브메뉴 처리
     * 장바구니 조회, 상품 추가/삭제, 수량 변경 등의 기능 제공
     */
    private void handleCartMenu() {
        while (true) {
            // 장바구니 메뉴 출력
            printCartMenu();

            System.out.print("메뉴를 선택하세요: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = inputValidator.validateMenuChoice(input, 1, 5);

                switch (choice) {
                    case 1:
                        // 장바구니 조회
                        orderController.viewCart();
                        break;
                    case 2:
                        // 장바구니에 상품 추가
                        orderController.addToCart();
                        break;
                    case 3:
                        // 장바구니에서 상품 삭제
                        orderController.removeFromCart();
                        break;
                    case 4:
                        // 장바구니 상품 수량 변경
                        orderController.updateCartItemQuantity();
                        break;
                    case 5:
                        // 메인 메뉴로 돌아가기
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /**
     * 관리자 전용 상품 관리 메뉴 처리
     * 상품 등록, 수정, 삭제 등의 관리 기능 제공
     */
    private void handleAdminProductMenu() {
        while (true) {
            // 관리자 상품 관리 메뉴 출력
            printAdminProductMenu();

            System.out.print("메뉴를 선택하세요: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = inputValidator.validateMenuChoice(input, 1, 5);

                switch (choice) {
                    case 1:
                        // 상품 등록
                        productController.addProduct();
                        break;
                    case 2:
                        // 상품 수정
                        productController.updateProduct();
                        break;
                    case 3:
                        // 상품 삭제
                        productController.deleteProduct();
                        break;
                    case 4:
                        // 재고 관리
                        productController.manageInventory();
                        break;
                    case 5:
                        // 메인 메뉴로 돌아가기
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /**
     * 관리자 전용 사용자 관리 메뉴 처리
     * 사용자 목록 조회, 권한 변경 등의 관리 기능 제공
     */
    private void handleAdminUserMenu() {
        while (true) {
            // 관리자 사용자 관리 메뉴 출력
            printAdminUserMenu();

            System.out.print("메뉴를 선택하세요: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = inputValidator.validateMenuChoice(input, 1, 4);

                switch (choice) {
                    case 1:
                        // 전체 사용자 목록 조회
                        userController.viewAllUsers();
                        break;
                    case 2:
                        // 사용자 검색
                        userController.searchUser();
                        break;
                    case 3:
                        // 사용자 권한 변경
                        userController.changeUserRole();
                        break;
                    case 4:
                        // 메인 메뉴로 돌아가기
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /**
     * 게스트 메뉴 출력
     */
    private void printGuestMenu() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         🏠 메인 메뉴 (게스트)        │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 회원가입                       │");
        System.out.println("│  2. 로그인                         │");
        System.out.println("│  3. 상품 둘러보기                  │");
        System.out.println("│  4. 종료                           │");
        System.out.println("└────────────────────────────────────┘");
    }

    /**
     * 로그인 사용자 메뉴 출력
     * @param user 현재 로그인한 사용자
     */
    private void printLoggedInMenu(User user) {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         🏠 메인 메뉴                │");
        System.out.println("│  환영합니다, " + String.format("%-20s", user.getName() + "님!") + "│");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 상품 둘러보기                  │");
        System.out.println("│  2. 상품 검색                      │");
        System.out.println("│  3. 장바구니 관리                  │");
        System.out.println("│  4. 주문하기                       │");
        System.out.println("│  5. 주문 내역 조회                 │");
        System.out.println("│  6. 마이페이지                     │");
        System.out.println("│  7. 로그아웃                       │");

        // 관리자인 경우 추가 메뉴 표시
        if (user.isAdmin()) {
            System.out.println("├────────────────────────────────────┤");
            System.out.println("│  [관리자 메뉴]                     │");
            System.out.println("│  8. 상품 관리                      │");
            System.out.println("│  9. 사용자 관리                    │");
        }

        System.out.println("└────────────────────────────────────┘");
    }

    /**
     * 장바구니 메뉴 출력
     */
    private void printCartMenu() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         🛒 장바구니 관리            │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 장바구니 조회                  │");
        System.out.println("│  2. 상품 추가                      │");
        System.out.println("│  3. 상품 삭제                      │");
        System.out.println("│  4. 수량 변경                      │");
        System.out.println("│  5. 메인 메뉴로 돌아가기           │");
        System.out.println("└────────────────────────────────────┘");
    }

    /**
     * 관리자 상품 관리 메뉴 출력
     */
    private void printAdminProductMenu() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│      📦 관리자 - 상품 관리          │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 상품 등록                      │");
        System.out.println("│  2. 상품 수정                      │");
        System.out.println("│  3. 상품 삭제                      │");
        System.out.println("│  4. 재고 관리                      │");
        System.out.println("│  5. 메인 메뉴로 돌아가기           │");
        System.out.println("└────────────────────────────────────┘");
    }

    /**
     * 관리자 사용자 관리 메뉴 출력
     */
    private void printAdminUserMenu() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│      👥 관리자 - 사용자 관리        │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 전체 사용자 목록               │");
        System.out.println("│  2. 사용자 검색                    │");
        System.out.println("│  3. 사용자 권한 변경               │");
        System.out.println("│  4. 메인 메뉴로 돌아가기           │");
        System.out.println("└────────────────────────────────────┘");
    }

    /**
     * 로그아웃 처리 메서드
     */
    private void logout() {
        // 현재 사용자 이름 저장 (로그아웃 메시지용)
        String userName = sessionManager.getCurrentUser().getName();

        // 세션에서 로그아웃 처리
        sessionManager.logout();

        // 로그아웃 메시지 출력
        System.out.println("\n✅ " + userName + "님, 안전하게 로그아웃되었습니다.");
        System.out.println("이용해 주셔서 감사합니다! 👋\n");
    }

    /**
     * 프로그램 종료 처리 메서드
     */
    private void exitProgram() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║                                            ║");
        System.out.println("║    Java Shopping Mall을 이용해 주셔서     ║");
        System.out.println("║         감사합니다! 안녕히 가세요 👋       ║");
        System.out.println("║                                            ║");
        System.out.println("╚════════════════════════════════════════════╝");

        // 스캐너 리소스 정리
        scanner.close();

        // 프로그램 정상 종료
        System.exit(0);
    }

    /**
     * 리소스 정리 메서드
     * 프로그램 종료 시 호출되어 열려있는 리소스를 정리
     */
    public void cleanup() {
        try {
            // 스캐너가 열려있으면 닫기
            if (scanner != null) {
                scanner.close();
            }

            // 각 컨트롤러의 cleanup 메서드 호출 (있다면)
            // 예: 파일 저장, 데이터베이스 연결 종료 등

            System.out.println("✅ 리소스가 정상적으로 정리되었습니다.");
        } catch (Exception e) {
            System.err.println("⚠️ 리소스 정리 중 오류 발생: " + e.getMessage());
        }
    }
}