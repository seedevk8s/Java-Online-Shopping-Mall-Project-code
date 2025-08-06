package javaproject.controller;

import javaproject.domain.User;
import javaproject.service.UserService;
import javaproject.util.SessionManager;
import javaproject.util.InputValidator;
import javaproject.exception.*;
import java.util.List;
import java.util.Scanner;

/**
 * 사용자 관리 컨트롤러
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 회원가입, 로그인, 마이페이지 등의 기능 제공
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UserController {

    // 싱글톤 인스턴스
    private static final UserController instance = new UserController();

    // 서비스 레이어 인스턴스
    private final UserService userService;

    // 유틸리티 인스턴스들
    private final SessionManager sessionManager;
    private final InputValidator inputValidator;

    // 스캐너 객체
    private final Scanner scanner;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private UserController() {
        this.userService = UserService.getInstance();
        this.sessionManager = SessionManager.getInstance();
        this.inputValidator = InputValidator.getInstance();
        this.scanner = new Scanner(System.in);
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return UserController의 유일한 인스턴스
     */
    public static UserController getInstance() {
        return instance;
    }

    /**
     * 회원가입 처리
     */
    public void register() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         🆕 회원가입                │");
        System.out.println("└────────────────────────────────────┘");

        try {
            // ID 입력
            System.out.print("아이디 (4~20자, 영문/숫자): ");
            String id = scanner.nextLine();
            id = inputValidator.validateUserId(id);

            // 중복 확인
            if (userService.isUserIdExists(id)) {
                System.out.println("❌ 이미 사용 중인 아이디입니다.");
                return;
            }

            // 비밀번호 입력
            System.out.print("비밀번호 (6~20자): ");
            String password = scanner.nextLine();
            password = inputValidator.validatePassword(password);

            // 비밀번호 확인
            System.out.print("비밀번호 확인: ");
            String passwordConfirm = scanner.nextLine();
            if (!password.equals(passwordConfirm)) {
                System.out.println("❌ 비밀번호가 일치하지 않습니다.");
                return;
            }

            // 이름 입력
            System.out.print("이름: ");
            String name = scanner.nextLine();
            name = inputValidator.validateName(name);

            // 이메일 입력
            System.out.print("이메일: ");
            String email = scanner.nextLine();
            email = inputValidator.validateEmail(email);

            // 전화번호 입력
            System.out.print("전화번호 (010-XXXX-XXXX): ");
            String phone = scanner.nextLine();
            phone = inputValidator.validatePhone(phone);

            // 주소 입력
            System.out.print("주소: ");
            String address = scanner.nextLine();
            address = inputValidator.validateAddress(address);

            // 사용자 생성
            User newUser = new User(id, password, name, email, phone, address, false);
            userService.register(newUser);

            System.out.println("\n✅ 회원가입이 완료되었습니다!");
            System.out.println("가입하신 아이디로 로그인해주세요.");

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (DuplicateUserException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 로그인 처리
     */
    public void login() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         🔐 로그인                   │");
        System.out.println("└────────────────────────────────────┘");

        try {
            // 이미 로그인되어 있는지 확인
            if (sessionManager.isLoggedIn()) {
                System.out.println("⚠️ 이미 로그인되어 있습니다.");
                System.out.print("다시 로그인하시겠습니까? (Y/N): ");
                String answer = scanner.nextLine();
                if (!inputValidator.validateYesNo(answer)) {
                    return;
                }
                sessionManager.logout();
            }

            // ID 입력
            System.out.print("아이디: ");
            String id = scanner.nextLine().trim();

            // 비밀번호 입력
            System.out.print("비밀번호: ");
            String password = scanner.nextLine();

            // 로그인 시도
            User user = userService.login(id, password);

            // 세션에 사용자 정보 저장
            sessionManager.login(user);

            System.out.println("\n✅ 로그인 성공!");
            System.out.println("환영합니다, " + user.getName() + "님! 👋");

            if (user.isAdmin()) {
                System.out.println("🔑 관리자 권한으로 로그인하셨습니다.");
            }

        } catch (UserNotFoundException e) {
            System.out.println("❌ 존재하지 않는 아이디입니다.");
        } catch (InvalidPasswordException e) {
            System.out.println("❌ 비밀번호가 일치하지 않습니다.");
        } catch (Exception e) {
            System.out.println("❌ 로그인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 마이페이지 표시
     */
    public void showMyPage() {
        // 로그인 확인
        if (!sessionManager.isLoggedIn()) {
            System.out.println("⚠️ 로그인이 필요한 서비스입니다.");
            return;
        }

        User currentUser = sessionManager.getCurrentUser();

        while (true) {
            System.out.println("\n┌────────────────────────────────────┐");
            System.out.println("│         👤 마이페이지               │");
            System.out.println("├────────────────────────────────────┤");
            System.out.println("│  1. 내 정보 보기                   │");
            System.out.println("│  2. 정보 수정                      │");
            System.out.println("│  3. 비밀번호 변경                  │");
            System.out.println("│  4. 회원 탈퇴                      │");
            System.out.println("│  5. 이전 메뉴로                    │");
            System.out.println("└────────────────────────────────────┘");

            System.out.print("메뉴를 선택하세요: ");
            String input = scanner.nextLine();

            try {
                int choice = inputValidator.validateMenuChoice(input, 1, 5);

                switch (choice) {
                    case 1:
                        viewMyInfo();
                        break;
                    case 2:
                        updateMyInfo();
                        break;
                    case 3:
                        changePassword();
                        break;
                    case 4:
                        deleteAccount();
                        return;
                    case 5:
                        return;
                }
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /**
     * 내 정보 보기
     */
    private void viewMyInfo() {
        User user = sessionManager.getCurrentUser();

        System.out.println("\n=== 👤 내 정보 ===");
        System.out.println("아이디: " + user.getId());
        System.out.println("이름: " + user.getName());
        System.out.println("이메일: " + user.getEmail());
        System.out.println("전화번호: " + user.getPhone());
        System.out.println("주소: " + user.getAddress());
        System.out.println("회원 등급: " + (user.isAdmin() ? "관리자" : "일반회원"));
        System.out.println("가입일: " + user.getCreatedDate());
    }

    /**
     * 정보 수정
     */
    private void updateMyInfo() {
        User user = sessionManager.getCurrentUser();

        System.out.println("\n=== 정보 수정 ===");
        System.out.println("(변경하지 않을 항목은 Enter를 누르세요)");

        try {
            // 이름 수정
            System.out.print("이름 [" + user.getName() + "]: ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                name = inputValidator.validateName(name);
                user.setName(name);
            }

            // 이메일 수정
            System.out.print("이메일 [" + user.getEmail() + "]: ");
            String email = scanner.nextLine();
            if (!email.trim().isEmpty()) {
                email = inputValidator.validateEmail(email);
                user.setEmail(email);
            }

            // 전화번호 수정
            System.out.print("전화번호 [" + user.getPhone() + "]: ");
            String phone = scanner.nextLine();
            if (!phone.trim().isEmpty()) {
                phone = inputValidator.validatePhone(phone);
                user.setPhone(phone);
            }

            // 주소 수정
            System.out.print("주소 [" + user.getAddress() + "]: ");
            String address = scanner.nextLine();
            if (!address.trim().isEmpty()) {
                address = inputValidator.validateAddress(address);
                user.setAddress(address);
            }

            // 변경사항 저장
            userService.updateUser(user);
            System.out.println("✅ 정보가 수정되었습니다.");

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 비밀번호 변경
     */
    private void changePassword() {
        User user = sessionManager.getCurrentUser();

        System.out.println("\n=== 비밀번호 변경 ===");

        try {
            // 현재 비밀번호 확인
            System.out.print("현재 비밀번호: ");
            String currentPassword = scanner.nextLine();

            if (!user.getPassword().equals(currentPassword)) {
                System.out.println("❌ 현재 비밀번호가 일치하지 않습니다.");
                return;
            }

            // 새 비밀번호 입력
            System.out.print("새 비밀번호 (6~20자): ");
            String newPassword = scanner.nextLine();
            newPassword = inputValidator.validatePassword(newPassword);

            // 새 비밀번호 확인
            System.out.print("새 비밀번호 확인: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ 새 비밀번호가 일치하지 않습니다.");
                return;
            }

            // 비밀번호 변경
            user.setPassword(newPassword);
            userService.updateUser(user);

            System.out.println("✅ 비밀번호가 변경되었습니다.");

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 회원 탈퇴
     */
    private void deleteAccount() {
        User user = sessionManager.getCurrentUser();

        System.out.println("\n⚠️ 회원 탈퇴");
        System.out.println("탈퇴하시면 모든 정보가 삭제되며 복구할 수 없습니다.");
        System.out.print("정말 탈퇴하시겠습니까? (Y/N): ");

        try {
            String answer = scanner.nextLine();
            if (!inputValidator.validateYesNo(answer)) {
                System.out.println("회원 탈퇴를 취소했습니다.");
                return;
            }

            // 비밀번호 재확인
            System.out.print("비밀번호를 입력하세요: ");
            String password = scanner.nextLine();

            if (!user.getPassword().equals(password)) {
                System.out.println("❌ 비밀번호가 일치하지 않습니다.");
                return;
            }

            // 회원 탈퇴 처리
            userService.deleteUser(user.getId());
            sessionManager.logout();

            System.out.println("✅ 회원 탈퇴가 완료되었습니다.");
            System.out.println("그동안 이용해 주셔서 감사합니다.");

        } catch (Exception e) {
            System.out.println("❌ 회원 탈퇴 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 관리자용: 모든 사용자 목록 조회
     */
    public void viewAllUsers() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        List<User> users = userService.getAllUsers();

        System.out.println("\n=== 전체 사용자 목록 ===");
        System.out.println("총 " + users.size() + "명의 사용자");
        System.out.println("-".repeat(60));

        for (User user : users) {
            System.out.printf("ID: %-15s | 이름: %-10s | 등급: %s\n",
                    user.getId(), user.getName(),
                    user.isAdmin() ? "관리자" : "일반");
        }
    }

    /**
     * 관리자용: 사용자 검색
     */
    public void searchUser() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        System.out.print("검색할 사용자 ID: ");
        String userId = scanner.nextLine().trim();

        try {
            User user = userService.getUserById(userId);

            System.out.println("\n=== 사용자 정보 ===");
            System.out.println("아이디: " + user.getId());
            System.out.println("이름: " + user.getName());
            System.out.println("이메일: " + user.getEmail());
            System.out.println("전화번호: " + user.getPhone());
            System.out.println("주소: " + user.getAddress());
            System.out.println("회원 등급: " + (user.isAdmin() ? "관리자" : "일반회원"));
            System.out.println("가입일: " + user.getCreatedDate());

        } catch (UserNotFoundException e) {
            System.out.println("❌ 사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 관리자용: 사용자 권한 변경
     */
    public void changeUserRole() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        System.out.print("권한을 변경할 사용자 ID: ");
        String userId = scanner.nextLine().trim();

        try {
            User user = userService.getUserById(userId);

            System.out.println("\n현재 권한: " + (user.isAdmin() ? "관리자" : "일반회원"));
            System.out.print("관리자 권한을 부여하시겠습니까? (Y/N): ");

            String answer = scanner.nextLine();
            boolean makeAdmin = inputValidator.validateYesNo(answer);

            user.setAdmin(makeAdmin);
            userService.updateUser(user);

            System.out.println("✅ 권한이 변경되었습니다.");
            System.out.println(user.getName() + "님의 권한: " +
                    (user.isAdmin() ? "관리자" : "일반회원"));

        } catch (UserNotFoundException e) {
            System.out.println("❌ 사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            System.out.println("❌ 권한 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}