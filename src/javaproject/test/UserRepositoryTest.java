// =================================================================
// UserRepositoryTest.java - 사용자 Repository 테스트
// =================================================================
package javaproject.test;

import javaproject.domain.User;
import javaproject.exception.AuthenticationException;
import javaproject.exception.FileIOException;
import javaproject.exception.UserNotFoundException;
import javaproject.repository.UserRepository;

import java.util.Optional;

/**
 * UserRepository 기능을 테스트하는 클래스
 * 실제 프로젝트에서는 JUnit을 사용하지만, 여기서는 간단한 테스트 메서드로 구현
 */
public class UserRepositoryTest {

    private UserRepository userRepository;

    /**
     * 테스트 초기화
     */
    public UserRepositoryTest() {
        this.userRepository = UserRepository.getInstance();
    }

    /**
     * 모든 테스트 실행
     */
    public void runAllTests() {
        System.out.println("========== UserRepository 테스트 시작 ==========");

        try {
            testSaveUser();
            testFindUserById();
            testFindUserByEmail();
            testUpdateUser();
            testDeleteUser();
            testLogin();
            testUserCount();
            testUserStatistics();

            System.out.println("✅ UserRepository 모든 테스트 통과!");

        } catch (Exception e) {
            System.err.println("❌ UserRepository 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("========== UserRepository 테스트 완료 ==========\n");
    }

    /**
     * 사용자 저장 테스트
     */
    private void testSaveUser() throws FileIOException {
        System.out.println("📋 사용자 저장 테스트...");

        // 테스트 사용자 생성
        User testUser = new User("testuser", "password123", "테스트사용자", "test@example.com");

        // 저장 테스트
        boolean saved = userRepository.save(testUser);
        assert saved : "사용자 저장 실패";

        // 중복 저장 테스트 (실패해야 함)
        boolean duplicateSaved = userRepository.save(testUser);
        assert !duplicateSaved : "중복 사용자 저장이 성공하면 안됨";

        System.out.println("   ✅ 사용자 저장 테스트 통과");
    }

    /**
     * 사용자 ID로 조회 테스트
     */
    private void testFindUserById() throws FileIOException {
        System.out.println("📋 사용자 ID 조회 테스트...");

        // 존재하는 사용자 조회
        Optional<User> foundUser = userRepository.findById("testuser");
        assert foundUser.isPresent() : "저장된 사용자를 찾을 수 없음";
        assert "테스트사용자".equals(foundUser.get().getName()) : "사용자 이름이 일치하지 않음";

        // 존재하지 않는 사용자 조회
        Optional<User> notFoundUser = userRepository.findById("nonexistent");
        assert !notFoundUser.isPresent() : "존재하지 않는 사용자가 조회됨";

        System.out.println("   ✅ 사용자 ID 조회 테스트 통과");
    }

    /**
     * 이메일로 조회 테스트
     */
    private void testFindUserByEmail() throws FileIOException {
        System.out.println("📋 이메일 조회 테스트...");

        // 존재하는 이메일로 조회
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assert foundUser.isPresent() : "이메일로 사용자를 찾을 수 없음";
        assert "testuser".equals(foundUser.get().getUserId()) : "사용자 ID가 일치하지 않음";

        // 존재하지 않는 이메일로 조회
        Optional<User> notFoundUser = userRepository.findByEmail("notfound@example.com");
        assert !notFoundUser.isPresent() : "존재하지 않는 이메일로 사용자가 조회됨";

        System.out.println("   ✅ 이메일 조회 테스트 통과");
    }

    /**
     * 사용자 정보 수정 테스트
     */
    private void testUpdateUser() throws FileIOException, UserNotFoundException {
        System.out.println("📋 사용자 정보 수정 테스트...");

        // 사용자 조회
        Optional<User> userOpt = userRepository.findById("testuser");
        assert userOpt.isPresent() : "수정할 사용자를 찾을 수 없음";

        User user = userOpt.get();
        String originalName = user.getName();

        // 이름 변경 테스트
        boolean nameChanged = userRepository.changeName("testuser", "수정된이름");
        assert nameChanged : "이름 변경 실패";

        // 변경 확인
        Optional<User> updatedUserOpt = userRepository.findById("testuser");
        assert updatedUserOpt.isPresent() : "수정된 사용자를 찾을 수 없음";
        assert "수정된이름".equals(updatedUserOpt.get().getName()) : "이름이 변경되지 않음";

        // 원래 이름으로 복구
        userRepository.changeName("testuser", originalName);

        System.out.println("   ✅ 사용자 정보 수정 테스트 통과");
    }

    /**
     * 로그인 테스트
     */
    private void testLogin() throws FileIOException {
        System.out.println("📋 로그인 테스트...");

        try {
            // 정상 로그인
            User loggedInUser = userRepository.login("testuser", "password123");
            assert loggedInUser != null : "로그인 실패";
            assert "testuser".equals(loggedInUser.getUserId()) : "로그인된 사용자 ID가 일치하지 않음";

            System.out.println("   ✅ 정상 로그인 테스트 통과");

        } catch (AuthenticationException e) {
            assert false : "정상 로그인이 실패함: " + e.getMessage();
        }

        try {
            // 잘못된 비밀번호로 로그인 (실패해야 함)
            userRepository.login("testuser", "wrongpassword");
            assert false : "잘못된 비밀번호로 로그인이 성공하면 안됨";

        } catch (AuthenticationException e) {
            System.out.println("   ✅ 잘못된 비밀번호 로그인 실패 테스트 통과");
        }

        try {
            // 존재하지 않는 사용자로 로그인 (실패해야 함)
            userRepository.login("nonexistent", "password");
            assert false : "존재하지 않는 사용자로 로그인이 성공하면 안됨";

        } catch (AuthenticationException e) {
            System.out.println("   ✅ 존재하지 않는 사용자 로그인 실패 테스트 통과");
        }
    }

    /**
     * 사용자 수 조회 테스트
     */
    private void testUserCount() throws FileIOException {
        System.out.println("📋 사용자 수 조회 테스트...");

        long count = userRepository.count();
        assert count > 0 : "사용자 수가 0보다 작음";

        System.out.println("   ✅ 사용자 수 조회 테스트 통과 (총 " + count + "명)");
    }

    /**
     * 사용자 통계 테스트
     */
    private void testUserStatistics() throws FileIOException {
        System.out.println("📋 사용자 통계 테스트...");

        String statistics = userRepository.getUserStatistics();
        assert statistics != null && !statistics.isEmpty() : "사용자 통계가 비어있음";

        System.out.println("   ✅ 사용자 통계 테스트 통과");
        System.out.println(statistics);
    }

    /**
     * 사용자 삭제 테스트 (마지막에 실행)
     */
    private void testDeleteUser() throws FileIOException {
        System.out.println("📋 사용자 삭제 테스트...");

        // 삭제 전 존재 확인
        assert userRepository.existsById("testuser") : "삭제할 사용자가 존재하지 않음";

        // 삭제 실행
        boolean deleted = userRepository.deleteById("testuser");
        assert deleted : "사용자 삭제 실패";

        // 삭제 후 존재하지 않음 확인
        assert !userRepository.existsById("testuser") : "삭제된 사용자가 여전히 존재함";

        System.out.println("   ✅ 사용자 삭제 테스트 통과");
    }
}