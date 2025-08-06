package javaproject.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 상품 도메인 클래스
 * 쇼핑몰의 상품 정보를 표현
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class Product implements Serializable {

    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 상품 ID (고유 식별자)
    private String id;

    // 상품명
    private String name;

    // 상품 설명
    private String description;

    // 가격
    private double price;

    // 재고 수량
    private int stock;

    // 카테고리
    private String category;

    // 등록일시
    private LocalDateTime createdDate;

    // 수정일시
    private LocalDateTime modifiedDate;

    // 판매 가능 여부
    private boolean available;

    // 이미지 URL (선택사항)
    private String imageUrl;

    // 제조사 (선택사항)
    private String manufacturer;

    /**
     * 기본 생성자
     * 자동으로 ID와 날짜 생성
     */
    public Product() {
        this.id = generateId();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.available = true;
    }

    /**
     * 필수 정보를 포함한 생성자
     *
     * @param name 상품명
     * @param description 상품 설명
     * @param price 가격
     * @param stock 재고 수량
     * @param category 카테고리
     */
    public Product(String name, String description, double price, int stock, String category) {
        this();  // 기본 생성자 호출 (ID와 날짜 설정)
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    /**
     * 전체 정보를 포함한 생성자
     *
     * @param id 상품 ID
     * @param name 상품명
     * @param description 상품 설명
     * @param price 가격
     * @param stock 재고 수량
     * @param category 카테고리
     */
    public Product(String id, String name, String description, double price,
                   int stock, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.available = true;
    }

    /**
     * 고유 ID 생성 메서드
     *
     * @return 생성된 고유 ID
     */
    private String generateId() {
        return "PRD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 재고 증가
     *
     * @param quantity 증가시킬 수량
     */
    public void addStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("증가 수량은 0 이상이어야 합니다.");
        }
        this.stock += quantity;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 재고 감소
     *
     * @param quantity 감소시킬 수량
     * @return 재고 감소 성공 여부
     */
    public boolean reduceStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("감소 수량은 0 이상이어야 합니다.");
        }

        if (this.stock >= quantity) {
            this.stock -= quantity;
            this.modifiedDate = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * 재고 확인
     *
     * @param requiredQuantity 필요한 수량
     * @return 재고가 충분하면 true
     */
    public boolean hasStock(int requiredQuantity) {
        return this.stock >= requiredQuantity;
    }

    /**
     * 판매 가능 여부 확인
     *
     * @return 판매 가능하면 true
     */
    public boolean isAvailable() {
        return available && stock > 0;
    }

    /**
     * 가격 할인 적용
     *
     * @param discountPercent 할인율 (0~100)
     * @return 할인된 가격
     */
    public double getDiscountedPrice(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("할인율은 0~100 사이여야 합니다.");
        }
        return price * (1 - discountPercent / 100);
    }

    // Getters and Setters

    /**
     * 상품 ID 반환
     * @return 상품 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 상품 ID 설정
     * @param id 상품 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 상품명 반환
     * @return 상품명
     */
    public String getName() {
        return name;
    }

    /**
     * 상품명 설정
     * @param name 상품명
     */
    public void setName(String name) {
        this.name = name;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 상품 설명 반환
     * @return 상품 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 상품 설명 설정
     * @param description 상품 설명
     */
    public void setDescription(String description) {
        this.description = description;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 가격 반환
     * @return 가격
     */
    public double getPrice() {
        return price;
    }

    /**
     * 가격 설정
     * @param price 가격
     */
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        this.price = price;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 재고 수량 반환
     * @return 재고 수량
     */
    public int getStock() {
        return stock;
    }

    /**
     * 재고 수량 설정
     * @param stock 재고 수량
     */
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
        this.stock = stock;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 카테고리 반환
     * @return 카테고리
     */
    public String getCategory() {
        return category;
    }

    /**
     * 카테고리 설정
     * @param category 카테고리
     */
    public void setCategory(String category) {
        this.category = category;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 등록일시 반환
     * @return 등록일시
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * 등록일시 설정
     * @param createdDate 등록일시
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 수정일시 반환
     * @return 수정일시
     */
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    /**
     * 수정일시 설정
     * @param modifiedDate 수정일시
     */
    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * 판매 가능 여부 설정
     * @param available 판매 가능 여부
     */
    public void setAvailable(boolean available) {
        this.available = available;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 이미지 URL 반환
     * @return 이미지 URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 이미지 URL 설정
     * @param imageUrl 이미지 URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 제조사 반환
     * @return 제조사
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * 제조사 설정
     * @param manufacturer 제조사
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * 상품 정보를 문자열로 변환
     *
     * @return 상품 정보 문자열
     */
    @Override
    public String toString() {
        return String.format(
                "Product{id='%s', name='%s', price=%.2f, stock=%d, category='%s', available=%s}",
                id, name, price, stock, category, available
        );
    }

    /**
     * 상품 정보를 상세하게 출력하는 메서드
     *
     * @return 상세 정보 문자열
     */
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 상품 상세 정보 ===\n");
        sb.append("상품 ID: ").append(id).append("\n");
        sb.append("상품명: ").append(name).append("\n");
        sb.append("설명: ").append(description).append("\n");
        sb.append("가격: ").append(String.format("%,.0f원", price)).append("\n");
        sb.append("재고: ").append(stock).append("개\n");
        sb.append("카테고리: ").append(category).append("\n");

        if (manufacturer != null) {
            sb.append("제조사: ").append(manufacturer).append("\n");
        }

        sb.append("판매 상태: ").append(isAvailable() ? "판매중" : "품절").append("\n");
        sb.append("등록일: ").append(createdDate).append("\n");
        sb.append("수정일: ").append(modifiedDate).append("\n");

        return sb.toString();
    }

    /**
     * 객체 동등성 비교
     * ID를 기준으로 비교
     *
     * @param obj 비교할 객체
     * @return 동일한 상품이면 true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Product product = (Product) obj;
        return Objects.equals(id, product.id);
    }

    /**
     * 해시코드 생성
     * ID를 기준으로 생성
     *
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 상품 복사본 생성
     *
     * @return 복사된 상품 객체
     */
    public Product copy() {
        Product copy = new Product();
        copy.id = this.id;
        copy.name = this.name;
        copy.description = this.description;
        copy.price = this.price;
        copy.stock = this.stock;
        copy.category = this.category;
        copy.createdDate = this.createdDate;
        copy.modifiedDate = this.modifiedDate;
        copy.available = this.available;
        copy.imageUrl = this.imageUrl;
        copy.manufacturer = this.manufacturer;

        return copy;
    }

    /**
     * 상품 유효성 검증
     *
     * @return 유효한 상품이면 true
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                price >= 0 &&
                stock >= 0;
    }
}