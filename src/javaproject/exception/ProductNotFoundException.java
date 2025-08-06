// =================================================================
// ProductNotFoundException.java - 상품을 찾을 수 없을 때 발생하는 예외
// =================================================================
package javaproject.exception;

/**
 * 상품을 찾을 수 없을 때 발생하는 예외 클래스
 * 상품 ID로 조회 시 해당 상품이 존재하지 않는 경우
 */
public class ProductNotFoundException extends Exception {

    // 찾을 수 없는 상품 ID
    private String productId;

    // ID로 검색했는지 여부
    private boolean searchedById;

    /**
     * 기본 생성자
     * @param message 예외 메시지
     */
    public ProductNotFoundException(String message) {
        super(message);
        this.searchedById = false;
    }

    /**
     * 상품 ID를 포함한 생성자
     * @param productId 찾을 수 없는 상품 ID
     * @param searchedById ID로 검색했는지 여부
     */
    public ProductNotFoundException(String productId, boolean searchedById) {
        super(createMessage(productId, searchedById));
        this.productId = productId;
        this.searchedById = searchedById;
    }

    /**
     * 원인 예외를 포함한 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.searchedById = false;
    }

    /**
     * 전체 정보를 포함한 생성자
     * @param productId 찾을 수 없는 상품 ID
     * @param searchedById ID로 검색했는지 여부
     * @param cause 원인 예외
     */
    public ProductNotFoundException(String productId, boolean searchedById, Throwable cause) {
        super(createMessage(productId, searchedById), cause);
        this.productId = productId;
        this.searchedById = searchedById;
    }

    /**
     * 예외 메시지 생성
     * @param productId 상품 ID
     * @param searchedById ID로 검색했는지 여부
     * @return 생성된 메시지
     */
    private static String createMessage(String productId, boolean searchedById) {
        if (searchedById && productId != null) {
            return "상품을 찾을 수 없습니다. 상품 ID: " + productId;
        } else if (productId != null) {
            return "해당 조건의 상품을 찾을 수 없습니다: " + productId;
        } else {
            return "상품을 찾을 수 없습니다.";
        }
    }

    // Getter 메서드들

    /**
     * 찾을 수 없는 상품 ID 반환
     * @return 상품 ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * ID로 검색했는지 여부 반환
     * @return ID 검색 여부
     */
    public boolean isSearchedById() {
        return searchedById;
    }

    /**
     * 상세 정보 문자열 반환
     * @return 상세 정보
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductNotFoundException: ");

        if (productId != null) {
            sb.append("[상품 ID: ").append(productId).append("] ");
        }

        if (searchedById) {
            sb.append("[ID 검색] ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}