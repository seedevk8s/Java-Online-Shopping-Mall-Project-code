package javaproject.exception;

/**
 * 재고 부족 예외 클래스
 * 상품의 재고가 요청 수량보다 적을 때 발생
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class InsufficientStockException extends Exception {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    /**
     * 기본 생성자
     */
    public InsufficientStockException() {
        super("재고가 부족합니다.");
    }

    /**
     * 메시지를 포함한 생성자
     *
     * @param message 예외 메시지
     */
    public InsufficientStockException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 생성자
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인을 포함한 생성자
     *
     * @param cause 예외 원인
     */
    public InsufficientStockException(Throwable cause) {
        super(cause);
    }

    /**
     * 상품명과 재고 정보를 포함한 상세 메시지 생성
     *
     * @param productName 상품명
     * @param available 사용 가능한 재고
     * @param requested 요청된 수량
     * @return 상세 예외 메시지
     */
    public static InsufficientStockException withDetails(String productName, int available, int requested) {
        return new InsufficientStockException(
                String.format("'%s' 상품의 재고가 부족합니다. (재고: %d개, 요청: %d개)",
                        productName, available, requested)
        );
    }

    /**
     * 상품 ID와 재고 정보를 포함한 메시지 생성
     *
     * @param productId 상품 ID
     * @param available 사용 가능한 재고
     * @param requested 요청된 수량
     * @return 상세 예외 메시지
     */
    public static InsufficientStockException withProductId(String productId, int available, int requested) {
        return new InsufficientStockException(
                String.format("상품(ID: %s)의 재고가 부족합니다. (재고: %d개, 요청: %d개)",
                        productId, available, requested)
        );
    }

    /**
     * 품절 상태 메시지 생성
     *
     * @param productName 상품명
     * @return 품절 예외
     */
    public static InsufficientStockException outOfStock(String productName) {
        return new InsufficientStockException(
                String.format("'%s' 상품은 현재 품절 상태입니다.", productName)
        );
    }

    /**
     * 재고 부족분 정보를 포함한 메시지 생성
     *
     * @param productName 상품명
     * @param shortage 부족한 수량
     * @return 재고 부족 예외
     */
    public static InsufficientStockException withShortage(String productName, int shortage) {
        return new InsufficientStockException(
                String.format("'%s' 상품의 재고가 %d개 부족합니다.", productName, shortage)
        );
    }

    /**
     * 구매 가능 수량 안내 메시지 생성
     *
     * @param productName 상품명
     * @param available 구매 가능한 수량
     * @return 구매 가능 수량 안내 예외
     */
    public static InsufficientStockException withAvailable(String productName, int available) {
        if (available == 0) {
            return outOfStock(productName);
        }
        return new InsufficientStockException(
                String.format("'%s' 상품은 최대 %d개까지 구매 가능합니다.", productName, available)
        );
    }
}