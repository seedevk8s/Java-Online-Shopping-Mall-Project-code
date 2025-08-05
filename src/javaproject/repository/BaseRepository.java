// =================================================================
// BaseRepository.java - Repository 공통 인터페이스
// =================================================================
package javaproject.repository;

import javaproject.exception.FileIOException;

import java.util.List;
import java.util.Optional;

/**
 * 모든 Repository가 구현해야 하는 기본 인터페이스
 * CRUD 기본 연산을 정의
 * @param <T> 도메인 객체 타입
 * @param <ID> ID 타입
 */
public interface BaseRepository<T, ID> {

    /**
     * 새로운 엔티티 저장
     * @param entity 저장할 엔티티
     * @return 저장 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    boolean save(T entity) throws FileIOException;

    /**
     * ID로 엔티티 조회
     * @param id 조회할 ID
     * @return 조회된 엔티티 (Optional)
     * @throws FileIOException 파일 읽기 실패 시
     */
    Optional<T> findById(ID id) throws FileIOException;

    /**
     * 모든 엔티티 조회
     * @return 전체 엔티티 목록
     * @throws FileIOException 파일 읽기 실패 시
     */
    List<T> findAll() throws FileIOException;

    /**
     * 엔티티 수정
     * @param entity 수정할 엔티티
     * @return 수정 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    boolean update(T entity) throws FileIOException;

    /**
     * ID로 엔티티 삭제
     * @param id 삭제할 ID
     * @return 삭제 성공 여부
     * @throws FileIOException 파일 저장 실패 시
     */
    boolean deleteById(ID id) throws FileIOException;

    /**
     * 엔티티 존재 여부 확인
     * @param id 확인할 ID
     * @return 존재하면 true
     * @throws FileIOException 파일 읽기 실패 시
     */
    boolean existsById(ID id) throws FileIOException;

    /**
     * 전체 엔티티 개수
     * @return 총 개수
     * @throws FileIOException 파일 읽기 실패 시
     */
    long count() throws FileIOException;
}