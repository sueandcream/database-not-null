import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookManager {

    // 1. Product 추가 기능 (INSERT)
    public static void addProduct(Connection conn, Scanner scanner) {
        System.out.println("\n=== Register New Product ===");
        
        System.out.print("Product ID (Integer): ");
        int product_id = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Product Title: ");
        String product_name = scanner.nextLine();
        
        System.out.print("Author Name: ");
        String author = scanner.nextLine();
        
        System.out.print("Category ID (Integer): ");
        int category_id = scanner.nextInt();
        
        System.out.print("Publisher ID (Integer): ");
        int publisher_id = scanner.nextInt();
        
        System.out.print("Unit Price: ");
        double unit_price = scanner.nextDouble();
        scanner.nextLine();

        // [REQ10] PreparedStatement 사용하기 위해
        String sql = "INSERT INTO product (product_id, product_name, author, category_id, publisher_id, unit_price) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ? 자리 mapping
            pstmt.setInt(1, product_id);
            pstmt.setString(2, product_name);
            pstmt.setString(3, author);
            pstmt.setInt(4, category_id);
            pstmt.setInt(5, publisher_id);
            pstmt.setDouble(6, unit_price);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println(">> Product successfully registered!");
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to register product: " + e.getMessage());
        }
    }

    // 2. Product 가격(unit price) 수정 기능 (UPDATE)
    public static void updateProductPrice(Connection conn, Scanner scanner) {
        System.out.println("\n=== Update Product Unit Price ===");
        
        System.out.print("Product ID to update: ");
        int product_id = scanner.nextInt();
        
        System.out.print("New Unit Price: ");
        double new_price = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE product SET unit_price = ? WHERE product_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, new_price);
            pstmt.setInt(2, product_id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println(">> Product price successfully updated!");
            } else {
                System.out.println(">> [WARNING] No product found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to update product price: " + e.getMessage());
        }
    }

    // 3. Delete Existing Product (DELETE)
    public static void deleteProduct(Connection conn, Scanner scanner) {
        System.out.println("\n=== Delete Product from Catalog ===");
        
        System.out.print("Enter Product ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM product WHERE product_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println(">> Product successfully removed from the database!");
            } else {
                System.out.println(">> [WARNING] No product found with the given ID. Delete aborted.");
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to delete product: " + e.getMessage());
            System.out.println(">> Note: If this product is linked to a sales transaction history, it cannot be deleted due to FK constraints.");
        }
    }
}
