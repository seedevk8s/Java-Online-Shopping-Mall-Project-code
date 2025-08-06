package javaproject.repository;

import javaproject.domain.Cart;
import java.util.*;

/**
 * 장바구니 저장소 클래스
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 메모리에 장바구니 데이터를 저장하고 관리
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class CartRepository {

    // 싱글톤 인스턴스
    private static final CartRepository instance = new CartRepository();

    // 장바구니 데이터 저장소 (ID를 키로 사용)
    private final Map<String, Cart> carts;

    // 사용자별 장바구니 인덱스 (사용자당 하나의 장바구니)
    private final Map<String, Cart> userCartIndex;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private CartRepository() {
        this.carts = new HashMap<>();
        this.userCartIndex = new HashMap<>();
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return CartRepository의 유일한 인스턴스
     */
    public static CartRepository getInstance() {
        return instance;
    }

    /**
     * 장바구니 저장
     *
     * @param cart 저장할 장바구니
     * @return 저장된 장바구니
     */
    public Cart save(Cart cart) {
        if (cart == null || cart.getId() == null) {
            throw new IllegalArgumentException("장바구니 정보가 유효하지 않습니다.");
        }

        // 기존 장바구니가 있으면 사용자 인덱스에서 제거
        Cart existing = carts.get(cart.getId());
        if (existing != null && existing.getUserId() != null) {
            userCartIndex.remove(existing.getUserId());
        }

        // 장바구니 저장
        carts.put(cart.getId(), cart);

        // 사용자 인덱스 업데이트
        if (cart.getUserId() != null) {
            userCartIndex.put(cart.getUserId(), cart);
        }

        return cart;
    }

    /**
     * ID로 장바구니 조회
     *
     * @param id 장바구니 ID
     * @return 조회된 장바구니, 없으면 null
     */
    public Cart findById(String id) {
        if (id == null) {
            return null;
        }
        return carts.get(id);
    }

    /**
     * 사용자 ID로 장바구니 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 장바구니, 없으면 null
     */
    public Cart findByUserId(String userId) {
        if (userId == null) {
            return null;
        }
        return userCartIndex.get(userId);
    }

    /**
     * 모든 장바구니 조회
     *
     * @return 전체 장바구니 목록
     */
    public List<Cart> findAll() {
        return new ArrayList<>(carts.values());
    }

    /**
     * 장바구니 수정
     *
     * @param cart 수정할 장바구니
     * @return 수정된 장바구니
     */
    public Cart update(Cart cart) {
        if (cart == null || cart.getId() == null) {
            throw new IllegalArgumentException("장바구니 정보가 유효하지 않습니다.");
        }

        if (!carts.containsKey(cart.getId())) {
            throw new IllegalArgumentException("존재하지 않는 장바구니입니다: " + cart.getId());
        }

        return save(cart);
    }

    /**
     * 장바구니 삭제
     *
     * @param id 삭제할 장바구니 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(String id) {
        if (id == null) {
            return false;
        }

        Cart removed = carts.remove(id);
        if (removed != null) {
            // 사용자 인덱스에서도 제거
            if (removed.getUserId() != null) {
                userCartIndex.remove(removed.getUserId());
            }
            return true;
        }

        return false;
    }

    /**
     * 사용자의 장바구니 삭제
     *
     * @param userId 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteByUserId(String userId) {
        if (userId == null) {
            return false;
        }

        Cart cart = userCartIndex.get(userId);
        if (cart != null) {
            return delete(cart.getId());
        }

        return false;
    }

    /**
     * 장바구니 ID 존재 여부 확인
     *
     * @param id 확인할 ID
     * @return 존재하면 true
     */
    public boolean existsById(String id) {
        return id != null && carts.containsKey(id);
    }

    /**
     * 사용자의 장바구니 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return 장바구니가 존재하면 true
     */
    public boolean existsByUserId(String userId) {
        return userId != null && userCartIndex.containsKey(userId);
    }

    /**
     * 장바구니 수 반환
     *
     * @return 전체 장바구니 수
     */
    public int count() {
        return carts.size();
    }

    /**
     * 비어있지 않은 장바구니 수 반환
     *
     * @return 상품이 담긴 장바구니 수
     */
    public int countNonEmpty() {
        return (int) carts.values().stream()
                .filter(cart -> !cart.isEmpty())
                .count();
    }

    /**
     * 모든 데이터 삭제
     */
    public void deleteAll() {
        carts.clear();
        userCartIndex.clear();
    }

    /**
     * 비어있는 장바구니 조회
     *
     * @return 비어있는 장바구니 목록
     */
    public List<Cart> findEmptyCarts() {
        List<Cart> emptyCarts = new ArrayList<>();
        for (Cart cart : carts.values()) {
            if (cart.isEmpty()) {
                emptyCarts.add(cart);
            }
        }
        return emptyCarts;
    }

    /**
     * 오래된 비어있는 장바구니 정리
     *
     * @param daysOld 며칠 이상 된 장바구니를 정리할지
     * @return 정리된 장바구니 수
     */
    public int cleanupOldEmptyCarts(int daysOld) {
        List<Cart> toRemove = new ArrayList<>();
        java.time.LocalDateTime cutoffDate =
                java.time.LocalDateTime.now().minusDays(daysOld);

        for (Cart cart : carts.values()) {
            if (cart.isEmpty() &&
                    cart.getModifiedDate().isBefore(cutoffDate)) {
                toRemove.add(cart);
            }
        }

        int count = 0;
        for (Cart cart : toRemove) {
            if (delete(cart.getId())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 저장소 상태 정보 반환
     *
     * @return 상태 정보 문자열
     */
    public String getStatistics() {
        int totalCarts = carts.size();
        int nonEmptyCarts = countNonEmpty();
        int emptyCarts = totalCarts - nonEmptyCarts;

        int totalItems = 0;
        for (Cart cart : carts.values()) {
            totalItems += cart.getItemCount();
        }

        return String.format(
                "=== 장바구니 저장소 통계 ===\n" +
                        "전체 장바구니: %d개\n" +
                        "활성 장바구니: %d개\n" +
                        "비어있는 장바구니: %d개\n" +
                        "전체 담긴 상품: %d개",
                totalCarts, nonEmptyCarts, emptyCarts, totalItems
        );
    }

    /**
     * 페이징 처리된 장바구니 목록 조회
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 장바구니 목록
     */
    public List<Cart> findAllPaged(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("유효하지 않은 페이징 파라미터입니다.");
        }

        List<Cart> allCarts = findAll();
        int start = page * size;
        int end = Math.min(start + size, allCarts.size());

        if (start >= allCarts.size()) {
            return new ArrayList<>();
        }

        return allCarts.subList(start, end);
    }
}