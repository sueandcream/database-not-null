import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AnalysisManager {

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
}
