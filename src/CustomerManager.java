import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerManager {

    // 1. [REQ5] Customer 추가 기능 (INSERT)
    public static void addCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ5] 신규 고객 등록 ===");

        // 사용자에게 글자 입력받기
        System.out.print("고객 ID (숫자): ");
        int id = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        System.out.print("이름(First Name): ");
        String firstName = scanner.nextLine();

        System.out.print("성(Last Name): ");
        String lastName = scanner.nextLine();

        System.out.print("이메일: ");
        String email = scanner.nextLine();

        System.out.print("전화번호: ");
        String phone = scanner.nextLine();

        System.out.print("도시(City): ");
        String city = scanner.nextLine();

        System.out.print("생년월일 (YYYY-MM-DD): ");
        String birthDate = scanner.nextLine();

        System.out.print("멤버십 등급: ");
        String grade = scanner.nextLine();

        // [REQ10] PreparedStatement를 사용하기 위한 SQL 쿼리 (? 사용)
        String sql = "INSERT INTO customer (Customer_ID, First_name, Last_name, Email, Phone_number, City, birth_date, membership_grade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ? 자리에 입력값 매핑하기
            pstmt.setInt(1, id);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, city);
            pstmt.setDate(7, java.sql.Date.valueOf(birthDate)); // 문자열을 SQL 날짜형으로 변환
            pstmt.setString(8, grade);

            int rows = pstmt.executeUpdate(); // 실행
            if (rows > 0) {
                System.out.println(">> 고객 등록 성공!");
            }
        } catch (SQLException e) {
            System.out.println(">> [오류] 고객 등록 실패: " + e.getMessage());
        }
    }

    // 2. [REQ8, REQ12] Customer 정보 수정 및 이력 저장 (UPDATE & INSERT with Transaction)
    public static void updateCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ8] 고객 정보 수정 및 이력 기록 ===");

        System.out.print("정보를 수정할 고객의 ID를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        System.out.print("새로운 도시(City): ");
        String newCity = scanner.nextLine();

        System.out.print("새로운 멤버십 등급: ");
        String newGrade = scanner.nextLine();

        System.out.print("새로운 생년월일 (YYYY-MM-DD): ");
        String newBirthDate = scanner.nextLine();

        // 1. 고객 정보 수정을 위한 SQL
        String updateCustomerSql = "UPDATE customer SET City = ?, membership_grade = ?, birth_date = ? WHERE Customer_ID = ?";

        // 2. 변경 이력(History) 저장을 위한 SQL (changed_at은 DB의 CURRENT_TIMESTAMP 활용)
        String insertHistorySql = "INSERT INTO customer_history (customer_id, city, birth_date, membership_grade, changed_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try {
            // [REQ12] 트랜잭션 시작 (수동 커밋 모드)
            conn.setAutoCommit(false);

            // [Step 1] 고객 정보 업데이트 실행
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateCustomerSql)) {
                pstmtUpdate.setString(1, newCity);
                pstmtUpdate.setString(2, newGrade);
                pstmtUpdate.setDate(3, java.sql.Date.valueOf(newBirthDate));
                pstmtUpdate.setInt(4, id);

                int updateRows = pstmtUpdate.executeUpdate();

                if (updateRows > 0) {
                    System.out.println(">> 고객 정보 수정 완료. 이력을 기록합니다...");

                    // [Step 2] 업데이트 성공 시, 이력 테이블(customer_history)에 변경사항 기록
                    try (PreparedStatement pstmtHistory = conn.prepareStatement(insertHistorySql)) {
                        pstmtHistory.setInt(1, id); // 외래키(FK)로 연결될 고객 ID
                        pstmtHistory.setString(2, newCity);
                        pstmtHistory.setDate(3, java.sql.Date.valueOf(newBirthDate));
                        pstmtHistory.setString(4, newGrade);

                        pstmtHistory.executeUpdate();
                    }

                    // 두 작업이 모두 성공하면 최종 커밋!
                    conn.commit();
                    System.out.println(">> [REQ12] 트랜잭션 커밋 완료! 고객 정보 수정 및 이력 저장이 모두 완료되었습니다.");

                } else {
                    // 수정할 고객 ID가 존재하지 않는 경우 전체 롤백
                    conn.rollback();
                    System.out.println(">> 해당 ID의 고객이 존재하지 않습니다. 트랜잭션 롤백.");
                }
            } catch (SQLException e) {
                // SQL 실행 중 에러 발생 시 전체 롤백 (고객 정보 수정과 이력 저장 모두 취소됨)
                conn.rollback();
                System.out.println(">> SQL 실행 중 에러 발생. 트랜잭션 롤백 실행.");
                throw e;
            }

        } catch (SQLException e) {
            System.out.println(">> [오류] 수정 및 이력 기록 실패: " + e.getMessage());
        } finally {
            try {
                // 다른 쿼리 작업을 위해 자동 커밋 모드를 다시 true로 복구
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 3. [REQ9, REQ10] Customer 삭제 기능 (DELETE)
    public static void deleteCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ9] 고객 삭제 ===");

        System.out.print("삭제할 고객의 ID를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        String checkCustomerSql =
                "SELECT First_name, Last_name FROM customer WHERE Customer_ID = ?";

        String checkSalesSql =
                "SELECT COUNT(*) AS sales_count FROM sales WHERE Customer_ID = ?";

        String deleteHistorySql =
                "DELETE FROM customer_history WHERE customer_id = ?";

        String deleteCustomerSql =
                "DELETE FROM customer WHERE Customer_ID = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtCheckCustomer = conn.prepareStatement(checkCustomerSql)) {
                pstmtCheckCustomer.setInt(1, id);
                ResultSet rsCustomer = pstmtCheckCustomer.executeQuery();

                if (!rsCustomer.next()) {
                    System.out.println(">> 해당 ID의 고객이 존재하지 않습니다.");
                    conn.rollback();
                    return;
                }

                String fullName = rsCustomer.getString("First_name") + " " + rsCustomer.getString("Last_name");

                try (PreparedStatement pstmtCheckSales = conn.prepareStatement(checkSalesSql)) {
                    pstmtCheckSales.setInt(1, id);
                    ResultSet rsSales = pstmtCheckSales.executeQuery();
                    rsSales.next();

                    int salesCount = rsSales.getInt("sales_count");

                    if (salesCount > 0) {
                        System.out.println(">> 구매 이력이 있는 고객은 삭제할 수 없습니다.");
                        System.out.println(">> 고객명: " + fullName);
                        conn.rollback();
                        return;
                    }
                }

                try (PreparedStatement pstmtDeleteHistory = conn.prepareStatement(deleteHistorySql);
                     PreparedStatement pstmtDeleteCustomer = conn.prepareStatement(deleteCustomerSql)) {

                    pstmtDeleteHistory.setInt(1, id);
                    pstmtDeleteHistory.executeUpdate();

                    pstmtDeleteCustomer.setInt(1, id);
                    int deletedRows = pstmtDeleteCustomer.executeUpdate();

                    if (deletedRows > 0) {
                        conn.commit();
                        System.out.println(">> 고객 삭제 성공!");
                        System.out.println(">> 삭제된 고객: " + fullName);
                    } else {
                        conn.rollback();
                        System.out.println(">> 고객 삭제 실패.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println(">> SQL 실행 중 에러 발생. 트랜잭션 롤백 실행.");
                throw e;
            }

        } catch (SQLException e) {
            System.out.println(">> [오류] 고객 삭제 실패: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
