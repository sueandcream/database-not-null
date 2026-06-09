import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AnalysisManager {

    // REQ-07: View Bestselling Books by Category (SELECT + VIEW)
    public static void viewBestsellingBooksByCategory(Connection conn, Scanner scanner) {
        System.out.println("\n=== View Bestselling Books by Category ===");
        System.out.print("Enter category name. Press Enter to view all categories: ");
        String categoryName = scanner.nextLine().trim();

        String sql = """
            SELECT
                c.category_name,
                v.book_id,
                b.title,
                b.author,
                p.publisher_name,
                b.unit_price AS current_price,
                v.total_quantity_sold,
                v.total_revenue
            FROM book_sales_summary_view v
            JOIN book b
                ON v.book_id = b.book_id
            JOIN category c
                ON b.category_id = c.category_id
            JOIN publisher p
                ON b.publisher_id = p.publisher_id
            WHERE c.category_name LIKE ?
            ORDER BY
                c.category_name ASC,
                v.total_quantity_sold DESC,
                v.total_revenue DESC
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (categoryName.isBlank()) {
                pstmt.setString(1, "%");
            } else {
                pstmt.setString(1, "%" + categoryName + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.printf(
                    "\n%-15s %-8s %-30s %-20s %-20s %-15s %-10s %-15s%n",
                    "Category",
                    "Book ID",
                    "Title",
                    "Author",
                    "Publisher",
                    "Price",
                    "Qty Sold",
                    "Revenue"
                );
                System.out.println("-".repeat(140));

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.printf(
                        "%-15s %-8d %-30s %-20s %-20s $%-14.2f %-10d $%-14.2f%n",
                        rs.getString("category_name"),
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher_name"),
                        rs.getDouble("current_price"),
                        rs.getInt("total_quantity_sold"),
                        rs.getDouble("total_revenue")
                    );
                }

                if (!found) {
                    System.out.println(">> No bestselling book data found for the given category.");
                }
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to view bestselling books: " + e.getMessage());
        }
    }

    // REQ-08: Compare Book Sales Before and After Price Change (JOIN + VIEW + REQ13)
    public static void compareBookSalesBeforeAfterPriceChange(Connection conn, Scanner scanner) {
        System.out.print("Enter book title keyword: ");
        String titleKeyword = scanner.nextLine();

        String sql = """
        SELECT
            v.title,
            h.changed_at AS price_changed_at,
            h.old_price,
            h.new_price,
            CASE
                WHEN v.transaction_timestamp < h.changed_at THEN 'Before Price Change'
                ELSE 'After Price Change'
            END AS period,
            SUM(v.quantity) AS total_quantity_sold,
            SUM(v.subtotal) AS total_sales_amount
        FROM order_summary_view v
        JOIN book_price_history h
            ON v.title = (
                SELECT b.title
                FROM book b
                WHERE b.book_id = h.book_id
            )
        WHERE v.title LIKE ?
        GROUP BY
            v.title,
            h.changed_at,
            h.old_price,
            h.new_price,
            CASE
                WHEN v.transaction_timestamp < h.changed_at THEN 'Before Price Change'
                ELSE 'After Price Change'
            END
        ORDER BY v.title, h.changed_at, period
    """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + titleKeyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n=== Book Sales Before/After Price Change ===");
                System.out.printf("%-25s %-20s %-10s %-10s %-25s %-10s %-15s%n",
                        "Title", "Changed At", "Old Price", "New Price",
                        "Period", "Qty", "Sales Amount");

                while (rs.next()) {
                    System.out.printf("%-25s %-20s %-10.2f %-10.2f %-25s %-10d %-15.2f%n",
                            rs.getString("title"),
                            rs.getTimestamp("price_changed_at"),
                            rs.getBigDecimal("old_price"),
                            rs.getBigDecimal("new_price"),
                            rs.getString("period"),
                            rs.getInt("total_quantity_sold"),
                            rs.getBigDecimal("total_sales_amount"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error comparing book sales: " + e.getMessage());
        }
    }

    // REQ14: 고객 인구통계 정보(나이대) 기반 판매 분석
    // 사용자가 나이를 입력하면 해당 연령대의 판매량과 추천 도서를 조회
    public static void viewTotalBookSalesByAgeGroup(Connection conn, Scanner scanner) {
        System.out.println("===== Age Group Sales Analysis =====");
        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        String ageGroup = getAgeGroup(age);

        System.out.println();
        System.out.println("Age Group: " + ageGroup);

        printSalesBeforeAfterChange(conn, ageGroup);
        printTopRecommendedBooks(conn, ageGroup);
    }

    private static String getAgeGroup(int age) {
        if (age < 20) {
            return "Under 20";
        } else if (age <= 29) {
            return "20s";
        } else if (age <= 39) {
            return "30s";
        } else {
            return "40+";
        }
    }

    // REQ14
    // 고객 정보 변경(customer_history)을 기준으로
    // 변경 전후 판매 수량을 비교 분석
    private static void printSalesBeforeAfterChange(Connection conn, String ageGroup) {
        String sql = """
        SELECT
            CASE
                WHEN s.transaction_timestamp < ch.changed_at THEN 'Before Change'
                ELSE 'After Change'
            END AS change_period,
            SUM(mb.quantity) AS total_books_sold,
            SUM(mb.quantity * mb.price_at_purchase) AS total_sales_amount
        FROM customer c
        JOIN customer_history ch ON c.customer_id = ch.customer_id
        JOIN sales s ON c.customer_id = s.customer_id
        JOIN total_sales ts ON s.market_basket_id = ts.market_basket_id
        JOIN market_basket mb ON ts.market_basket_id = mb.market_basket_id
        WHERE
            CASE
                WHEN TIMESTAMPDIFF(YEAR, c.birth_date, s.transaction_timestamp) < 20 THEN 'Under 20'
                WHEN TIMESTAMPDIFF(YEAR, c.birth_date, s.transaction_timestamp) BETWEEN 20 AND 29 THEN '20s'
                WHEN TIMESTAMPDIFF(YEAR, c.birth_date, s.transaction_timestamp) BETWEEN 30 AND 39 THEN '30s'
                ELSE '40+'
            END = ?
        GROUP BY change_period
        """;

        int beforeSales = 0;
        int afterSales = 0;

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, ageGroup);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String period = rs.getString("change_period");
                int total = rs.getInt("total_books_sold");

                if (period.equals("Before Change")) {
                    beforeSales = total;
                } else if (period.equals("After Change")) {
                    afterSales = total;
                }
            }

            System.out.println();
            System.out.println("[Sales Before Demographic Change]");
            System.out.println(beforeSales + " books sold");

            System.out.println();
            System.out.println("[Sales After Demographic Change]");
            System.out.println(afterSales + " books sold");

        } catch (Exception e) {
            System.out.println("Failed to analyze age group sales.");
            e.printStackTrace();
        }
    }

    // REQ14 + GROUP BY
    // 동일 연령대 고객들이 가장 많이 구매한 도서 TOP 3 추천
    private static void printTopRecommendedBooks(Connection conn, String ageGroup){
        String sql = """
                SELECT
                    b.title,
                    SUM(mb.quantity) AS total_sold
                FROM customer c
                JOIN sales s ON c.customer_id = s.customer_id
                JOIN total_sales ts ON s.market_basket_id = ts.market_basket_id
                JOIN market_basket mb ON ts.market_basket_id = mb.market_basket_id
                JOIN book b ON mb.book_id = b.book_id
                WHERE
                    CASE
                        WHEN TIMESTAMPDIFF(YEAR, c.birth_date, s.transaction_timestamp) < 20 THEN 'Under 20'
                        WHEN TIMESTAMPDIFF(YEAR, c.birth_date, s.transaction_timestamp) BETWEEN 20 AND 29 THEN '20s'
                        WHEN TIMESTAMPDIFF(YEAR, c.birth_date, s.transaction_timestamp) BETWEEN 30 AND 39 THEN '30s'
                        ELSE '40+'
                    END = ?
                GROUP BY b.book_id, b.title
                ORDER BY total_sold DESC
                LIMIT 3
                """;

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, ageGroup);

            ResultSet rs = pstmt.executeQuery();

            System.out.println();
            System.out.println("[Top 3 Recommended Books]");

            int rank = 1;
            while (rs.next()) {
                String title = rs.getString("title");
                int totalSold = rs.getInt("total_sold");

                System.out.println(rank + ". " + title + " (" + totalSold + ")");
                rank++;
            }

            if (rank == 1) {
                System.out.println("No recommended books found.");
            }

        } catch (Exception e) {
            System.out.println("Failed to recommend books.");
            e.printStackTrace();
        }
    }


    // [GROUP BY] Sales Statistics by Category
    // Displays aggregated sales metrics for all categories to analyze performance
    public static void viewSalesStatisticsByCategory(Connection conn, Scanner scanner) {
        System.out.println("\n=== View Book Sales Statistics by Category ===");
        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();

        if (categoryName.isBlank()) {
            System.out.println(">> [WARNING] Category name cannot be empty. Search aborted.");
            return;
        }

        String sql = """
                SELECT 
                    category_name, 
                    COUNT(DISTINCT book_id) AS unique_books_count, 
                    SUM(total_quantity_sold) AS total_qty_sold, 
                    SUM(total_revenue) AS category_total_revenue 
                FROM book_sales_summary_view
                WHERE category_name LIKE ?
                GROUP BY category_name 
                ORDER BY category_total_revenue DESC
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + categoryName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.printf(
                        "\n%-20s %-20s %-20s %-20s%n", 
                        "Category Name", "Books Registered", "Total Qty Sold", "Total Revenue"
                );
                System.out.println("-".repeat(85));

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf(
                            "%-20s %-20d %-20d $%-14.2f%n", 
                            rs.getString("category_name"), 
                            rs.getInt("unique_books_count"), 
                            rs.getInt("total_qty_sold"), 
                            rs.getDouble("category_total_revenue")
                    );
                }

                if (!found) {
                    System.out.println(">> No sales statistics available yet.");
                }
                System.out.println("-".repeat(85));
            }
        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to retrieve sales statistics: " + e.getMessage());
        }
    }
}
