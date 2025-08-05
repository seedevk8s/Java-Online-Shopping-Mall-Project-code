package javaproject.util;

/**
 * 애플리케이션에서 사용하는 상수들을 정의
 * 파일명, 메뉴 번호, 비즈니스 규칙 등
 */
public class Constants {

    // ================= 파일 관련 상수 =================

    /** 사용자 데이터 파일명 */
    public static final String USER_DATA_FILE = "users.txt";

    /** 상품 데이터 파일명 */
    public static final String PRODUCT_DATA_FILE = "products.txt";

    /** 주문 데이터 파일명 */
    public static final String ORDER_DATA_FILE = "orders.txt";

    /** 주문 아이템 데이터 파일명 */
    public static final String ORDER_ITEM_DATA_FILE = "order_items.txt";

    /** 장바구니 데이터 파일명 */
    public static final String CART_DATA_FILE = "carts.txt";

    /** 장바구니 아이템 데이터 파일명 */
    public static final String CART_ITEM_DATA_FILE = "cart_items.txt";

    // ================= 비즈니스 규칙 상수 =================

    /** 최소 비밀번호 길이 */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /** 최대 비밀번호 길이 */
    public static final int MAX_PASSWORD_LENGTH = 20;

    /** 최대 장바구니 아이템 수 */
    public static final int MAX_CART_ITEMS = 50;

    /** 최소 상품 가격 */
    public static final int MIN_PRODUCT_PRICE = 0;

    /** 최대 상품 가격 */
    public static final int MAX_PRODUCT_PRICE = 10_000_000; // 천만원

    /** 최대 주문 수량 (한 번에) */
    public static final int MAX_ORDER_QUANTITY = 999;

    /** 기본 관리자 ID */
    public static final String DEFAULT_ADMIN_ID = "admin";

    /** 기본 관리자 비밀번호 */
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    // ================= 메뉴 번호 상수 =================

    /** 메인 메뉴 - 종료 */
    public static final int MAIN_MENU_EXIT = 0;

    /** 메인 메뉴 - 사용자 관리 */
    public static final int MAIN_MENU_USER = 1;

    /** 메인 메뉴 - 상품 관리 */
    public static final int MAIN_MENU_PRODUCT = 2;

    /** 메인 메뉴 - 주문 관리 */
    public static final int MAIN_MENU_ORDER = 3;

    /** 메인 메뉴 - 로그아웃 */
    public static final int MAIN_MENU_LOGOUT = 4;

    // 사용자 메뉴
    public static final int USER_MENU_EXIT = 0;
    public static final int USER_MENU_LOGIN = 1;
    public static final int USER_MENU_REGISTER = 2;
    public static final int USER_MENU_MYPAGE = 3;

    // 상품 메뉴
    public static final int PRODUCT_MENU_EXIT = 0;
    public static final int PRODUCT_MENU_VIEW_ALL = 1;
    public static final int PRODUCT_MENU_SEARCH = 2;
    public static final int PRODUCT_MENU_DETAIL = 3;
    public static final int PRODUCT_MENU_ADMIN = 4;

    // 주문 메뉴
    public static final int ORDER_MENU_EXIT = 0;
    public static final int ORDER_MENU_CART = 1;
    public static final int ORDER_MENU_CHECKOUT = 2;
    public static final int ORDER_MENU_HISTORY = 3;
    public static final int ORDER_MENU_CANCEL = 4;

    // ================= 메시지 상수 =================

    /** 성공 메시지 */
    public static final String MSG_SUCCESS = "✓ 성공적으로 처리되었습니다.";

    /** 실패 메시지 */
    public static final String MSG_FAILURE = "✗ 처리에 실패했습니다.";

    /** 로그인 필요 메시지 */
    public static final String MSG_LOGIN_REQUIRED = "로그인이 필요한 서비스입니다.";

    /** 관리자 권한 필요 메시지 */
    public static final String MSG_ADMIN_REQUIRED = "관리자 권한이 필요합니다.";

    /** 잘못된 입력 메시지 */
    public static final String MSG_INVALID_INPUT = "잘못된 입력입니다. 다시 시도해주세요.";

    /** 데이터 없음 메시지 */
    public static final String MSG_NO_DATA = "조회된 데이터가 없습니다.";

    // ================= 정규식 패턴 =================

    /** 이메일 검증 정규식 (간단한 형태) */
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /** 사용자 ID 검증 정규식 (영문+숫자, 4-15자) */
    public static final String USER_ID_PATTERN = "^[a-zA-Z0-9]{4,15}$";

    /** 상품 ID 패턴 (P + 3자리 숫자) */
    public static final String PRODUCT_ID_PATTERN = "^P\\d{3}$";

    /** 주문 ID 패턴 (ORD + 3자리 숫자) */
    public static final String ORDER_ID_PATTERN = "^ORD\\d{3}$";

    // ================= 유틸리티 메서드 =================

    /**
     * 클래스 인스턴스 생성 방지
     */
    private Constants() {
        throw new UnsupportedOperationException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    /**
     * 상수 정보 출력 (디버깅용)
     */
    public static void printConstants() {
        System.out.println("=== 애플리케이션 상수 정보 ===");
        System.out.println("사용자 데이터 파일: " + USER_DATA_FILE);
        System.out.println("상품 데이터 파일: " + PRODUCT_DATA_FILE);
        System.out.println("주문 데이터 파일: " + ORDER_DATA_FILE);
        System.out.println("최소 비밀번호 길이: " + MIN_PASSWORD_LENGTH);
        System.out.println("최대 장바구니 아이템: " + MAX_CART_ITEMS);
        System.out.println("최대 상품 가격: " + String.format("%,d원", MAX_PRODUCT_PRICE));
        System.out.println("============================");
    }
}
