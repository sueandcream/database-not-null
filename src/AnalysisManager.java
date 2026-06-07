import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AnalysisManager {

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