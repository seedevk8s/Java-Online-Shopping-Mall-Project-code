package javaproject.exception;

import java.io.IOException;

/**
 * 파일 입출력 과정에서 발생하는 예외
 * 데이터 저장/로드 시 사용
 */
public class FileIOException extends Exception {

    // 파일 경로
    private String filePath;

    // 원본 IOException
    private IOException originalException;

    /**
     * 기본 생성자
     */
    public FileIOException() {
        super("파일 입출력 중 오류가 발생했습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public FileIOException(String message) {
        super(message);
    }

    /**
     * 파일 경로와 원본 예외를 포함한 생성자
     * @param filePath 파일 경로
     * @param originalException 원본 IOException
     */
    public FileIOException(String filePath, IOException originalException) {
        super("파일 입출력 중 오류가 발생했습니다. 파일: " + filePath + ", 원인: " + originalException.getMessage());
        this.filePath = filePath;
        this.originalException = originalException;
    }

    /**
     * 상세 정보를 포함한 생성자
     * @param filePath 파일 경로
     * @param message 예외 메시지
     * @param originalException 원본 IOException
     */
    public FileIOException(String filePath, String message, IOException originalException) {
        super(message);
        this.filePath = filePath;
        this.originalException = originalException;
    }

    // Getter 메서드들
    public String getFilePath() {
        return filePath;
    }

    public IOException getOriginalException() {
        return originalException;
    }
}
