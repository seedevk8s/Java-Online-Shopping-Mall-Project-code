package javaproject.domain;

/**
 * 장바구니의 개별 아이템을 나타내는 클래스
 * 상품과 수량 정보를 포함
 */
public class CartItem {
    // 상품 ID
    private String productId;

    // 상품 객체 (상품 정보 참조용)
    private Product product;

    // 장바구니에 담은 수량
    private int quantity;

    /**
     * 기본 생성자
     */
    public CartItem() {
    }

    /**
     * 장바구니 아이템 생성자
     * @param product 상품 객체
     * @param quantity 수량
     */
    public CartItem(Product product, int quantity) {
        this.productId = product.getProductId();
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * 파일 저장용 생성자 (Product 객체 없이)
     */
    public CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getter/Setter 메서드들
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product.getProductId();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 수량 증가
     * @param amount 증가할 수량
     */
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * 수량 감소
     * @param amount 감소할 수량
     * @return 감소 후 수량 (0 이하가 되면 0으로 설정)
     */
    public int decreaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity = Math.max(0, this.quantity - amount);
        }
        return this.quantity;
    }

    /**
     * 해당 아이템의 총 가격 계산
     * @return 상품 가격 × 수량
     */
    public int getTotalPrice() {
        if (product != null) {
            return product.getPrice() * quantity;
        }
        return 0;
    }

    /**
     * 총 가격을 원화 형식으로 반환
     */
    public String getFormattedTotalPrice() {
        return String.format("%,d원", getTotalPrice());
    }

    /**
     * 파일 저장용 문자열 변환
     */
    public String toFileString() {
        return productId + "|" + quantity;
    }

    /**
     * 파일에서 읽은 문자열로부터 CartItem 객체 생성
     */
    public static CartItem fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        return new CartItem(parts[0], Integer.parseInt(parts[1]));
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
