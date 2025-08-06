package javaproject.test;

import javaproject.domain.User;
import javaproject.repository.UserRepository;
import java.util.List;

/**
 * UserRepository 테스트
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class UserRepositoryTest {

    private static UserRepository repository;

    /**
     * 테스트 실행 메서드
     */
    public static void runTests() {
        System.out.println("\n====== UserRepository 테스트 시작 ======");

        repository = UserRepository.getInstance();
        repository.deleteAll(); // 테스트 시작 전 초기화

        testSaveUser();
        testFindById();
        testFindByEmail();
        testUpdateUser();
        testDeleteUser();
        testFindAll();
        testDuplicateCheck();

        repository.deleteAll(); // 테스트 후 정리

        System.out.println("====== UserRepository 테스트 완료 ======\n");
    }

    /**
     * 사용자 저장 테스트
     */
    private static void testSaveUser() {
        System.out.println("\n[TEST] 사용자 저장 테스트");

        try {
            User user = new User("test1", "pass1", "테스트1",
                    "test1@test.com", "010-1111-1111", "서울", false);

            User saved = repository.save(user);

            assert saved != null : "저장된 사용자가 null";
            assert saved.getId().equals("test1") : "저장된 사용자 ID 불일치";
            assert repository.count() == 1 : "저장 후 카운트 오류";

            System.out.println("✅ 사용자 저장 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 저장 실패: " + e.getMessage());
        }
    }

    /**
     * ID로 조회 테스트
     */
    private static void testFindById() {
        System.out.println("\n[TEST] ID로 사용자 조회 테스트");

        try {
            // 사용자 저장
            User user = new User("test2", "pass2", "테스트2",
                    "test2@test.com", "010-2222-2222", "부산", false);
            repository.save(user);

            // ID로 조회
            User found = repository.findById("test2");
            assert found != null : "사용자 조회 실패";
            assert found.getName().equals("테스트2") : "조회된 사용자 정보 불일치";

            // 존재하지 않는 ID 조회
            User notFound = repository.findById("notexist");
            assert notFound == null : "존재하지 않는 사용자 조회 오류";

            System.out.println("✅ ID로 사용자 조회 성공");

        } catch (Exception e) {
            System.err.println("❌ ID로 사용자 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 이메일로 조회 테스트
     */
    private static void testFindByEmail() {
        System.out.println("\n[TEST] 이메일로 사용자 조회 테스트");

        try {
            // 사용자 저장
            User user = new User("test3", "pass3", "테스트3",
                    "test3@test.com", "010-3333-3333", "대구", false);
            repository.save(user);

            // 이메일로 조회
            User found = repository.findByEmail("test3@test.com");
            assert found != null : "이메일로 사용자 조회 실패";
            assert found.getId().equals("test3") : "조회된 사용자 ID 불일치";

            // 존재하지 않는 이메일 조회
            User notFound = repository.findByEmail("notexist@test.com");
            assert notFound == null : "존재하지 않는 이메일 조회 오류";

            System.out.println("✅ 이메일로 사용자 조회 성공");

        } catch (Exception e) {
            System.err.println("❌ 이메일로 사용자 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 수정 테스트
     */
    private static void testUpdateUser() {
        System.out.println("\n[TEST] 사용자 수정 테스트");

        try {
            // 사용자 저장
            User user = new User("test4", "pass4", "테스트4",
                    "test4@test.com", "010-4444-4444", "인천", false);
            repository.save(user);

            // 정보 수정
            user.setName("수정된이름");
            user.setEmail("modified@test.com");
            User updated = repository.update(user);

            // 수정 확인
            User found = repository.findById("test4");
            assert found.getName().equals("수정된이름") : "이름 수정 실패";
            assert found.getEmail().equals("modified@test.com") : "이메일 수정 실패";

            // 이메일 인덱스 업데이트 확인
            User foundByEmail = repository.findByEmail("modified@test.com");
            assert foundByEmail != null : "이메일 인덱스 업데이트 실패";

            System.out.println("✅ 사용자 수정 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 수정 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 삭제 테스트
     */
    private static void testDeleteUser() {
        System.out.println("\n[TEST] 사용자 삭제 테스트");

        try {
            // 사용자 저장
            User user = new User("test5", "pass5", "테스트5",
                    "test5@test.com", "010-5555-5555", "광주", false);
            repository.save(user);

            int countBefore = repository.count();

            // 삭제
            boolean deleted = repository.delete("test5");
            assert deleted : "사용자 삭제 실패";

            // 삭제 확인
            User found = repository.findById("test5");
            assert found == null : "삭제된 사용자가 조회됨";

            int countAfter = repository.count();
            assert countAfter == countBefore - 1 : "삭제 후 카운트 오류";

            // 이메일 인덱스에서도 삭제 확인
            User foundByEmail = repository.findByEmail("test5@test.com");
            assert foundByEmail == null : "이메일 인덱스에서 삭제 실패";

            System.out.println("✅ 사용자 삭제 성공");

        } catch (Exception e) {
            System.err.println("❌ 사용자 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 전체 조회 테스트
     */
    private static void testFindAll() {
        System.out.println("\n[TEST] 전체 사용자 조회 테스트");

        try {
            // 기존 데이터 삭제
            repository.deleteAll();

            // 여러 사용자 저장
            repository.save(new User("all1", "pass", "사용자1",
                    "all1@test.com", "010-1111-1111", "서울", false));
            repository.save(new User("all2", "pass", "사용자2",
                    "all2@test.com", "010-2222-2222", "부산", false));
            repository.save(new User("all3", "pass", "관리자",
                    "all3@test.com", "010-3333-3333", "대구", true));

            // 전체 조회
            List<User> all = repository.findAll();
            assert all.size() == 3 : "전체 조회 수 오류";

            // 관리자 조회
            List<User> admins = repository.findAdmins();
            assert admins.size() == 1 : "관리자 조회 수 오류";

            // 일반 사용자 조회
            List<User> regular = repository.findRegularUsers();
            assert regular.size() == 2 : "일반 사용자 조회 수 오류";

            System.out.println("✅ 전체 사용자 조회 성공");
            System.out.println("   - 전체: " + all.size() + "명");
            System.out.println("   - 관리자: " + admins.size() + "명");
            System.out.println("   - 일반: " + regular.size() + "명");

        } catch (Exception e) {
            System.err.println("❌ 전체 사용자 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 중복 체크 테스트
     */
    private static void testDuplicateCheck() {
        System.out.println("\n[TEST] 중복 체크 테스트");

        try {
            // 사용자 저장
            User user = new User("dup1", "pass", "중복테스트",
                    "dup@test.com", "010-9999-9999", "제주", false);
            repository.save(user);

            // ID 중복 체크
            assert repository.existsById("dup1") : "존재하는 ID 체크 실패";
            assert !repository.existsById("notexist") : "존재하지 않는 ID 체크 실패";

            // 이메일 중복 체크
            assert repository.existsByEmail("dup@test.com") : "존재하는 이메일 체크 실패";
            assert !repository.existsByEmail("notexist@test.com") : "존재하지 않는 이메일 체크 실패";

            System.out.println("✅ 중복 체크 성공");

        } catch (Exception e) {
            System.err.println("❌ 중복 체크 실패: " + e.getMessage());
        }
    }

    /**
     * 메인 메서드 - 단독 실행용
     */
    public static void main(String[] args) {
        runTests();
    }
}