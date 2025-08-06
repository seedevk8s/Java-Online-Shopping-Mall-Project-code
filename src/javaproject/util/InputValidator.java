package javaproject.util;

import javaproject.exception.InvalidInputException;
import java.util.regex.Pattern;

/**
 * 입력 검증 유틸리티 클래스
 * 싱글톤 패턴을 적용하여 전역적으로 하나의 인스턴스만 존재
 * 사용자 입력에 대한 유효성 검사 기능 제공
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class InputValidator {

    // 싱글톤 인스턴스 - 클래스 로딩 시점에 생성 (thread-safe)
    private static final InputValidator instance = new InputValidator();

    // 정규표현식 패턴들 (컴파일된 패턴을 재사용하여 성능 향상)
    private final Pattern emailPattern;
    private final Pattern phonePattern;
    private final Pattern idPattern;
    private final Pattern passwordPattern;
    private final Pattern namePattern;

    // 검증 규칙 상수들
    private static final int MIN_ID_LENGTH = 4;
    private static final int MAX_ID_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 1000000000.0; // 10억
    private static final int MIN_QUANTITY = 0;
    private static final int MAX_QUANTITY = 10000;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     * 정규표현식 패턴들을 미리 컴파일하여 성능 최적화
     */
    private InputValidator() {
        // 이메일 패턴: 기본적인 이메일 형식
        this.emailPattern = Pattern.compile(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );

        // 전화번호 패턴: 한국 휴대폰 번호 형식 (010-XXXX-XXXX)
        this.phonePattern = Pattern.compile(
                "^010-\\d{4}-\\d{4}$"
        );

        // ID 패턴: 영문자로 시작, 영문자와 숫자만 허용
        this.idPattern = Pattern.compile(
                "^[a-zA-Z][a-zA-Z0-9]*$"
        );

        // 비밀번호 패턴: 영문자, 숫자, 특수문자 허용
        this.passwordPattern = Pattern.compile(
                "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]*$"
        );

        // 이름 패턴: 한글, 영문, 공백 허용
        this.namePattern = Pattern.compile(
                "^[가-힣a-zA-Z\\s]+$"
        );
    }

    /**
     * 싱글톤 인스턴스를 반환하는 정적 메서드
     * @return InputValidator의 유일한 인스턴스
     */
    public static InputValidator getInstance() {
        return instance;
    }

    /**
     * 메뉴 선택 입력 검증
     * @param input 사용자 입력 문자열
     * @param min 최소값
     * @param max 최대값
     * @return 검증된 정수값
     * @throws InvalidInputException 유효하지 않은 입력인 경우
     */
    public int validateMenuChoice(String input, int min, int max)
            throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException("입력값이 비어있습니다.");
        }

        try {
            int choice = Integer.parseInt(input.trim());

            // 범위 검증
            if (choice < min || choice > max) {
                throw new InvalidInputException(
                        String.format("%d~%d 사이의 숫자를 입력해주세요.", min, max)
                );
            }

            return choice;

        } catch (NumberFormatException e) {
            throw new InvalidInputException("숫자를 입력해주세요.");
        }
    }

    /**
     * 사용자 ID 검증
     * @param id 사용자 ID
     * @return 검증된 ID
     * @throws InvalidInputException 유효하지 않은 ID인 경우
     */
    public String validateUserId(String id) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException("ID를 입력해주세요.");
        }

        id = id.trim();

        // 길이 검증
        if (id.length() < MIN_ID_LENGTH || id.length() > MAX_ID_LENGTH) {
            throw new InvalidInputException(
                    String.format("ID는 %d~%d자 사이여야 합니다.",
                            MIN_ID_LENGTH, MAX_ID_LENGTH)
            );
        }

        // 형식 검증
        if (!idPattern.matcher(id).matches()) {
            throw new InvalidInputException(
                    "ID는 영문자로 시작하며, 영문자와 숫자만 사용 가능합니다."
            );
        }

        return id;
    }

    /**
     * 비밀번호 검증
     * @param password 비밀번호
     * @return 검증된 비밀번호
     * @throws InvalidInputException 유효하지 않은 비밀번호인 경우
     */
    public String validatePassword(String password) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("비밀번호를 입력해주세요.");
        }

        // 길이 검증
        if (password.length() < MIN_PASSWORD_LENGTH ||
                password.length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidInputException(
                    String.format("비밀번호는 %d~%d자 사이여야 합니다.",
                            MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH)
            );
        }

        // 형식 검증
        if (!passwordPattern.matcher(password).matches()) {
            throw new InvalidInputException(
                    "비밀번호에 허용되지 않는 문자가 포함되어 있습니다."
            );
        }

        // 보안 강도 체크 (선택적)
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (hasLetter && hasDigit) break;
        }

        if (!hasLetter || !hasDigit) {
            throw new InvalidInputException(
                    "비밀번호는 영문자와 숫자를 모두 포함해야 합니다."
            );
        }

        return password;
    }

    /**
     * 이메일 검증
     * @param email 이메일 주소
     * @return 검증된 이메일
     * @throws InvalidInputException 유효하지 않은 이메일인 경우
     */
    public String validateEmail(String email) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("이메일을 입력해주세요.");
        }

        email = email.trim().toLowerCase();

        // 형식 검증
        if (!emailPattern.matcher(email).matches()) {
            throw new InvalidInputException(
                    "올바른 이메일 형식이 아닙니다. (예: user@example.com)"
            );
        }

        return email;
    }

    /**
     * 전화번호 검증
     * @param phone 전화번호
     * @return 검증된 전화번호
     * @throws InvalidInputException 유효하지 않은 전화번호인 경우
     */
    public String validatePhone(String phone) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidInputException("전화번호를 입력해주세요.");
        }

        phone = phone.trim();

        // 형식 검증
        if (!phonePattern.matcher(phone).matches()) {
            throw new InvalidInputException(
                    "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)"
            );
        }

        return phone;
    }

    /**
     * 이름 검증
     * @param name 이름
     * @return 검증된 이름
     * @throws InvalidInputException 유효하지 않은 이름인 경우
     */
    public String validateName(String name) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("이름을 입력해주세요.");
        }

        name = name.trim();

        // 길이 검증
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new InvalidInputException(
                    String.format("이름은 %d~%d자 사이여야 합니다.",
                            MIN_NAME_LENGTH, MAX_NAME_LENGTH)
            );
        }

        // 형식 검증
        if (!namePattern.matcher(name).matches()) {
            throw new InvalidInputException(
                    "이름은 한글 또는 영문자만 사용 가능합니다."
            );
        }

        return name;
    }

    /**
     * 주소 검증
     * @param address 주소
     * @return 검증된 주소
     * @throws InvalidInputException 유효하지 않은 주소인 경우
     */
    public String validateAddress(String address) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (address == null || address.trim().isEmpty()) {
            throw new InvalidInputException("주소를 입력해주세요.");
        }

        address = address.trim();

        // 최소 길이 검증
        if (address.length() < 5) {
            throw new InvalidInputException("주소가 너무 짧습니다.");
        }

        // 최대 길이 검증
        if (address.length() > 200) {
            throw new InvalidInputException("주소가 너무 깁니다. (최대 200자)");
        }

        return address;
    }

    /**
     * 상품명 검증
     * @param productName 상품명
     * @return 검증된 상품명
     * @throws InvalidInputException 유효하지 않은 상품명인 경우
     */
    public String validateProductName(String productName)
            throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (productName == null || productName.trim().isEmpty()) {
            throw new InvalidInputException("상품명을 입력해주세요.");
        }

        productName = productName.trim();

        // 길이 검증
        if (productName.length() < 2) {
            throw new InvalidInputException("상품명은 최소 2자 이상이어야 합니다.");
        }

        if (productName.length() > 100) {
            throw new InvalidInputException("상품명은 최대 100자까지 가능합니다.");
        }

        return productName;
    }

    /**
     * 가격 검증
     * @param priceStr 가격 문자열
     * @return 검증된 가격
     * @throws InvalidInputException 유효하지 않은 가격인 경우
     */
    public double validatePrice(String priceStr) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (priceStr == null || priceStr.trim().isEmpty()) {
            throw new InvalidInputException("가격을 입력해주세요.");
        }

        try {
            double price = Double.parseDouble(priceStr.trim());

            // 음수 체크
            if (price < MIN_PRICE) {
                throw new InvalidInputException("가격은 0원 이상이어야 합니다.");
            }

            // 최대값 체크
            if (price > MAX_PRICE) {
                throw new InvalidInputException(
                        String.format("가격은 %,.0f원 이하여야 합니다.", MAX_PRICE)
                );
            }

            return price;

        } catch (NumberFormatException e) {
            throw new InvalidInputException("올바른 숫자를 입력해주세요.");
        }
    }

    /**
     * 수량 검증
     * @param quantityStr 수량 문자열
     * @return 검증된 수량
     * @throws InvalidInputException 유효하지 않은 수량인 경우
     */
    public int validateQuantity(String quantityStr)
            throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            throw new InvalidInputException("수량을 입력해주세요.");
        }

        try {
            int quantity = Integer.parseInt(quantityStr.trim());

            // 최소값 체크
            if (quantity < MIN_QUANTITY) {
                throw new InvalidInputException("수량은 0개 이상이어야 합니다.");
            }

            // 최대값 체크
            if (quantity > MAX_QUANTITY) {
                throw new InvalidInputException(
                        String.format("수량은 %d개 이하여야 합니다.", MAX_QUANTITY)
                );
            }

            return quantity;

        } catch (NumberFormatException e) {
            throw new InvalidInputException("올바른 숫자를 입력해주세요.");
        }
    }

    /**
     * Yes/No 입력 검증
     * @param input 사용자 입력
     * @return true(Y/y) 또는 false(N/n)
     * @throws InvalidInputException 유효하지 않은 입력인 경우
     */
    public boolean validateYesNo(String input) throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException("Y(예) 또는 N(아니오)를 입력해주세요.");
        }

        String normalized = input.trim().toUpperCase();

        if ("Y".equals(normalized) || "YES".equals(normalized)) {
            return true;
        } else if ("N".equals(normalized) || "NO".equals(normalized)) {
            return false;
        } else {
            throw new InvalidInputException("Y(예) 또는 N(아니오)를 입력해주세요.");
        }
    }

    /**
     * 검색 키워드 검증
     * @param keyword 검색 키워드
     * @return 검증된 키워드
     * @throws InvalidInputException 유효하지 않은 키워드인 경우
     */
    public String validateSearchKeyword(String keyword)
            throws InvalidInputException {
        // null 또는 빈 문자열 체크
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidInputException("검색어를 입력해주세요.");
        }

        keyword = keyword.trim();

        // 최소 길이 체크
        if (keyword.length() < 2) {
            throw new InvalidInputException("검색어는 최소 2자 이상 입력해주세요.");
        }

        // 최대 길이 체크
        if (keyword.length() > 50) {
            throw new InvalidInputException("검색어는 최대 50자까지 가능합니다.");
        }

        return keyword;
    }
}