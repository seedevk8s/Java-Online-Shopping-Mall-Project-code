package javaproject.controller;

import javaproject.domain.Product;
import javaproject.service.ProductService;
import javaproject.util.SessionManager;
import javaproject.util.InputValidator;
import javaproject.exception.*;
import java.util.List;
import java.util.Scanner;

/**
 * 상품 관리 컨트롤러
 * 싱글톤 패턴을 적용하여 하나의 인스턴스만 생성
 * 상품 조회, 검색, 관리 등의 기능 제공
 *
 * @author ShoppingMall Team
 * @version 1.0
 */
public class ProductController {

    // 싱글톤 인스턴스
    private static final ProductController instance = new ProductController();

    // 서비스 레이어 인스턴스
    private final ProductService productService;

    // 유틸리티 인스턴스들
    private final SessionManager sessionManager;
    private final InputValidator inputValidator;

    // 스캐너 객체
    private final Scanner scanner;

    /**
     * private 생성자 - 싱글톤 패턴 구현
     */
    private ProductController() {
        this.productService = ProductService.getInstance();
        this.sessionManager = SessionManager.getInstance();
        this.inputValidator = InputValidator.getInstance();
        this.scanner = new Scanner(System.in);
    }

    /**
     * 싱글톤 인스턴스 반환
     * @return ProductController의 유일한 인스턴스
     */
    public static ProductController getInstance() {
        return instance;
    }

    /**
     * 상품 둘러보기
     */
    public void browseProducts() {
        while (true) {
            System.out.println("\n┌────────────────────────────────────┐");
            System.out.println("│         🛍️ 상품 둘러보기           │");
            System.out.println("├────────────────────────────────────┤");
            System.out.println("│  1. 전체 상품 보기                 │");
            System.out.println("│  2. 카테고리별 보기                │");
            System.out.println("│  3. 가격대별 보기                  │");
            System.out.println("│  4. 베스트셀러                     │");
            System.out.println("│  5. 신상품                         │");
            System.out.println("│  6. 상품 상세보기                  │");
            System.out.println("│  7. 이전 메뉴로                    │");
            System.out.println("└────────────────────────────────────┘");

            System.out.print("메뉴를 선택하세요: ");
            String input = scanner.nextLine();

            try {
                int choice = inputValidator.validateMenuChoice(input, 1, 7);

                switch (choice) {
                    case 1:
                        viewAllProducts();
                        break;
                    case 2:
                        viewProductsByCategory();
                        break;
                    case 3:
                        viewProductsByPriceRange();
                        break;
                    case 4:
                        viewBestSellers();
                        break;
                    case 5:
                        viewNewProducts();
                        break;
                    case 6:
                        viewProductDetail();
                        break;
                    case 7:
                        return;
                }
            } catch (InvalidInputException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /**
     * 전체 상품 보기
     */
    private void viewAllProducts() {
        List<Product> products = productService.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("\n📭 등록된 상품이 없습니다.");
            return;
        }

        System.out.println("\n=== 전체 상품 목록 ===");
        System.out.println("총 " + products.size() + "개 상품");
        System.out.println("-".repeat(80));

        displayProductList(products);
    }

    /**
     * 카테고리별 상품 보기
     */
    private void viewProductsByCategory() {
        List<String> categories = productService.getCategories();

        System.out.println("\n=== 카테고리 선택 ===");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }

        System.out.print("\n카테고리 번호를 선택하세요: ");
        String input = scanner.nextLine();

        try {
            int choice = inputValidator.validateMenuChoice(input, 1, categories.size());
            String selectedCategory = categories.get(choice - 1);

            List<Product> products = productService.getProductsByCategory(selectedCategory);

            if (products.isEmpty()) {
                System.out.println("\n📭 해당 카테고리에 상품이 없습니다.");
                return;
            }

            System.out.println("\n=== " + selectedCategory + " 카테고리 상품 ===");
            System.out.println("총 " + products.size() + "개 상품");
            System.out.println("-".repeat(80));

            displayProductList(products);

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /**
     * 가격대별 상품 보기
     */
    private void viewProductsByPriceRange() {
        System.out.println("\n=== 가격대 설정 ===");

        try {
            System.out.print("최소 가격: ");
            String minStr = scanner.nextLine();
            double minPrice = inputValidator.validatePrice(minStr);

            System.out.print("최대 가격: ");
            String maxStr = scanner.nextLine();
            double maxPrice = inputValidator.validatePrice(maxStr);

            if (minPrice > maxPrice) {
                System.out.println("❌ 최소 가격이 최대 가격보다 클 수 없습니다.");
                return;
            }

            List<Product> products = productService.searchProductsByPriceRange(minPrice, maxPrice);

            if (products.isEmpty()) {
                System.out.println("\n📭 해당 가격대에 상품이 없습니다.");
                return;
            }

            System.out.printf("\n=== %,.0f원 ~ %,.0f원 상품 ===\n", minPrice, maxPrice);
            System.out.println("총 " + products.size() + "개 상품");
            System.out.println("-".repeat(80));

            displayProductList(products);

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /**
     * 베스트셀러 보기
     */
    private void viewBestSellers() {
        List<Product> products = productService.getBestSellers(10);

        if (products.isEmpty()) {
            System.out.println("\n📭 베스트셀러 상품이 없습니다.");
            return;
        }

        System.out.println("\n=== 🏆 베스트셀러 TOP 10 ===");
        System.out.println("-".repeat(80));

        int rank = 1;
        for (Product product : products) {
            System.out.printf("%2d위. %-30s | %,d원 | 재고: %d개\n",
                    rank++, product.getName(), (int)product.getPrice(), product.getStock());
        }
    }

    /**
     * 신상품 보기
     */
    private void viewNewProducts() {
        List<Product> products = productService.getNewProducts(10);

        if (products.isEmpty()) {
            System.out.println("\n📭 신상품이 없습니다.");
            return;
        }

        System.out.println("\n=== 🆕 신상품 ===");
        System.out.println("-".repeat(80));

        displayProductList(products);
    }

    /**
     * 상품 상세보기
     */
    private void viewProductDetail() {
        System.out.print("\n상품 ID를 입력하세요: ");
        String productId = scanner.nextLine().trim();

        Product product = productService.getProductById(productId);

        if (product == null) {
            System.out.println("❌ 상품을 찾을 수 없습니다.");
            return;
        }

        System.out.println("\n" + product.toDetailString());

        // 로그인한 경우 장바구니 추가 옵션 제공
        if (sessionManager.isLoggedIn()) {
            System.out.print("\n장바구니에 추가하시겠습니까? (Y/N): ");
            String answer = scanner.nextLine();

            try {
                if (inputValidator.validateYesNo(answer)) {
                    // OrderController의 addToCart 메서드 호출
                    System.out.println("장바구니 기능은 주문 메뉴에서 이용해주세요.");
                }
            } catch (InvalidInputException e) {
                // 무시
            }
        }
    }

    /**
     * 상품 검색
     */
    public void searchProducts() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         🔍 상품 검색                │");
        System.out.println("└────────────────────────────────────┘");

        System.out.print("검색어를 입력하세요: ");
        String keyword = scanner.nextLine();

        try {
            keyword = inputValidator.validateSearchKeyword(keyword);
            List<Product> products = productService.searchProductsByName(keyword);

            if (products.isEmpty()) {
                System.out.println("\n📭 검색 결과가 없습니다.");
                return;
            }

            System.out.println("\n=== 검색 결과 ===");
            System.out.println("'" + keyword + "' 검색 결과: " + products.size() + "개");
            System.out.println("-".repeat(80));

            displayProductList(products);

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /**
     * 상품 목록 출력 헬퍼 메서드
     */
    private void displayProductList(List<Product> products) {
        System.out.printf("%-10s %-30s %-15s %-10s %-10s\n",
                "상품ID", "상품명", "가격", "재고", "카테고리");
        System.out.println("-".repeat(80));

        for (Product product : products) {
            System.out.printf("%-10s %-30s %,12d원 %8d개 %-10s\n",
                    product.getId(),
                    product.getName().length() > 30 ?
                            product.getName().substring(0, 27) + "..." : product.getName(),
                    (int)product.getPrice(),
                    product.getStock(),
                    product.getCategory() != null ? product.getCategory() : "미분류");
        }
    }

    // ===== 관리자 기능 =====

    /**
     * 상품 추가 (관리자용)
     */
    public void addProduct() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        System.out.println("\n=== 상품 등록 ===");

        try {
            // 상품명 입력
            System.out.print("상품명: ");
            String name = scanner.nextLine();
            name = inputValidator.validateProductName(name);

            // 상품 설명 입력
            System.out.print("상품 설명: ");
            String description = scanner.nextLine();

            // 가격 입력
            System.out.print("가격: ");
            String priceStr = scanner.nextLine();
            double price = inputValidator.validatePrice(priceStr);

            // 재고 입력
            System.out.print("재고 수량: ");
            String stockStr = scanner.nextLine();
            int stock = inputValidator.validateQuantity(stockStr);

            // 카테고리 선택
            List<String> categories = productService.getCategories();
            System.out.println("\n카테고리 선택:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }
            System.out.print("카테고리 번호: ");
            String catChoice = scanner.nextLine();
            int categoryIndex = inputValidator.validateMenuChoice(catChoice, 1, categories.size());
            String category = categories.get(categoryIndex - 1);

            // 상품 생성 및 등록
            Product product = new Product(name, description, price, stock, category);
            productService.addProduct(product);

            System.out.println("\n✅ 상품이 등록되었습니다.");
            System.out.println("상품 ID: " + product.getId());

        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (DuplicateProductException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 상품 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 상품 수정 (관리자용)
     */
    public void updateProduct() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        System.out.print("\n수정할 상품 ID: ");
        String productId = scanner.nextLine().trim();

        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("❌ 상품을 찾을 수 없습니다.");
            return;
        }

        System.out.println("\n=== 상품 수정 ===");
        System.out.println("(변경하지 않을 항목은 Enter를 누르세요)");

        try {
            // 상품명 수정
            System.out.print("상품명 [" + product.getName() + "]: ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                name = inputValidator.validateProductName(name);
                product.setName(name);
            }

            // 설명 수정
            System.out.print("설명 [" + product.getDescription() + "]: ");
            String description = scanner.nextLine();
            if (!description.trim().isEmpty()) {
                product.setDescription(description);
            }

            // 가격 수정
            System.out.print("가격 [" + (int)product.getPrice() + "]: ");
            String priceStr = scanner.nextLine();
            if (!priceStr.trim().isEmpty()) {
                double price = inputValidator.validatePrice(priceStr);
                product.setPrice(price);
            }

            // 재고 수정
            System.out.print("재고 [" + product.getStock() + "]: ");
            String stockStr = scanner.nextLine();
            if (!stockStr.trim().isEmpty()) {
                int stock = inputValidator.validateQuantity(stockStr);
                product.setStock(stock);
            }

            // 변경사항 저장
            productService.updateProduct(productId, product);
            System.out.println("✅ 상품이 수정되었습니다.");

        } catch (Exception e) {
            System.out.println("❌ 상품 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 상품 삭제 (관리자용)
     */
    public void deleteProduct() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        System.out.print("\n삭제할 상품 ID: ");
        String productId = scanner.nextLine().trim();

        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("❌ 상품을 찾을 수 없습니다.");
            return;
        }

        System.out.println("\n상품명: " + product.getName());
        System.out.print("정말 삭제하시겠습니까? (Y/N): ");

        try {
            String answer = scanner.nextLine();
            if (inputValidator.validateYesNo(answer)) {
                productService.deleteProduct(productId);
                System.out.println("✅ 상품이 삭제되었습니다.");
            } else {
                System.out.println("삭제를 취소했습니다.");
            }
        } catch (Exception e) {
            System.out.println("❌ 상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 재고 관리 (관리자용)
     */
    public void manageInventory() {
        // 관리자 권한 확인
        if (!sessionManager.isAdmin()) {
            System.out.println("❌ 관리자 권한이 필요합니다.");
            return;
        }

        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         📦 재고 관리                │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. 재고 추가                      │");
        System.out.println("│  2. 재고 부족 상품 보기            │");
        System.out.println("│  3. 품절 상품 보기                 │");
        System.out.println("│  4. 이전 메뉴로                    │");
        System.out.println("└────────────────────────────────────┘");

        System.out.print("메뉴를 선택하세요: ");
        String input = scanner.nextLine();

        try {
            int choice = inputValidator.validateMenuChoice(input, 1, 4);

            switch (choice) {
                case 1:
                    addStock();
                    break;
                case 2:
                    viewLowStockProducts();
                    break;
                case 3:
                    viewOutOfStockProducts();
                    break;
                case 4:
                    return;
            }
        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /**
     * 재고 추가
     */
    private void addStock() {
        System.out.print("\n상품 ID: ");
        String productId = scanner.nextLine().trim();

        Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("❌ 상품을 찾을 수 없습니다.");
            return;
        }

        System.out.println("상품명: " + product.getName());
        System.out.println("현재 재고: " + product.getStock() + "개");

        try {
            System.out.print("추가할 수량: ");
            String quantityStr = scanner.nextLine();
            int quantity = inputValidator.validateQuantity(quantityStr);

            productService.addStock(productId, quantity);
            System.out.println("✅ 재고가 추가되었습니다.");
            System.out.println("새로운 재고: " + (product.getStock() + quantity) + "개");

        } catch (Exception e) {
            System.out.println("❌ 재고 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 재고 부족 상품 보기
     */
    private void viewLowStockProducts() {
        List<Product> products = productService.getLowStockProducts(10);

        if (products.isEmpty()) {
            System.out.println("\n✅ 재고 부족 상품이 없습니다.");
            return;
        }

        System.out.println("\n=== ⚠️ 재고 부족 상품 (10개 이하) ===");
        System.out.println("-".repeat(60));

        for (Product product : products) {
            System.out.printf("%-10s %-30s 재고: %d개\n",
                    product.getId(), product.getName(), product.getStock());
        }
    }

    /**
     * 품절 상품 보기
     */
    private void viewOutOfStockProducts() {
        List<Product> products = productService.getLowStockProducts(0);

        if (products.isEmpty()) {
            System.out.println("\n✅ 품절 상품이 없습니다.");
            return;
        }

        System.out.println("\n=== ❌ 품절 상품 ===");
        System.out.println("-".repeat(60));

        for (Product product : products) {
            if (product.getStock() == 0) {
                System.out.printf("%-10s %-30s\n",
                        product.getId(), product.getName());
            }
        }
    }
}