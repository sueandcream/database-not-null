import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== Online Bookstore System ===");

            while (true) {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Add Customer");
                System.out.println("2. Add Book");
                System.out.println("3. Update Customer Info");
                System.out.println("4. Update Book Price");
                System.out.println("5. Delete Customer");
                System.out.println("6. Delete Book");
                System.out.println("7. View Bestselling Books by Category");
                System.out.println("8. Compare Book Sales Before/After Price Change");
                System.out.println("9. View Sales Statistics by Category");
                System.out.println("10. View Total Book Sales by Age Group");
                System.out.println("0. Exit");
                System.out.print("Select: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> CustomerManager.addCustomer(conn, scanner);
                    case 2 -> BookManager.addBook(conn, scanner);
                    case 3 -> CustomerManager.updateCustomer(conn, scanner);
                    case 4 -> BookManager.updateBookPrice(conn, scanner);
                    case 5 -> CustomerManager.deleteCustomer(conn, scanner);
                    case 6 -> BookManager.deleteBook(conn, scanner);
                    case 7 -> AnalysisManager.viewBestsellingBooksByCategory(conn, scanner);
                    case 8 -> AnalysisManager.compareBookSalesBeforeAfterPriceChange(conn, scanner);
                    case 9 -> AnalysisManager.viewSalesStatisticsByCategory(conn, scanner);
                    case 10 ->AnalysisManager.viewTotalBookSalesByAgeGroup(conn, scanner);
                    case 0 -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid option.");
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] DB connection failed: " + e.getMessage());
        }
    }
}
