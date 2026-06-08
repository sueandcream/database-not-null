import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookManager {

    // 1. Book 추가 기능 (INSERT)
    public static void addBook(Connection conn, Scanner scanner) {
        System.out.println("\n=== Register New Book ===");
        
        System.out.print("Book ID (Integer): ");
        int book_id = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Book Title: ");
        String title = scanner.nextLine();
        
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
        String sql = "INSERT INTO book (book_id, title, author, category_id, publisher_id, unit_price) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ? 자리 mapping
            pstmt.setInt(1, book_id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setInt(4, category_id);
            pstmt.setInt(5, publisher_id);
            pstmt.setDouble(6, unit_price);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println(">> Book successfully registered!");
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to register book: " + e.getMessage());
        }
    }

 // 2. Book 가격(unit price) 수정 기능 (UPDATE with Transaction)
    public static void updateBookPrice(Connection conn, Scanner scanner) {
        System.out.println("\n=== Update Book Unit Price ===");

        System.out.print("Book ID to update: ");
        int book_id = scanner.nextInt();

        System.out.print("New Unit Price: ");
        double new_price = scanner.nextDouble();
        scanner.nextLine();

        String selectSql = "SELECT title, unit_price FROM book WHERE book_id = ?";
        String insertHistorySql =
                "INSERT INTO book_price_history (book_id, old_price, new_price, changed_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        String updateSql = "UPDATE book SET unit_price = ? WHERE book_id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtSelect = conn.prepareStatement(selectSql)) {
                pstmtSelect.setInt(1, book_id);
                ResultSet rs = pstmtSelect.executeQuery();

                if (!rs.next()) {
                    System.out.println(">> [WARNING] No book found with the given ID.");
                    conn.rollback();
                    return;
                }

                String title = rs.getString("title");
                double old_price = rs.getDouble("unit_price");

                try (PreparedStatement pstmtHistory = conn.prepareStatement(insertHistorySql);
                     PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql)) {

                    pstmtHistory.setInt(1, book_id);
                    pstmtHistory.setDouble(2, old_price);
                    pstmtHistory.setDouble(3, new_price);
                    pstmtHistory.executeUpdate();

                    pstmtUpdate.setDouble(1, new_price);
                    pstmtUpdate.setInt(2, book_id);
                    int rows = pstmtUpdate.executeUpdate();

                    if (rows > 0) {
                        conn.commit();
                        System.out.println(">> Book price successfully updated!");
                        System.out.println(">> Title: " + title);
                        System.out.println(">> Old Price: " + old_price);
                        System.out.println(">> New Price: " + new_price);
                    } else {
                        conn.rollback();
                        System.out.println(">> [WARNING] Book update failed.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println(">> SQL execution error. Transaction rolled back.");
                throw e;
            }

        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to update book price: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 3. Delete Existing Book (DELETE)
    public static void deleteBook(Connection conn, Scanner scanner) {
        System.out.println("\n=== Delete Book from Catalog ===");
        
        System.out.print("Enter Book ID to delete: ");
        int book_id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM book WHERE book_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, book_id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println(">> Book successfully removed from the database!");
            } else {
                System.out.println(">> [WARNING] No book found with the given ID. Delete aborted.");
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to delete book: " + e.getMessage());
            System.out.println(">> Note: If this book is linked to a sales transaction history, it cannot be deleted due to FK constraints.");
        }
    }
}
