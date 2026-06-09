# Online Bookstore Sales Tracking and Analysis System

## 1. Project Overview

This project is a Java console application for an online bookstore sales tracking and analysis system.

The application uses JDBC and MySQL to allow users to insert, select, update, and delete data in the database. It extends the HW2-1 online bookstore schema by adding sales, market basket, customer history, book price history, views, indexes, and analysis queries.

The application provides a text-based user interface and supports the required CRUD and analysis menus.

---

## 2. Tech Stack

| Category | Technology |
|---|---|
| Language | Java |
| Database | MySQL |
| DB Connection | JDBC |
| DB Driver | MySQL Connector/J |
| Interface | Text-based console UI |
| Version Control | Git, GitHub |
| Recommended IDE | IntelliJ IDEA, optional |

---

## 3. Project Structure

```text
database-not-null/
├── src/
│   ├── Main.java
│   ├── DBConnection.java
│   ├── BookManager.java
│   ├── CustomerManager.java
│   └── AnalysisManager.java
├── sql/
│   ├── createschema.sql
│   ├── initdata.sql
│   └── dropschema.sql
├── README.md
└── OnlineBookstoreSystem.iml
```

### Main Class

```text
Main
```

The entry point of the application is `Main.java`.

---

## 4. Prerequisites

Before running the project, the following programs must be installed.

### Required

1. Java JDK, recommended version 17 or later.
2. MySQL Server.
3. MySQL Connector/J.
4. Git, if cloning the repository from GitHub.

---

## 5. Installation Check

### macOS

```bash
java -version
javac -version
mysql --version
git --version
```

If Java or MySQL is not installed, install them with Homebrew.

```bash
brew install openjdk@17
brew install mysql
brew services start mysql
```

If Git is not installed:

```bash
brew install git
```

### Windows

Open Command Prompt or PowerShell and run:

```bat
java -version
javac -version
mysql --version
git --version
```

If Java is not installed, install a JDK such as Oracle JDK, OpenJDK, or Temurin JDK.

If MySQL is not installed, install MySQL Server from the MySQL official installer. During installation, remember the MySQL root password because it must be written in `DBConnection.java`.

If Git is not installed, install Git for Windows.

After installation, if `java`, `javac`, `mysql`, or `git` is not recognized, add each program to the Windows `Path` environment variable.

---

## 6. Clone Repository

```bash
git clone https://github.com/sueandcream/database-not-null.git
cd database-not-null
```

For Windows Command Prompt or PowerShell, the same commands can be used.

```bat
git clone https://github.com/sueandcream/database-not-null.git
cd database-not-null
```

---

## 7. MySQL Connector/J Setup

This project uses JDBC to connect Java and MySQL, so MySQL Connector/J is required.

Create a `lib` directory in the project root.

### macOS

```bash
mkdir -p lib
```

### Windows

```bat
mkdir lib
```

Place the MySQL Connector/J jar file inside the `lib` directory.

Example:

```text
database-not-null/
└── lib/
    └── mysql-connector-j-8.4.0.jar
```

The actual connector version may be different. If your file name is different, replace `mysql-connector-j-8.4.0.jar` in the commands below with your actual file name.

---

## 8. Database Connection Configuration

Open `src/DBConnection.java` and check the following values.

```java
private static final String URL = "jdbc:mysql://localhost:3306/bookstore";
private static final String USER = "root";
private static final String PASSWORD = "비밀번호";
```

Before running the program, replace `"비밀번호"` with your own MySQL root password.

Example:

```java
private static final String PASSWORD = "your_mysql_password";
```

If your MySQL username is not `root`, also update the `USER` value.

---

## 9. Database Setup

The SQL scripts must be executed before running the Java application.

### 9.1. Create Database

#### macOS

```bash
mysql -u root -p
```

#### Windows

```bat
mysql -u root -p
```

Then run the following SQL commands in the MySQL console.

```sql
CREATE DATABASE bookstore;
EXIT;
```

If the database already exists, this step can be skipped.

### 9.2. Run SQL Scripts

Run the scripts from the project root directory in the following order.

#### macOS

```bash
mysql -u root -p bookstore < sql/dropschema.sql
mysql -u root -p bookstore < sql/createschema.sql
mysql -u root -p bookstore < sql/initdata.sql
```

#### Windows Command Prompt

```bat
mysql -u root -p bookstore < sql\dropschema.sql
mysql -u root -p bookstore < sql\createschema.sql
mysql -u root -p bookstore < sql\initdata.sql
```

#### Windows PowerShell

```powershell
Get-Content .\sql\dropschema.sql | mysql -u root -p bookstore
Get-Content .\sql\createschema.sql | mysql -u root -p bookstore
Get-Content .\sql\initdata.sql | mysql -u root -p bookstore
```

The correct order is:

1. `dropschema.sql`
2. `createschema.sql`
3. `initdata.sql`

The SQL scripts do not need a `USE DATABASE` statement because the database name is passed through the command line.

---

## 10. Compile and Run Without Jar

### macOS

```bash
mkdir -p out
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d out src/*.java
java -cp "out:lib/mysql-connector-j-8.4.0.jar" Main
```

### Windows Command Prompt

```bat
mkdir out
javac -cp "lib\mysql-connector-j-8.4.0.jar" -d out src\*.java
java -cp "out;lib\mysql-connector-j-8.4.0.jar" Main
```

### Windows PowerShell

```powershell
mkdir out
javac -cp "lib\mysql-connector-j-8.4.0.jar" -d out src\*.java
java -cp "out;lib\mysql-connector-j-8.4.0.jar" Main
```

Important difference:

1. macOS and Linux use `:` in the classpath.
2. Windows uses `;` in the classpath.

---

## 11. Create Executable Jar File

### 11.1. Compile Source Code

#### macOS

```bash
mkdir -p out
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d out src/*.java
```

#### Windows

```bat
mkdir out
javac -cp "lib\mysql-connector-j-8.4.0.jar" -d out src\*.java
```

### 11.2. Create Manifest File

#### macOS

```bash
echo "Main-Class: Main" > manifest.txt
```

#### Windows Command Prompt

```bat
echo Main-Class: Main> manifest.txt
```

#### Windows PowerShell

```powershell
"Main-Class: Main" | Out-File -Encoding ascii manifest.txt
```

### 11.3. Create Jar

#### macOS

```bash
jar cfm OnlineBookstoreSystem.jar manifest.txt -C out .
```

#### Windows

```bat
jar cfm OnlineBookstoreSystem.jar manifest.txt -C out .
```

### 11.4. Run Jar

The MySQL JDBC driver is not included inside `OnlineBookstoreSystem.jar`, so the connector jar must be included in the classpath when running the application.

#### macOS

```bash
java -cp "OnlineBookstoreSystem.jar:lib/mysql-connector-j-8.4.0.jar" Main
```

#### Windows Command Prompt

```bat
java -cp "OnlineBookstoreSystem.jar;lib\mysql-connector-j-8.4.0.jar" Main
```

#### Windows PowerShell

```powershell
java -cp "OnlineBookstoreSystem.jar;lib\mysql-connector-j-8.4.0.jar" Main
```

---

## 12. Application Menu

When the application starts, the following menu is displayed.

```text
=== Online Bookstore System ===

--- MENU ---
1. Add Customer
2. Add Book
3. Update Customer Info
4. Update Book Price
5. Delete Customer
6. Delete Book
7. View Bestselling Books by Category
8. Compare Book Sales Before/After Price Change
9. View Sales Statistics by Category
10. View Total Book Sales by Age Group
0. Exit
```

---

## 13. Menu and Requirement Mapping

| Menu | Feature | SQL Type | Related Requirement |
|---:|---|---|---|
| 1 | Add Customer | INSERT, PreparedStatement | REQ5, REQ10 |
| 2 | Add Book | INSERT, PreparedStatement | REQ5, REQ10 |
| 3 | Update Customer Info | UPDATE, Transaction, History | REQ8, REQ12, REQ14 |
| 4 | Update Book Price | UPDATE, Transaction, Price History | REQ8, REQ12, REQ13 |
| 5 | Delete Customer | DELETE, PreparedStatement | REQ9, REQ10 |
| 6 | Delete Book | DELETE, PreparedStatement | REQ9, REQ10 |
| 7 | View Bestselling Books by Category | SELECT, User Input, JOIN, VIEW | REQ6 |
| 8 | Compare Book Sales Before/After Price Change | SELECT, User Input, JOIN, VIEW, Aggregation | REQ6, REQ13 |
| 9 | View Sales Statistics by Category | SELECT, User Input, Aggregation, GROUP BY | REQ7 |
| 10 | View Total Book Sales by Age Group | SELECT, User Input, Aggregation, GROUP BY | REQ7, REQ14 |

---

## 14. Database Schema Summary

The database includes more than seven tables and more than two views.

### Main Tables

1. `category`
2. `publisher`
3. `customer`
4. `book`
5. `total_sales`
6. `sales`
7. `market_basket`
8. `customer_history`
9. `book_price_history`

### Views

1. `order_summary_view`
2. `book_sales_summary_view`

The schema includes primary keys, foreign keys, indexes, and views.

---

## 15. Java Class Summary

| Class | Responsibility |
|---|---|
| `Main` | Starts the program and controls the text-based menu. |
| `DBConnection` | Connects the Java application to the MySQL database. |
| `CustomerManager` | Handles customer insert, update, delete, and customer history logic. |
| `BookManager` | Handles book insert, book price update, price history insertion, and book delete logic. |
| `AnalysisManager` | Handles select and analysis menus using joins, views, aggregation, and group by. |
