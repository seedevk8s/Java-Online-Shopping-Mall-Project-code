package javaproject.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 상품 정보를 담는 도메인 클래스
 * 온라인 쇼핑몰의 핵심 상품 데이터
 */
public class Product {
    // 상품 고유 ID (자동 생성)
    private String productId;

    // 상품명
    private String name;

    // 상품 가격
    private int price;

    // 상품 카테고리 (예: 전자제품, 의류, 도서 등)
    private String category;

    // 재고 수량
    private int stock;

    // 상품 설명
    private String description;

    // 상품 등록일시
    private LocalDateTime createdAt;

    // 상품 ID 자동 생성을 위한 카운터 (static 변수)
    private static int productCounter = 1;

    /**
     * 기본 생성자 (파일에서 데이터 읽을 때 사용)
     */
    public Product() {
    }

    /**
     * 새 상품 등록용 생성자 (ID 자동 생성)
     * @param name 상품명
     * @param price 가격
     * @param category 카테고리
     * @param stock 재고
     * @param description 설명
     */
    public Product(String name, int price, String category, int stock, String description) {
        this.productId = generateProductId(); // 자동으로 ID 생성
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 전체 정보 포함 생성자 (파일에서 읽을 때 사용)
     */
    public Product(String productId, String name, int price, String category,
                   int stock, String description, LocalDateTime createdAt) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.description = description;
        this.createdAt = createdAt;

        // 기존 상품 ID에서 숫자 부분을 추출하여 카운터 업데이트
        updateProductCounter(productId);
    }

    /**
     * 상품 ID 자동 생성 메서드
     * 형식: P001, P002, P003...
     */
    private String generateProductId() {
        return String.format("P%03d", productCounter++);
    }

    /**
     * 기존 상품 ID로부터 카운터 업데이트
     * 파일에서 데이터를 읽을 때 ID 중복을 방지하기 위함
     */
    private void updateProductCounter(String productId) {
        if (productId != null && productId.startsWith("P")) {
            try {
                int idNumber = Integer.parseInt(productId.substring(1)); // P001 -> 001 -> 1
                if (idNumber >= productCounter) {
                    productCounter = idNumber + 1; // 다음 ID를 위해 1 증가
                }
            } catch (NumberFormatException e) {
                // ID 형식이 잘못된 경우 무시
            }
        }
    }

    // Getter 메서드들
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter 메서드들
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 재고 차감 메서드
     * @param quantity 차감할 수량
     * @return 성공 여부
     */
    public boolean decreaseStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            return true;
        }
        return false; // 재고 부족
    }

    /**
     * 재고 증가 메서드 (반품, 입고 시 사용)
     * @param quantity 증가할 수량
     */
    public void increaseStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
        }
    }

    /**
     * 재고 확인 메서드
     * @param quantity 필요한 수량
     * @return 재고 충분 여부
     */
    public boolean hasEnoughStock(int quantity) {
        return stock >= quantity;
    }

    /**
     * 가격을 원화 형식으로 반환
     * @return 1,000원 형식의 문자열
     */
    public String getFormattedPrice() {
        return String.format("%,d원", price);
    }

    /**
     * 파일 저장용 문자열 변환
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return productId + "|" + name + "|" + price + "|" + category + "|" +
                stock + "|" + description + "|" + createdAt.format(formatter);
    }

    /**
     * 파일에서 읽은 문자열로부터 Product 객체 생성
     */
    public static Product fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse(parts[6], formatter);

        return new Product(
                parts[0], // productId
                parts[1], // name
                Integer.parseInt(parts[2]), // price
                parts[3], // category
                Integer.parseInt(parts[4]), // stock
                parts[5], // description
                createdAt
        );
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Product product = (Product) obj;
        return productId != null ? productId.equals(product.productId) : product.productId == null;
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }
}

