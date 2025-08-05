package javaproject.exception;

/**
 * 사용자가 잘못된 값을 입력했을 때 발생하는 예외
 * 회원가입, 상품 등록 등에서 입력값 검증 시 사용
 */
public class InvalidInputException extends Exception {

    // 잘못된 입력 필드명
    private String fieldName;

    // 잘못된 입력값
    private String inputValue;

    /**
     * 기본 생성자
     */
    public InvalidInputException() {
        super("잘못된 입력값입니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * 필드명과 값을 포함한 생성자
     * @param fieldName 잘못된 필드명
     * @param inputValue 잘못된 입력값
     * @param message 예외 메시지
     */
    public InvalidInputException(String fieldName, String inputValue, String message) {
        super(String.format("%s - 필드: %s, 입력값: %s", message, fieldName, inputValue));
        this.fieldName = fieldName;
        this.inputValue = inputValue;
    }

    // Getter 메서드들
    public String getFieldName() {
        return fieldName;
    }

    public String getInputValue() {
        return inputValue;
    }
}
