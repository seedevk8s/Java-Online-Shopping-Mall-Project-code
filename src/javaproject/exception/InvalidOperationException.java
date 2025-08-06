// =================================================================
// InvalidOperationException.java - 잘못된 작업 예외 클래스
// =================================================================
package javaproject.exception;

/**
 * 잘못된 작업이나 허용되지 않는 연산을 시도할 때 발생하는 예외
 * 예: 배송 완료된 주문 취소, 이미 처리된 작업 재시도 등
 */
public class InvalidOperationException extends Exception {

    // 작업 이름
    private String operation;

    // 작업 상태
    private String currentState;

    /**
     * 기본 생성자
     * @param message 예외 메시지
     */
    public InvalidOperationException(String message) {
        super(message);
        this.operation = "UNKNOWN";
        this.currentState = "UNKNOWN";
    }

    /**
     * 작업 정보를 포함한 생성자
     * @param operation 시도한 작업
     * @param message 예외 메시지
     */
    public InvalidOperationException(String operation, String message) {
        super(message);
        this.operation = operation;
        this.currentState = "UNKNOWN";
    }

    /**
     * 전체 정보를 포함한 생성자
     * @param operation 시도한 작업
     * @param currentState 현재 상태
     * @param message 예외 메시지
     */
    public InvalidOperationException(String operation, String currentState, String message) {
        super(message);
        this.operation = operation;
        this.currentState = currentState;
    }

    /**
     * 원인 예외를 포함한 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
        this.operation = "UNKNOWN";
        this.currentState = "UNKNOWN";
    }

    /**
     * 전체 정보와 원인을 포함한 생성자
     * @param operation 시도한 작업
     * @param currentState 현재 상태
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public InvalidOperationException(String operation, String currentState,
                                     String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.currentState = currentState;
    }

    // Getter 메서드들

    /**
     * 시도한 작업 반환
     * @return 작업 이름
     */
    public String getOperation() {
        return operation;
    }

    /**
     * 현재 상태 반환
     * @return 현재 상태
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * 상세 메시지 반환
     * @return 상세 메시지
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("InvalidOperationException: ");

        if (!"UNKNOWN".equals(operation)) {
            sb.append("[작업: ").append(operation).append("] ");
        }

        if (!"UNKNOWN".equals(currentState)) {
            sb.append("[현재 상태: ").append(currentState).append("] ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}