package javaproject.test;

import javaproject.domain.User;
import javaproject.service.UserService;
import javaproject.repository.UserRepository;
import javaproject.exception.*;

/**
 * UserService 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UserServiceTest {

    private static UserService service;
    private static UserRepository repository;

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== UserService 테스트 시작 ======");

        service = UserService.getInstance();
        repository = UserRepository.getInstance();
        repository.deleteAll(); // 테스트 시작 전 초기화

        testRegister();
        testLogin();
        testDuplicateRegister();
        testInvalidLogin();
        testUpdateUser();
        testChangePassword();

        repository.deleteAll(); // 테스트 후 정리

        System.out.println("====== UserService 테스트 완료 ======\n");
    }

    /**
     * 회원가입 테스트
     */
    private static void testRegister() {
        System.out.println("\n[TEST] 회원가입 테스트");

        try {
            User newUser = new User("service1", "pass123", "서비스테스트1",
                    "service1@test.com", "010-1111-1111", "서울", false);

            User registered = service.register(newUser);

            assert registered != null : "회원가입 실패";
            assert registered.getId().equals("service1") : "회원가입 ID 불일치";
            assert registered.getCreatedDate() != null : "가입일시 설정 실패";

            System.out.println("✅ 회원가입 성공");

        } catch (Exception e) {
            System.err.println("❌ 회원가입 실패: " + e.getMessage());
        }
    }

    /**
     * 로그인 테스트
     */
    private static void testLogin() {
        System.out.println("\n[TEST] 로그인 테스트");

        try {
            // 회원가입
            User newUser = new User("login1", "password123", "로그인테스트",
                    "login1@test.com", "010-2222-2222", "부산", false);
            service.register(newUser);

            // 로그인
            User loggedIn = service.login("login1", "password123");

            assert loggedIn != null : "로그인 실패";
            assert loggedIn.getId().equals("login1") : "로그인 사용자 ID 불일치";
            assert loggedIn.getLastLoginDate() != null : "마지막 로그인 시간 설정 실패";

            System.out.println("✅ 로그인 성공");

        } catch (Exception e) {
            System.err.println("❌ 로그인 실패: " + e.getMessage());
        }
    }

    /**
     * 중복 회원가입 테스트
     */
    private static void testDuplicateRegister() {
        System.out.println("\n[TEST] 중복 회원가입 방지 테스트");

        try {
            // 첫 번째 회원가입
            User user1 = new User("dup1", "pass1", "중복테스트1",
                    "dup1@test.com", "010-3333-3333", "대구", false);
            service.register(user1);

            // 같은 ID로 회원가입 시도
            User user2 = new User("dup1", "pass2", "중복테스트2",
                    "dup2@test.com", "010-4444-4444", "인천", false);

            try {
                service.register(user2);
                assert false : "중복 ID 체크 실패";
            } catch (DuplicateUserException e) {
                System.out.println("✅ 중복 ID 방지 성공");
            }

            // 같은 이메일로 회원가입 시도
            User user3 = new User("dup2", "pass3", "중복테스트3",
                    "dup1@test.com", "010-5555-5555", "광주", false);

            try {
                service.register(user3);
                assert false : "중복 이메일 체크 실패";
            } catch (DuplicateUserException e) {
                System.out.println("✅ 중복 이메일 방지 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 중복 회원가입 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 잘못된 로그인 테스트
     */
    private static void testInvalidLogin() {
        System.out.println("\n[TEST] 잘못된 로그인 테스트");

        try {
            // 회원가입
            User user = new User("invalid1", "correct123", "테스트",
                    "invalid1@test.com", "010-6666-6666", "대전", false);
            service.register(user);

            // 존재하지 않는 ID로 로그인
            try {
                service.login("notexist", "password");
                assert false : "존재하지 않는 ID 로그인 체크 실패";
            } catch (UserNotFoundException e) {
                System.out.println("✅ 존재하지 않는 ID 체크 성공");
            }

            // 잘못된 비밀번호로 로그인
            try {
                service.login("invalid1", "wrongpass");
                assert false : "잘못된 비밀번호 체크 실패";
            } catch (InvalidPasswordException e) {
                System.out.println("✅ 잘못된 비밀번호 체크 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 잘못된 로그인 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 정보 수정 테스트
     */
    private static void testUpdateUser() {
        System.out.println("\n[TEST] 사용자 정보 수정 테스트");

        try {
            // 회원가입
            User user = new User("update1", "pass123", "수정전",
                    "update1@test.com", "010-7777-7777", "울산", false);
            service.register(user);

            // 정보 수정
            user.setName("수정후");
            user.setEmail("updated@test.com");
            user.setPhone("010-8888-8888");

            User updated = service.updateUser(user);

            assert updated.getName().equals("수정후") : "이름 수정 실패";
            assert updated.getEmail().equals("updated@test.com") : "이메일 수정 실패";
            assert updated.getPhone().equals("010-8888-8888") : "전화번호 수정 실패";
            assert updated.getModifiedDate() != null : "수정일시 설정 실패";

            System.out.println("✅ 사용자 정보 수정 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 정보 수정 실패: " + e.getMessage());
        }
    }

    /**
     * 비밀번호 변경 테스트
     */
    private static void testChangePassword() {
        System.out.println("\n[TEST] 비밀번호 변경 테스트");

        try {
            // 회원가입
            User user = new User("pass1", "oldpass123", "비밀번호테스트",
                    "pass1@test.com", "010-9999-9999", "세종", false);
            service.register(user);

            // 비밀번호 변경
            service.changePassword("pass1", "oldpass123", "newpass456");

            // 새 비밀번호로 로그인
            User loggedIn = service.login("pass1", "newpass456");
            assert loggedIn != null : "새 비밀번호로 로그인 실패";

            // 이전 비밀번호로 로그인 시도
            try {
                service.login("pass1", "oldpass123");
                assert false : "이전 비밀번호로 로그인됨";
            } catch (InvalidPasswordException e) {
                System.out.println("✅ 비밀번호 변경 성공");
            }

        } catch (Exception e) {
            System.err.println("❌ 비밀번호 변경 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}