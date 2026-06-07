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
        String categoryName = scanner.nextLine();

        String sql = """
                SELECT
                    category_name,
                    book_id,
                    title,
                    author,
                    publisher_name,
                    current_price,
                    total_quantity_sold,
                    total_revenue
                FROM book_sales_summary_view
                WHERE category_name LIKE ?
                ORDER BY category_name ASC, total_quantity_sold DESC, total_revenue DESC
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (categoryName.isBlank()) {
                pstmt.setString(1, "%");
            } else {
                pstmt.setString(1, "%" + categoryName + "%");
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.printf(
                    "\n%-15s %-8s %-30s %-20s %-20s %-15s %-10s %-15s%n",
                    "Category", "Book ID", "Title", "Author", "Publisher",
                    "Price", "Qty Sold", "Revenue"
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
                System.out.println(">> No bestselling book data found.");
            }

        } catch (SQLException e) {
            System.out.println(">> [ERROR] Failed to view bestselling books: " + e.getMessage());
        }
    }

    // REQ-08: Compare Book Sales Before and After Price Change (JOIN + VIEW + REQ13)
    public static void compareBookSalesBeforeAfterPriceChange(Connection conn, Scanner scanner) {
        System.out.println("\n=== Compare Book Sales Before/After Price Change ===");

        System.out.print("Enter book title to analyze: ");
        String title = scanner.nextLine();

        String sql = """
            SELECT
                b.title,
                b.unit_price        AS current_price,
                mb.price_at_purchase AS sold_price,
                SUM(mb.quantity)     AS total_qty_sold,
                SUM(mb.quantity * mb.price_at_purchase) AS total_revenue,
                CASE
                    WHEN mb.price_at_purchase < b.unit_price THEN 'Before Price Increase'
                    WHEN mb.price_at_purchase > b.unit_price THEN 'Before Price Decrease'
                    ELSE 'At Current Price'
                END AS price_period
            FROM book b
            JOIN market_basket mb ON b.book_id = mb.book_id
            WHERE b.title LIKE ?
            GROUP BY b.title, b.unit_price, mb.price_at_purchase
            ORDER BY mb.price_at_purchase
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.printf("\n%-30s %-15s %-15s %-10s %-15s %-25s%n",
                "Title", "Current Price", "Sold Price", "Qty", "Revenue", "Period");
            System.out.println("-".repeat(110));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-30s $%-14.2f $%-14.2f %-10d $%-14.2f %-25s%n",
                    rs.getString("title"),
                    rs.getDouble("current_price"),
                    rs.getDouble("sold_price"),
                    rs.getInt("total_qty_sold"),
                    rs.getDouble("total_revenue"),
                    rs.getString("price_period"));
            }
            if (!found) System.out.println(">> No sales data found for: " + title);

        } catch (SQLException e) {
            System.out.println(">> [ERROR] " + e.getMessage());
        }
    }


    public void viewTotalBookSalesByAgeGroup() {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== Age Group Sales Analysis =====");
        System.out.print("Enter age: ");
        int age = sc.nextInt();

        String ageGroup = getAgeGroup(age);

        System.out.println();
        System.out.println("Age Group: " + ageGroup);

        printSalesBeforeAfterChange(ageGroup);
        printTopRecommendedBooks(ageGroup);
    }

    private String getAgeGroup(int age) {
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

    private void printSalesBeforeAfterChange(String ageGroup) {
        String sql = """
                SELECT
                    CASE
                        WHEN s.transaction_timestamp < ch.changed_at THEN 'Before Change'
                        ELSE 'After Change'
                    END AS change_period,
                    SUM(mb.quantity) AS total_books_sold
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
                Connection conn = DBConnection.getConnection();
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

    private void printTopRecommendedBooks(String ageGroup) {
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
                Connection conn = DBConnection.getConnection();
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
}