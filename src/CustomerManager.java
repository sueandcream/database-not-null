import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerManager {

    // 1. [REQ5] Customer 추가 기능 (INSERT) - 기존 유지
    public static void addCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ5] 신규 고객 등록 ===");

        System.out.print("고객 ID (숫자): ");
        int id = scanner.nextInt();
        scanner.nextLine(); 

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

        String sql = "INSERT INTO customer (Customer_ID, First_name, Last_name, Email, Phone_number, City, birth_date, membership_grade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, city);
            pstmt.setDate(7, java.sql.Date.valueOf(birthDate)); 
            pstmt.setString(8, grade);

            int rows = pstmt.executeUpdate(); 
            if (rows > 0) {
                System.out.println(">> 고객 등록 성공!");
            }
        } catch (SQLException e) {
            System.out.println(">> [오류] 고객 등록 실패: " + e.getMessage());
        }
    }

    // 2. [REQ8, REQ12, REQ14] Customer 정보 수정 및 이전/이후 이력 저장 (★수정된 부분)
    public static void updateCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ8] 고객 정보 수정 및 변경 전/후 이력 기록 ===");

        System.out.print("정보를 수정할 고객의 ID를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("새로운 도시(City): ");
        String newCity = scanner.nextLine();

        System.out.print("새로운 멤버십 등급: ");
        String newGrade = scanner.nextLine();

        System.out.print("새로운 생년월일 (YYYY-MM-DD): ");
        String newBirthDate = scanner.nextLine();

        // [Step A] 업데이트 전에 "기존(과거) 고객 정보"를 먼저 가져오기 위한 쿼리
        String selectOldSql = "SELECT City, birth_date, membership_grade FROM customer WHERE Customer_ID = ?";

        // [Step B] 고객 정보를 새로운 값으로 변경하기 위한 쿼리
        String updateCustomerSql = "UPDATE customer SET City = ?, membership_grade = ?, birth_date = ? WHERE Customer_ID = ?";

        // [Step C] 변경 전(old) 정보와 변경 후(new) 정보를 모두 이력 테이블에 남기기 위한 쿼리
        String insertHistorySql = "INSERT INTO customer_history " +
                "(customer_id, old_city, new_city, old_birth_date, new_birth_date, old_membership_grade, new_membership_grade, changed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try {
            // [REQ12] 트랜잭션 시작 (수동 커밋 모드)
            conn.setAutoCommit(false);

            String oldCity = null;
            java.sql.Date oldBirthDate = null;
            String oldGrade = null;
            boolean customerExists = false;

            // 1. 기존 데이터 조회 실행
            try (PreparedStatement pstmtSelect = conn.prepareStatement(selectOldSql)) {
                pstmtSelect.setInt(1, id);
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    if (rs.next()) {
                        oldCity = rs.getString("City");
                        oldBirthDate = rs.getDate("birth_date");
                        oldGrade = rs.getString("membership_grade");
                        customerExists = true;
                    }
                }
            }

            // 만약 입력한 ID의 고객이 존재하지 않는다면 바로 트랜잭션 종료
            if (!customerExists) {
                System.out.println(">> 해당 ID의 고객이 존재하지 않습니다. 수정을 취소합니다.");
                conn.rollback();
                return;
            }

            // 2. 고객 정보 진짜로 업데이트 하기
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateCustomerSql)) {
                pstmtUpdate.setString(1, newCity);
                pstmtUpdate.setString(2, newGrade);
                pstmtUpdate.setDate(3, java.sql.Date.valueOf(newBirthDate));
                pstmtUpdate.setInt(4, id);

                pstmtUpdate.executeUpdate();
            }

            // 3. 기존(old) 데이터와 신규(new) 데이터를 모두 이력 테이블에 기록하기
            try (PreparedStatement pstmtHistory = conn.prepareStatement(insertHistorySql)) {
                pstmtHistory.setInt(1, id);
                pstmtHistory.setString(2, oldCity);       // 변경 전 도시
                pstmtHistory.setString(3, newCity);       // 변경 후 도시
                pstmtHistory.setDate(4, oldBirthDate);    // 변경 전 생일
                pstmtHistory.setDate(5, java.sql.Date.valueOf(newBirthDate)); // 변경 후 생일
                pstmtHistory.setString(6, oldGrade);      // 변경 전 등급
                pstmtHistory.setString(7, newGrade);      // 변경 후 등급

                pstmtHistory.executeUpdate();
            }

            // 두 작업(Update + History Insert)이 온전하게 완료되었을 때 최종 확정!
            conn.commit();
            System.out.println(">> [REQ12] 트랜잭션 커밋 완료! 과거 정보와 현재 정보가 모두 기록되었습니다.");

        } catch (SQLException e) {
            try {
                // 하나라도 실패하면 원상태로 롤백
                conn.rollback();
                System.out.println(">> 에러 발생으로 인해 변경 사항을 모두 취소(롤백)합니다.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println(">> [오류] 수정 및 이력 기록 실패: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true); // 커밋 모드 원상복구
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 3. [REQ9, REQ10] Customer 삭제 기능 (DELETE) - 기존 유지
    public static void deleteCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ9] 고객 삭제 ===");

        System.out.print("삭제할 고객의 ID를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 

        String checkCustomerSql = "SELECT First_name, Last_name FROM customer WHERE Customer_ID = ?";
        String checkSalesSql = "SELECT COUNT(*) AS sales_count FROM sales WHERE Customer_ID = ?";
        String deleteHistorySql = "DELETE FROM customer_history WHERE customer_id = ?";
        String deleteCustomerSql = "DELETE FROM customer WHERE Customer_ID = ?";

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
