import java.sql.Connection;
import java.sql.PreparedStatement;
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

    // 2. [REQ8, REQ12] Customer 정보 수정 기능 (UPDATE with Transaction)
    public static void updateCustomer(Connection conn, Scanner scanner) {
        System.out.println("\n=== [REQ8] 고객 정보 수정 (트랜잭션 적용) ===");
        
        System.out.print("정보를 수정할 고객의 ID를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기
        
        System.out.print("새로운 도시(City): ");
        String newCity = scanner.nextLine();
        
        System.out.print("새로운 멤버십 등급: ");
        String newGrade = scanner.nextLine();

        String updateSql = "UPDATE customer SET City = ?, membership_grade = ? WHERE Customer_ID = ?";

        try {
            // [REQ12] 수동 커밋 모드로 전환 (트랜잭션 시작)
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, newCity);
                pstmt.setString(2, newGrade);
                pstmt.setInt(3, id);

                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    // 성공하면 데이터베이스에 영구 반영
                    conn.commit();
                    System.out.println(">> [REQ12] 트랜잭션 커밋 완료! 고객 정보가 수정되었습니다.");
                } else {
                    // 수정된 행이 없다면 존재하지 않는 ID이므로 롤백
                    conn.rollback();
                    System.out.println(">> 해당 ID의 고객이 존재하지 않습니다. 트랜잭션 롤백.");
                }
            } catch (SQLException e) {
                // 에러 발생 시 원래대로 되돌림
                conn.rollback();
                System.out.println(">> SQL 실행 중 에러 발생. 트랜잭션 롤백 실행.");
                throw e;
            }

        } catch (SQLException e) {
            System.out.println(">> [오류] 수정 실패: " + e.getMessage());
        } finally {
            try {
                // 다음 쿼리들을 위해 자동 커밋 모드를 다시 원상복구(true) 시켜줍니다.
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
