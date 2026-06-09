-- ============================================================
-- Online Bookstore System - Create Schema
-- ============================================================

-- 1. category
CREATE TABLE category (
                          category_id   INTEGER      PRIMARY KEY,
                          category_name VARCHAR(100) NOT NULL
);

-- 2. publisher
CREATE TABLE publisher (
                           publisher_id   INTEGER      PRIMARY KEY,
                           publisher_name VARCHAR(100) NOT NULL,
                           city           VARCHAR(50),
                           manager_name   VARCHAR(100)
);

-- 3. customer
CREATE TABLE customer (
                          customer_id      INTEGER      PRIMARY KEY,
                          first_name       VARCHAR(50)  NOT NULL,
                          last_name        VARCHAR(50)  NOT NULL,
                          email            VARCHAR(100) UNIQUE,
                          phone_number     VARCHAR(20),
                          city             VARCHAR(50),
                          birth_date       DATE,
                          membership_grade VARCHAR(20)
);

-- 4. book
CREATE TABLE book (
                      book_id      INTEGER       PRIMARY KEY,
                      title        VARCHAR(100)  NOT NULL,
                      author       VARCHAR(50),
                      category_id  INTEGER       NOT NULL,
                      publisher_id INTEGER       NOT NULL,
                      unit_price   NUMERIC(10,2) NOT NULL,
                      FOREIGN KEY (category_id)  REFERENCES category(category_id),
                      FOREIGN KEY (publisher_id) REFERENCES publisher(publisher_id)
);

-- 5. total_sales  (sales & market_basket 둘 다 참조 → 먼저 생성)
CREATE TABLE total_sales (
                             total_sales_id   INTEGER       PRIMARY KEY,
                             market_basket_id INTEGER       NOT NULL UNIQUE,
                             total_amount     NUMERIC(10,2)
);

-- 6. sales
CREATE TABLE sales (
                       sales_id              INTEGER   PRIMARY KEY,
                       transaction_timestamp TIMESTAMP NOT NULL,
                       customer_id           INTEGER   NOT NULL,
                       market_basket_id      INTEGER   NOT NULL,
                       FOREIGN KEY (customer_id)      REFERENCES customer(customer_id),
                       FOREIGN KEY (market_basket_id) REFERENCES total_sales(market_basket_id)
);

-- 7. market_basket
CREATE TABLE market_basket (
                               basket_item_id    INTEGER       PRIMARY KEY,
                               market_basket_id  INTEGER       NOT NULL,
                               book_id           INTEGER       NOT NULL,
                               quantity          INTEGER       NOT NULL CHECK (quantity > 0),
                               price_at_purchase NUMERIC(10,2) NOT NULL CHECK (price_at_purchase > 0),
                               FOREIGN KEY (market_basket_id) REFERENCES total_sales(market_basket_id),
                               FOREIGN KEY (book_id)       REFERENCES book(book_id),
                               UNIQUE (market_basket_id, book_id)
);

-- 8. customer_history
CREATE TABLE customer_history (
                                  history_id       INTEGER AUTO_INCREMENT PRIMARY KEY,
                                  customer_id      INTEGER   NOT NULL,
                                  old_city VARCHAR(50),
                                  new_city VARCHAR(50),
                                  old_birth_date DATE,
                                  new_birth_date DATE,
                                  old_membership_grade VARCHAR(20),
                                  new_membership_grade VARCHAR(20),
                                  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (customer_id) REFERENCES customer(Customer_ID)
);
CREATE TABLE book_price_history (
    history_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    book_id INTEGER NOT NULL,
    old_price NUMERIC(10,2) NOT NULL,
    new_price NUMERIC(10,2) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES book(book_id)
);

   
-- ============================================================
-- INDEX (REQ3, REQ11)
-- ============================================================
CREATE INDEX idx_book_category    ON book(category_id);
CREATE INDEX idx_book_publisher   ON book(publisher_id);
CREATE INDEX idx_sales_customer   ON sales(customer_id);
CREATE INDEX idx_basket_book   ON market_basket(book_id);
CREATE INDEX idx_history_customer ON customer_history(customer_id);

-- ============================================================
-- VIEW 1 : order_summary_view (REQ6 - JOIN + VIEW)
-- 주문별 고객 이름 + 책 제목 + 수량 + 주문 당시 가격
-- ============================================================
CREATE VIEW order_summary_view AS
SELECT
    s.sales_id,
    s.transaction_timestamp,

    c.customer_id,
    c.first_name,
    c.last_name,
    c.city,
    c.membership_grade,

    b.book_id,
    b.title,
    b.author,
    b.unit_price,

    cat.category_name,

    mb.quantity,
    mb.price_at_purchase,

    mb.quantity * mb.price_at_purchase AS subtotal,

    ts.total_amount

FROM sales s
JOIN customer c
    ON s.customer_id = c.customer_id

JOIN total_sales ts
    ON s.market_basket_id = ts.market_basket_id

JOIN market_basket mb
    ON ts.market_basket_id = mb.market_basket_id

JOIN book b
    ON mb.book_id = b.book_id

JOIN category cat
    ON b.category_id = cat.category_id;

-- ============================================================
-- VIEW 2 : book_sales_summary_view (REQ7 - 집계 + GROUP BY)
-- 책별 총 판매 수량 + 총 매출 (주문 당시 가격 기준 = REQ13)
-- ============================================================
CREATE VIEW book_sales_summary_view AS
SELECT
    b.book_id,
    b.title,
    b.author,
    b.unit_price         AS current_price,
    cat.category_name,
    p.publisher_name,
    SUM(mb.quantity)                        AS total_quantity_sold,
    SUM(mb.quantity * mb.price_at_purchase) AS total_revenue
FROM book b
         JOIN category      cat ON b.category_id  = cat.category_id
         JOIN publisher     p   ON b.publisher_id = p.publisher_id
         JOIN market_basket mb  ON b.book_id      = mb.book_id
GROUP BY
    b.book_id, b.title, b.author,
    b.unit_price, cat.category_name, p.publisher_name;
