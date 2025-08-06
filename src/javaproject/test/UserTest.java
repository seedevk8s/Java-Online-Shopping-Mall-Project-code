package javaproject.test;

import javaproject.domain.User;
import java.time.LocalDateTime;

/**
 * User 도메인 클래스 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UserTest {

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== User 도메인 테스트 시작 ======");

        testUserCreation();
        testPasswordCheck();
        testUserEquality();
        testUserValidation();
        testUserUpdate();

        System.out.println("====== User 도메인 테스트 완료 ======\n");
    }

    /**
     * 사용자 생성 테스트
     */
    private static void testUserCreation() {
        System.out.println("\n[TEST] 사용자 생성 테스트");

        try {
            // 일반 사용자 생성
            User user = new User("testuser", "password123", "테스트유저",
                    "test@test.com", "010-1234-5678", "서울시", false);

            assert user.getId().equals("testuser") : "ID 설정 실패";
            assert user.getName().equals("테스트유저") : "이름 설정 실패";
            assert !user.isAdmin() : "관리자 권한 설정 실패";
            assert user.isActive() : "활성 상태 설정 실패";

            System.out.println("✅ 일반 사용자 생성 성공");

            // 관리자 생성
            User admin = new User("admin", "admin123", "관리자",
                    "admin@test.com", "010-0000-0000", "서울시", true);

            assert admin.isAdmin() : "관리자 권한 설정 실패";

            System.out.println("✅ 관리자 생성 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 비밀번호 체크 테스트
     */
    private static void testPasswordCheck() {
        System.out.println("\n[TEST] 비밀번호 체크 테스트");

        try {
            User user = new User("testuser", "password123", "테스트유저",
                    "test@test.com", "010-1234-5678", "서울시", false);

            // 올바른 비밀번호
            assert user.checkPassword("password123") : "올바른 비밀번호 체크 실패";

            // 잘못된 비밀번호
            assert !user.checkPassword("wrongpassword") : "잘못된 비밀번호 체크 실패";

            // null 비밀번호
            assert !user.checkPassword(null) : "null 비밀번호 체크 실패";

            System.out.println("✅ 비밀번호 체크 성공");

        } catch (Exception e) {
            System.err.println("❌ 비밀번호 체크 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 동등성 테스트
     */
    private static void testUserEquality() {
        System.out.println("\n[TEST] 사용자 동등성 테스트");

        try {
            User user1 = new User("testuser", "password123", "테스트유저",
                    "test@test.com", "010-1234-5678", "서울시", false);

            User user2 = new User("testuser", "different", "다른이름",
                    "other@test.com", "010-9999-9999", "부산시", true);

            User user3 = new User("otheruser", "password123", "테스트유저",
                    "test@test.com", "010-1234-5678", "서울시", false);

            // 같은 ID를 가진 사용자는 동일
            assert user1.equals(user2) : "같은 ID 사용자 동등성 실패";

            // 다른 ID를 가진 사용자는 다름
            assert !user1.equals(user3) : "다른 ID 사용자 구분 실패";

            // null과 비교
            assert !user1.equals(null) : "null 비교 실패";

            System.out.println("✅ 사용자 동등성 테스트 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 동등성 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 유효성 검증 테스트
     */
    private static void testUserValidation() {
        System.out.println("\n[TEST] 사용자 유효성 검증 테스트");

        try {
            // 유효한 사용자
            User validUser = new User("testuser", "password123", "테스트유저",
                    "test@test.com", "010-1234-5678", "서울시", false);
            assert validUser.isValid() : "유효한 사용자 검증 실패";

            // 유효하지 않은 사용자 (ID 없음)
            User invalidUser = new User();
            assert !invalidUser.isValid() : "유효하지 않은 사용자 검증 실패";

            System.out.println("✅ 사용자 유효성 검증 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 유효성 검증 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 정보 업데이트 테스트
     */
    private static void testUserUpdate() {
        System.out.println("\n[TEST] 사용자 정보 업데이트 테스트");

        try {
            User user = new User("testuser", "password123", "테스트유저",
                    "test@test.com", "010-1234-5678", "서울시", false);

            LocalDateTime originalModified = user.getModifiedDate();

            // 잠시 대기 (수정 시간 변경 확인용)
            Thread.sleep(10);

            // 정보 업데이트
            user.setName("변경된이름");
            user.setEmail("new@test.com");
            user.setPhone("010-9999-9999");

            assert user.getName().equals("변경된이름") : "이름 변경 실패";
            assert user.getEmail().equals("new@test.com") : "이메일 변경 실패";
            assert user.getPhone().equals("010-9999-9999") : "전화번호 변경 실패";

            // 수정 시간이 업데이트되었는지 확인
            assert user.getModifiedDate().isAfter(originalModified) : "수정 시간 업데이트 실패";

            System.out.println("✅ 사용자 정보 업데이트 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 정보 업데이트 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}