// =================================================================
// InputValidator.java - 입력값 검증 유틸리티
// =================================================================
package javaproject.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 사용자 입력값을 검증하는 유틸리티 클래스
 * 회원가입, 상품등록 등에서 입력값 유효성 검사에 사용
 */
public class InputValidator {

    // 정규식 패턴들을 미리 컴파일해서 성능 향상
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,15}$");
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("^P\\d{3}$");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^ORD\\d{3}$");

    /**
     * 클래스 인스턴스 생성 방지 (유틸리티 클래스)
     */
    private InputValidator() {
        throw new UnsupportedOperationException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    // ================= 사용자 관련 검증 =================

    /**
     * 사용자 ID 유효성 검증
     * - 4-15자리
     * - 영문자와 숫자만 허용
     * - 첫 글자는 영문자
     * @param userId 검증할 사용자 ID
     * @return 유효하면 true
     */
    public static boolean isValidUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        userId = userId.trim();

        // 길이 검사
        if (userId.length() < 4 || userId.length() > 15) {
            return false;
        }

        // 첫 글자는 영문자여야 함
        if (!Character.isLetter(userId.charAt(0))) {
            return false;
        }

        // 정규식 검사
        return USER_ID_PATTERN.matcher(userId).matches();
    }

    /**
     * 비밀번호 유효성 검증
     * - 6-20자리
     * - 공백 불포함
     * @param password 검증할 비밀번호
     * @return 유효하면 true
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }

        // 길이 검사
        if (password.length() < 6 || password.length() > 20) {
            return false;
        }

        // 공백 포함 검사
        if (password.contains(" ")) {
            return false;
        }

        return true;
    }

    /**
     * 이메일 유효성 검증
     * @param email 검증할 이메일
     * @return 유효하면 true
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.trim();

        // 기본 길이 검사 (최소 5자: a@b.c)
        if (email.length() < 5) {
            return false;
        }

        // 정규식 검사
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 사용자 이름 유효성 검증
     * - 2-20자리
     * - 한글, 영문자만 허용
     * @param name 검증할 이름
     * @return 유효하면 true
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        name = name.trim();

        // 길이 검사
        if (name.length() < 2 || name.length() > 20) {
            return false;
        }

        // 한글, 영문자만 허용 (숫자, 특수문자 제외)
        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }

        return true;
    }

    // ================= 상품 관련 검증 =================

    /**
     * 상품명 유효성 검증
     * - 2-100자리
     * - 빈 문자열 불허
     * @param productName 검증할 상품명
     * @return 유효하면 true
     */
    public static boolean isValidProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }

        productName = productName.trim();

        // 길이 검사
        if (productName.length() < 2 || productName.length() > 100) {
            return false;
        }

        return true;
    }

    /**
     * 상품 가격 유효성 검증
     * - 0 이상의 정수
     * - 최대 가격 제한
     * @param price 검증할 가격
     * @return 유효하면 true
     */
    public static boolean isValidPrice(int price) {
        return price >= 0 && price <= 10_000_000;
    }

    /**
     * 재고 수량 유효성 검증
     * - 0 이상의 정수
     * @param stock 검증할 재고
     * @return 유효하면 true
     */
    public static boolean isValidStock(int stock) {
        return stock >= 0;
    }

    /**
     * 상품 카테고리 유효성 검증
     * - 2-50자리
     * - 빈 문자열 불허
     * @param category 검증할 카테고리
     * @return 유효하면 true
     */
    public static boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }

        category = category.trim();

        // 길이 검사
        if (category.length() < 2 || category.length() > 50) {
            return false;
        }

        return true;
    }

    /**
     * 상품 설명 유효성 검증
     * - 0-500자리 (선택적)
     * @param description 검증할 설명
     * @return 유효하면 true
     */
    public static boolean isValidDescription(String description) {
        if (description == null) {
            return true; // null은 허용 (선택적 항목)
        }

        // 길이 검사
        return description.length() <= 500;
    }

    // ================= 주문 관련 검증 =================

    /**
     * 주문 수량 유효성 검증
     * - 1 이상의 정수
     * - 최대 수량 제한
     * @param quantity 검증할 수량
     * @return 유효하면 true
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0 && quantity <= 999;
    }

    /**
     * 배송 주소 유효성 검증
     * - 10-200자리
     * - 빈 문자열 불허
     * @param address 검증할 주소
     * @return 유효하면 true
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        address = address.trim();

        // 길이 검사
        if (address.length() < 10 || address.length() > 200) {
            return false;
        }

        return true;
    }

    /**
     * 전화번호 유효성 검증
     * - 010-1234-5678 또는 01012345678 형식
     * @param phoneNumber 검증할 전화번호
     * @return 유효하면 true
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        phoneNumber = phoneNumber.trim().replaceAll("-", ""); // 하이픈 제거

        // 01X로 시작하는 11자리 숫자인지 확인
        if (!phoneNumber.matches("^01[0-9]\\d{8}$")) {
            return false;
        }

        return true;
    }

    // ================= ID 형식 검증 =================

    /**
     * 상품 ID 형식 검증 (P001, P002...)
     * @param productId 검증할 상품 ID
     * @return 유효하면 true
     */
    public static boolean isValidProductIdFormat(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return false;
        }

        return PRODUCT_ID_PATTERN.matcher(productId.trim()).matches();
    }

    /**
     * 주문 ID 형식 검증 (ORD001, ORD002...)
     * @param orderId 검증할 주문 ID
     * @return 유효하면 true
     */
    public static boolean isValidOrderIdFormat(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }

        return ORDER_ID_PATTERN.matcher(orderId.trim()).matches();
    }

    // ================= 공통 검증 =================

    /**
     * 문자열이 null이 아니고 비어있지 않은지 검증
     * @param str 검증할 문자열
     * @return 유효하면 true
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 숫자 문자열 검증
     * @param str 검증할 문자열
     * @return 숫자로 변환 가능하면 true
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 양의 정수 문자열 검증
     * @param str 검증할 문자열
     * @return 양의 정수로 변환 가능하면 true
     */
    public static boolean isPositiveInteger(String str) {
        if (!isNumeric(str)) {
            return false;
        }

        int number = Integer.parseInt(str.trim());
        return number > 0;
    }

    /**
     * 0 이상의 정수 문자열 검증
     * @param str 검증할 문자열
     * @return 0 이상의 정수로 변환 가능하면 true
     */
    public static boolean isNonNegativeInteger(String str) {
        if (!isNumeric(str)) {
            return false;
        }

        int number = Integer.parseInt(str.trim());
        return number >= 0;
    }

    // ================= 복합 검증 메서드 =================

    /**
     * 사용자 회원가입 정보 전체 검증
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param name 이름
     * @param email 이메일
     * @return 검증 결과 객체
     */
    public static ValidationResult validateUserRegistration(String userId, String password,
                                                            String name, String email) {
        ValidationResult result = new ValidationResult();

        // 사용자 ID 검증
        if (!isValidUserId(userId)) {
            result.addError("사용자 ID는 4-15자의 영문자와 숫자로 구성되어야 하며, 첫 글자는 영문자여야 합니다.");
        }

        // 비밀번호 검증
        if (!isValidPassword(password)) {
            result.addError("비밀번호는 6-20자로 구성되어야 하며, 공백을 포함할 수 없습니다.");
        }

        // 이름 검증
        if (!isValidName(name)) {
            result.addError("이름은 2-20자의 한글 또는 영문자로 구성되어야 합니다.");
        }

        // 이메일 검증
        if (!isValidEmail(email)) {
            result.addError("올바른 이메일 형식이 아닙니다.");
        }

        return result;
    }

    /**
     * 상품 등록 정보 전체 검증
     * @param name 상품명
     * @param price 가격
     * @param category 카테고리
     * @param stock 재고
     * @param description 설명
     * @return 검증 결과 객체
     */
    public static ValidationResult validateProductRegistration(String name, int price,
                                                               String category, int stock, String description) {
        ValidationResult result = new ValidationResult();

        // 상품명 검증
        if (!isValidProductName(name)) {
            result.addError("상품명은 2-100자로 구성되어야 합니다.");
        }

        // 가격 검증
        if (!isValidPrice(price)) {
            result.addError("가격은 0원 이상 10,000,000원 이하여야 합니다.");
        }

        // 카테고리 검증
        if (!isValidCategory(category)) {
            result.addError("카테고리는 2-50자로 구성되어야 합니다.");
        }

        // 재고 검증
        if (!isValidStock(stock)) {
            result.addError("재고는 0 이상의 정수여야 합니다.");
        }

        // 설명 검증
        if (!isValidDescription(description)) {
            result.addError("상품 설명은 500자 이하여야 합니다.");
        }

        return result;
    }

    /**
     * 주문 정보 검증
     * @param quantity 주문 수량
     * @param address 배송 주소
     * @param phoneNumber 전화번호
     * @return 검증 결과 객체
     */
    public static ValidationResult validateOrderInfo(int quantity, String address, String phoneNumber) {
        ValidationResult result = new ValidationResult();

        // 수량 검증
        if (!isValidQuantity(quantity)) {
            result.addError("주문 수량은 1개 이상 999개 이하여야 합니다.");
        }

        // 주소 검증
        if (!isValidAddress(address)) {
            result.addError("배송 주소는 10-200자로 입력해주세요.");
        }

        // 전화번호 검증
        if (!isValidPhoneNumber(phoneNumber)) {
            result.addError("올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)");
        }

        return result;
    }

    // ================= 검증 결과 클래스 =================

    /**
     * 검증 결과를 담는 내부 클래스
     */
    public static class ValidationResult {
        private List<String> errors;
        private boolean valid;

        public ValidationResult() {
            this.errors = new ArrayList<>();
            this.valid = true;
        }

        /**
         * 에러 메시지 추가
         * @param errorMessage 에러 메시지
         */
        public void addError(String errorMessage) {
            errors.add(errorMessage);
            valid = false;
        }

        /**
         * 검증 성공 여부
         * @return 모든 검증이 성공하면 true
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * 에러 메시지 목록
         * @return 에러 메시지 리스트
         */
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        /**
         * 첫 번째 에러 메시지
         * @return 첫 번째 에러 메시지 (없으면 null)
         */
        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }

        /**
         * 모든 에러 메시지를 하나의 문자열로 결합
         * @return 결합된 에러 메시지
         */
        public String getAllErrorsAsString() {
            if (errors.isEmpty()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                sb.append("- ").append(errors.get(i));
                if (i < errors.size() - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "valid=" + valid +
                    ", errorCount=" + errors.size() +
                    '}';
        }
    }
}