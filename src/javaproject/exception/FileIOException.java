// =================================================================
// FileIOException.java - 파일 입출력 예외 클래스
// =================================================================
package javaproject.exception;

/**
 * 파일 입출력 중 발생하는 예외 클래스
 * 파일 읽기/쓰기 실패 시 사용
 */
public class FileIOException extends Exception {

    // 관련 파일명
    private String fileName;

    /**
     * 기본 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public FileIOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 파일명을 포함한 생성자
     * @param fileName 파일명
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public FileIOException(String fileName, String message, Throwable cause) {
        super(message, cause);
        this.fileName = fileName;
    }

    /**
     * 파일명 반환
     * @return 파일명
     */
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileIOException: ");

        if (fileName != null) {
            sb.append("[파일: ").append(fileName).append("] ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}