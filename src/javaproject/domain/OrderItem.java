package javaproject.domain;

/**
 * 주문 내 개별 상품 정보를 담는 클래스
 * 주문 시점의 상품 정보와 수량을 저장
 */
public class OrderItem {
    // 주문 ID (어떤 주문에 속하는지)
    private String orderId;

    // 상품 ID
    private String productId;

    // 주문 시점의 상품명 (상품 정보 변경에 대비)
    private String productName;

    // 주문 시점의 상품 가격
    private int productPrice;

    // 주문 수량
    private int quantity;

    // 상품 객체 참조 (조회용)
    private Product product;

    /**
     * 기본 생성자
     */
    public OrderItem() {
    }

    /**
     * 상품 객체를 이용한 생성자
     * @param product 상품 객체
     * @param quantity 주문 수량
     */
    public OrderItem(Product product, int quantity) {
        this.productId = product.getProductId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.quantity = quantity;
        this.product = product;
    }

    /**
     * 파일에서 읽기용 생성자
     * @param orderId 주문 ID
     * @param productId 상품 ID
     * @param productName 상품명
     * @param productPrice 상품 가격
     * @param quantity 수량
     */
    public OrderItem(String orderId, String productId, String productName,
                     int productPrice, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    /**
     * 해당 아이템의 총 가격 계산
     * @return 상품 가격 × 수량
     */
    public int getTotalPrice() {
        return productPrice * quantity;
    }

    /**
     * 총 가격을 원화 형식으로 반환
     */
    public String getFormattedTotalPrice() {
        return String.format("%,d원", getTotalPrice());
    }

    /**
     * 단가를 원화 형식으로 반환
     */
    public String getFormattedProductPrice() {
        return String.format("%,d원", productPrice);
    }

    // Getter/Setter 메서드들
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getProductId();
            // 주문 시점 정보는 유지 (가격 변동 대비)
        }
    }

    /**
     * 파일 저장용 문자열 변환
     */
    public String toFileString() {
        return orderId + "|" + productId + "|" + productName + "|" +
                productPrice + "|" + quantity;
    }

    /**
     * 파일에서 읽은 문자열로부터 OrderItem 객체 생성
     */
    public static OrderItem fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        return new OrderItem(
                parts[0], // orderId
                parts[1], // productId
                parts[2], // productName
                Integer.parseInt(parts[3]), // productPrice
                Integer.parseInt(parts[4])  // quantity
        );
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
