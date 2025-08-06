package javaproject.exception;

/**
 * 잘못된 입력 예외 클래스
 * 입력값이 유효성 검사를 통과하지 못할 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class InvalidInputException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public InvalidInputException() {
        super("입력값이 유효하지 않습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public InvalidInputException(Throwable cause) {
        super(cause);
    }

    /**
     * 필드명을 포함한 상세 메시지 생성
     *
     * @param fieldName 유효하지 않은 필드명
     * @return 상세 예외 메시지
     */
    public static InvalidInputException withFieldName(String fieldName) {
        return new InvalidInputException(
                String.format("%s 입력값이 유효하지 않습니다.", fieldName)
        );
    }

    /**
     * 필드명과 이유를 포함한 상세 메시지 생성
     *
     * @param fieldName 유효하지 않은 필드명
     * @param reason 유효하지 않은 이유
     * @return 상세 예외 메시지
     */
    public static InvalidInputException withReason(String fieldName, String reason) {
        return new InvalidInputException(
                String.format("%s이(가) 유효하지 않습니다: %s", fieldName, reason)
        );
    }

    /**
     * 범위 벗어남 메시지 생성
     *
     * @param value 입력값
     * @param min 최소값
     * @param max 최대값
     * @return 범위 벗어남 예외
     */
    public static InvalidInputException outOfRange(int value, int min, int max) {
        return new InvalidInputException(
                String.format("입력값 %d은(는) 유효 범위(%d~%d)를 벗어났습니다.", value, min, max)
        );
    }

    /**
     * 최소 길이 미달 메시지 생성
     *
     * @param fieldName 필드명
     * @param minLength 최소 길이
     * @return 길이 미달 예외
     */
    public static InvalidInputException tooShort(String fieldName, int minLength) {
        return new InvalidInputException(
                String.format("%s은(는) 최소 %d자 이상이어야 합니다.", fieldName, minLength)
        );
    }

    /**
     * 최대 길이 초과 메시지 생성
     *
     * @param fieldName 필드명
     * @param maxLength 최대 길이
     * @return 길이 초과 예외
     */
    public static InvalidInputException tooLong(String fieldName, int maxLength) {
        return new InvalidInputException(
                String.format("%s은(는) 최대 %d자를 초과할 수 없습니다.", fieldName, maxLength)
        );
    }

    /**
     * 형식 오류 메시지 생성
     *
     * @param fieldName 필드명
     * @param expectedFormat 예상 형식
     * @return 형식 오류 예외
     */
    public static InvalidInputException invalidFormat(String fieldName, String expectedFormat) {
        return new InvalidInputException(
                String.format("%s의 형식이 올바르지 않습니다. 올바른 형식: %s", fieldName, expectedFormat)
        );
    }

    /**
     * 필수 입력 누락 메시지 생성
     *
     * @param fieldName 필드명
     * @return 필수 입력 누락 예외
     */
    public static InvalidInputException required(String fieldName) {
        return new InvalidInputException(
                String.format("%s은(는) 필수 입력 항목입니다.", fieldName)
        );
    }

    /**
     * 숫자 형식 오류 메시지 생성
     *
     * @param input 잘못된 입력값
     * @return 숫자 형식 오류 예외
     */
    public static InvalidInputException notANumber(String input) {
        return new InvalidInputException(
                String.format("'%s'은(는) 올바른 숫자가 아닙니다.", input)
        );
    }

    /**
     * 선택지 오류 메시지 생성
     *
     * @param value 입력값
     * @param validOptions 유효한 선택지들
     * @return 선택지 오류 예외
     */
    public static InvalidInputException invalidOption(String value, String validOptions) {
        return new InvalidInputException(
                String.format("'%s'은(는) 유효한 선택지가 아닙니다. 가능한 선택지: %s", value, validOptions)
        );
    }
}