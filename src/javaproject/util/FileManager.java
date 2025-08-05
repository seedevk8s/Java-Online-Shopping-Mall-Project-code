package javaproject.util;


import javaproject.exception.FileIOException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 입출력을 담당하는 유틸리티 클래스
 * 모든 Repository에서 공통으로 사용
 */
public class FileManager {

    // 데이터 파일들이 저장될 기본 디렉토리
    private static final String DATA_DIRECTORY = "data";

    // 파일 인코딩 (한글 지원)
    private static final String FILE_ENCODING = "UTF-8";

    /**
     * 데이터 디렉토리 초기화
     * 프로그램 시작 시 호출하여 data 폴더 생성
     */
    public static void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath); // data 폴더 생성
                System.out.println("데이터 디렉토리가 생성되었습니다: " + DATA_DIRECTORY);
            }
        } catch (IOException e) {
            System.err.println("데이터 디렉토리 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 파일 경로 생성 (data 폴더 포함)
     * @param fileName 파일명
     * @return 전체 파일 경로
     */
    public static String getFilePath(String fileName) {
        return DATA_DIRECTORY + File.separator + fileName;
    }

    /**
     * 파일에 문자열 목록을 한 줄씩 저장
     * @param fileName 파일명 (확장자 포함)
     * @param lines 저장할 문자열 목록
     * @throws FileIOException 파일 저장 실패 시
     */
    public static void writeLines(String fileName, List<String> lines) throws FileIOException {
        String filePath = getFilePath(fileName);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), FILE_ENCODING))) {

            // 각 줄을 파일에 쓰기
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 줄바꿈 추가
            }

        } catch (IOException e) {
            throw new FileIOException(filePath, "파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일에서 모든 줄을 읽어서 문자열 목록으로 반환
     * @param fileName 파일명 (확장자 포함)
     * @return 읽은 문자열 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    public static List<String> readLines(String fileName) throws FileIOException {
        String filePath = getFilePath(fileName);
        List<String> lines = new ArrayList<>();

        // 파일이 존재하지 않으면 빈 목록 반환
        if (!fileExists(fileName)) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), FILE_ENCODING))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 빈 줄이 아닌 경우에만 추가
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            throw new FileIOException(filePath, "파일 읽기 중 오류가 발생했습니다.", e);
        }

        return lines;
    }

    /**
     * 파일에 단일 문자열 줄 추가 (append 모드)
     * @param fileName 파일명
     * @param line 추가할 문자열
     * @throws FileIOException 파일 저장 실패 시
     */
    public static void appendLine(String fileName, String line) throws FileIOException {
        String filePath = getFilePath(fileName);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath, true), FILE_ENCODING))) {

            writer.write(line);
            writer.newLine();

        } catch (IOException e) {
            throw new FileIOException(filePath, "파일 추가 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 존재 여부 확인
     * @param fileName 파일명
     * @return 파일이 존재하면 true
     */
    public static boolean fileExists(String fileName) {
        String filePath = getFilePath(fileName);
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 파일 삭제
     * @param fileName 삭제할 파일명
     * @return 삭제 성공 여부
     */
    public static boolean deleteFile(String fileName) {
        String filePath = getFilePath(fileName);
        File file = new File(filePath);

        if (file.exists()) {
            return file.delete();
        }
        return false; // 파일이 존재하지 않음
    }

    /**
     * 파일 크기 조회 (줄 수)
     * @param fileName 파일명
     * @return 파일의 줄 수
     * @throws FileIOException 파일 읽기 실패 시
     */
    public static long getLineCount(String fileName) throws FileIOException {
        List<String> lines = readLines(fileName);
        return lines.size();
    }

    /**
     * 파일 백업 생성
     * @param fileName 백업할 파일명
     * @return 백업 성공 여부
     */
    public static boolean backupFile(String fileName) {
        if (!fileExists(fileName)) {
            return false; // 원본 파일이 없음
        }

        try {
            // 백업 파일명 생성 (원본파일명_backup.확장자)
            String backupFileName = generateBackupFileName(fileName);
            List<String> lines = readLines(fileName);
            writeLines(backupFileName, lines);

            System.out.println("파일 백업 완료: " + fileName + " -> " + backupFileName);
            return true;

        } catch (FileIOException e) {
            System.err.println("파일 백업 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 백업 파일명 생성
     * @param originalFileName 원본 파일명
     * @return 백업 파일명
     */
    private static String generateBackupFileName(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex == -1) {
            // 확장자가 없는 경우
            return originalFileName + "_backup";
        } else {
            // 확장자가 있는 경우
            String nameWithoutExt = originalFileName.substring(0, dotIndex);
            String extension = originalFileName.substring(dotIndex);
            return nameWithoutExt + "_backup" + extension;
        }
    }

    /**
     * 파일 내용을 특정 조건으로 필터링하여 새 파일로 저장
     * @param sourceFileName 원본 파일명
     * @param targetFileName 대상 파일명
     * @param filterFunction 필터링 함수 (String -> boolean)
     * @throws FileIOException 파일 처리 실패 시
     */
    public static void filterAndSave(String sourceFileName, String targetFileName,
                                     java.util.function.Predicate<String> filterFunction) throws FileIOException {
        List<String> sourceLines = readLines(sourceFileName);
        List<String> filteredLines = new ArrayList<>();

        // 조건에 맞는 줄만 필터링
        for (String line : sourceLines) {
            if (filterFunction.test(line)) {
                filteredLines.add(line);
            }
        }

        // 필터링된 결과를 새 파일에 저장
        writeLines(targetFileName, filteredLines);
    }

    /**
     * 여러 파일을 하나로 합치기
     * @param sourceFileNames 합칠 파일명들
     * @param targetFileName 결과 파일명
     * @throws FileIOException 파일 처리 실패 시
     */
    public static void mergeFiles(List<String> sourceFileNames, String targetFileName) throws FileIOException {
        List<String> allLines = new ArrayList<>();

        // 각 파일의 내용을 순서대로 읽어서 합치기
        for (String sourceFileName : sourceFileNames) {
            if (fileExists(sourceFileName)) {
                List<String> lines = readLines(sourceFileName);
                allLines.addAll(lines);
            }
        }

        // 합친 내용을 새 파일에 저장
        writeLines(targetFileName, allLines);
    }

    /**
     * 파일 정보 출력 (디버깅용)
     * @param fileName 파일명
     */
    public static void printFileInfo(String fileName) {
        String filePath = getFilePath(fileName);
        File file = new File(filePath);

        System.out.println("=== 파일 정보 ===");
        System.out.println("파일명: " + fileName);
        System.out.println("전체 경로: " + filePath);
        System.out.println("존재 여부: " + file.exists());

        if (file.exists()) {
            System.out.println("파일 크기: " + file.length() + " bytes");
            System.out.println("마지막 수정: " + new java.util.Date(file.lastModified()));

            try {
                long lineCount = getLineCount(fileName);
                System.out.println("줄 수: " + lineCount);
            } catch (FileIOException e) {
                System.out.println("줄 수 조회 실패: " + e.getMessage());
            }
        }
        System.out.println("================");
    }
}
