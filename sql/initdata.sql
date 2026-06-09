-- ============================================================
-- Online Bookstore System - Initial Data
-- ============================================================

-- 1. category
INSERT INTO category VALUES (1, 'Fiction');
INSERT INTO category VALUES (2, 'Computer');
INSERT INTO category VALUES (3, 'Business');
INSERT INTO category VALUES (4, 'Science');
INSERT INTO category VALUES (5, 'History');
INSERT INTO category VALUES (6, 'Self-Help');
INSERT INTO category VALUES (7, 'Art');
INSERT INTO category VALUES (8, 'Children');
INSERT INTO category VALUES (9, 'Travel');
INSERT INTO category VALUES (10, 'Education');

-- 2. publisher
INSERT INTO publisher VALUES (1, 'Hanbit Media', 'Seoul', 'Kim Minjun');
INSERT INTO publisher VALUES (2, 'Gilbut', 'Seoul', 'Lee Jisoo');
INSERT INTO publisher VALUES (3, 'Eulyoo Publishing', 'Seoul', 'Park Hana');
INSERT INTO publisher VALUES (4, 'Munhakdongne', 'Paju', 'Choi Yuna');
INSERT INTO publisher VALUES (5, 'Wisdom House', 'Paju', 'Jung Hyunwoo');
INSERT INTO publisher VALUES (6, 'Book21', 'Seoul', 'Kang Doyeon');
INSERT INTO publisher VALUES (7, 'Minumsa', 'Seoul', 'Yoon Seojin');
INSERT INTO publisher VALUES (8, 'Dasan Books', 'Paju', 'Han Jiho');
INSERT INTO publisher VALUES (9, 'Youngjin', 'Seoul', 'Oh Sumin');
INSERT INTO publisher VALUES (10, 'Sakyejul', 'Paju', 'Seo Jiwon');

-- 3. customer
INSERT INTO customer VALUES (1, 'Seongyeun', 'Cho', 'Seongyeun.Cho@email.com', '010-1000-1001', 'Seoul', '2004-03-12', 'Gold');
INSERT INTO customer VALUES (2, 'Minji', 'Kim', 'minji.kim@email.com', '010-1000-1002', 'Busan', '1998-07-21', 'Silver');
INSERT INTO customer VALUES (3, 'Hyunwoo', 'Park', 'hyunwoo.park@email.com', '010-1000-1003', 'Incheon', '1995-11-05', 'Bronze');
INSERT INTO customer VALUES (4, 'Soyeon', 'Choi', 'soyeon.choi@email.com', '010-1000-1004', 'Daegu', '2001-02-17', 'Gold');
INSERT INTO customer VALUES (5, 'Junho', 'Jung', 'junho.jung@email.com', '010-1000-1005', 'Daejeon', '1989-09-30', 'Silver');
INSERT INTO customer VALUES (6, 'Yujin', 'Han', 'yujin.han@email.com', '010-1000-1006', 'Gwangju', '1992-12-14', 'Gold');
INSERT INTO customer VALUES (7, 'Seojun', 'Yoon', 'seojun.yoon@email.com', '010-1000-1007', 'Seoul', '1985-04-08', 'Bronze');
INSERT INTO customer VALUES (8, 'Harin', 'Oh', 'harin.oh@email.com', '010-1000-1008', 'Suwon', '2006-06-25', 'Silver');
INSERT INTO customer VALUES (9, 'Doyoon', 'Kang', 'doyoon.kang@email.com', '010-1000-1009', 'Ulsan', '1979-01-19', 'Gold');
INSERT INTO customer VALUES (10, 'Jiwon', 'Seo', 'jiwon.seo@email.com', '010-1000-1010', 'Jeju', '2000-10-03', 'Bronze');

-- 4. book
INSERT INTO book VALUES (1, 'The Silent Library', 'Alice Morgan', 1, 4, 15.00);
INSERT INTO book VALUES (2, 'Java Programming Basics', 'James Carter', 2, 1, 18.00);
INSERT INTO book VALUES (3, 'Database Design Guide', 'Robert Kim', 2, 2, 22.00);
INSERT INTO book VALUES (4, 'Modern Business Strategy', 'Sarah Lee', 3, 6, 27.00);
INSERT INTO book VALUES (5, 'Introduction to Physics', 'David Brown', 4, 3, 16.00);
INSERT INTO book VALUES (6, 'Korean History Today', 'Hana Park', 5, 10, 14.00);
INSERT INTO book VALUES (7, 'Think Better', 'Michael Green', 6, 5, 25.00);
INSERT INTO book VALUES (8, 'Drawing for Beginners', 'Emily White', 7, 8, 13.00);
INSERT INTO book VALUES (9, 'Children Story Box', 'Olivia Wilson', 8, 10, 20.00);
INSERT INTO book VALUES (10, 'Travel Around Europe', 'Daniel Smith', 9, 8, 30.00);
INSERT INTO book VALUES (11, 'English Grammar Practice', 'Sophia Taylor', 10, 2, 12.00);
INSERT INTO book VALUES (12, 'Python Data Analysis', 'Kevin Miller', 2, 9, 17.00);
INSERT INTO book VALUES (13, 'Startup Mindset', 'Laura Johnson', 3, 6, 24.00);
INSERT INTO book VALUES (14, 'Astronomy Night', 'Chris Evans', 4, 3, 21.00);
INSERT INTO book VALUES (15, 'World War Stories', 'George Harris', 5, 7, 15.50);
INSERT INTO book VALUES (16, 'Daily Motivation', 'Emma Davis', 6, 5, 19.00);
INSERT INTO book VALUES (17, 'Art Museum Guide', 'Noah Martin', 7, 8, 26.00);
INSERT INTO book VALUES (18, 'Fun Math for Kids', 'Mia Anderson', 8, 10, 23.00);
INSERT INTO book VALUES (19, 'Seoul Travel Notes', 'Ethan Thomas', 9, 8, 28.00);
INSERT INTO book VALUES (20, 'Study Skills Handbook', 'Grace Moore', 10, 2, 11.00);

-- 5. total_sales
INSERT INTO total_sales VALUES (1, 1001, 50.00);
INSERT INTO total_sales VALUES (2, 1002, 48.00);
INSERT INTO total_sales VALUES (3, 1003, 87.00);
INSERT INTO total_sales VALUES (4, 1004, 56.00);
INSERT INTO total_sales VALUES (5, 1005, 64.00);
INSERT INTO total_sales VALUES (6, 1006, 55.00);
INSERT INTO total_sales VALUES (7, 1007, 60.00);
INSERT INTO total_sales VALUES (8, 1008, 70.00);
INSERT INTO total_sales VALUES (9, 1009, 43.00);
INSERT INTO total_sales VALUES (10, 1010, 87.00);
INSERT INTO total_sales VALUES (11, 1011, 53.00);
INSERT INTO total_sales VALUES (12, 1012, 92.50);

-- 6. sales
INSERT INTO sales VALUES (1, '2026-05-01 09:15:00', 1, 1001);
INSERT INTO sales VALUES (2, '2026-05-01 10:20:00', 2, 1002);
INSERT INTO sales VALUES (3, '2026-05-02 11:10:00', 3, 1003);
INSERT INTO sales VALUES (4, '2026-05-02 13:45:00', 4, 1004);
INSERT INTO sales VALUES (5, '2026-05-03 14:30:00', 5, 1005);
INSERT INTO sales VALUES (6, '2026-05-03 15:05:00', 6, 1006);
INSERT INTO sales VALUES (7, '2026-05-04 09:40:00', 7, 1007);
INSERT INTO sales VALUES (8, '2026-05-04 10:25:00', 8, 1008);
INSERT INTO sales VALUES (9, '2026-05-05 12:15:00', 9, 1009);
INSERT INTO sales VALUES (10, '2026-05-05 16:00:00', 10, 1010);
INSERT INTO sales VALUES (11, '2026-05-06 11:30:00', 1, 1011);
INSERT INTO sales VALUES (12, '2026-05-06 17:20:00', 2, 1012);

-- 7. market_basket
INSERT INTO market_basket VALUES (1, 1001, 1, 2, 14.00);
INSERT INTO market_basket VALUES (2, 1001, 3, 1, 22.00);

INSERT INTO market_basket VALUES (3, 1002, 2, 1, 18.00);
INSERT INTO market_basket VALUES (4, 1002, 5, 2, 15.00);

INSERT INTO market_basket VALUES (5, 1003, 4, 1, 27.00);
INSERT INTO market_basket VALUES (6, 1003, 7, 1, 24.00);
INSERT INTO market_basket VALUES (7, 1003, 11, 3, 12.00);

INSERT INTO market_basket VALUES (8, 1004, 8, 2, 13.00);
INSERT INTO market_basket VALUES (9, 1004, 10, 1, 30.00);

INSERT INTO market_basket VALUES (10, 1005, 6, 1, 14.00);
INSERT INTO market_basket VALUES (11, 1005, 9, 1, 19.00);
INSERT INTO market_basket VALUES (12, 1005, 15, 2, 15.50);

INSERT INTO market_basket VALUES (13, 1006, 12, 2, 17.00);
INSERT INTO market_basket VALUES (14, 1006, 14, 1, 21.00);

INSERT INTO market_basket VALUES (15, 1007, 13, 1, 24.00);
INSERT INTO market_basket VALUES (16, 1007, 16, 2, 18.00);

INSERT INTO market_basket VALUES (17, 1008, 17, 1, 25.00);
INSERT INTO market_basket VALUES (18, 1008, 18, 1, 23.00);
INSERT INTO market_basket VALUES (19, 1008, 20, 2, 11.00);

INSERT INTO market_basket VALUES (20, 1009, 1, 1, 15.00);
INSERT INTO market_basket VALUES (21, 1009, 19, 1, 28.00);

INSERT INTO market_basket VALUES (22, 1010, 3, 2, 21.00);
INSERT INTO market_basket VALUES (23, 1010, 5, 1, 16.00);
INSERT INTO market_basket VALUES (24, 1010, 10, 1, 29.00);

INSERT INTO market_basket VALUES (25, 1011, 2, 2, 18.00);
INSERT INTO market_basket VALUES (26, 1011, 12, 1, 17.00);

INSERT INTO market_basket VALUES (27, 1012, 4, 1, 27.00);
INSERT INTO market_basket VALUES (28, 1012, 7, 2, 25.00);
INSERT INTO market_basket VALUES (29, 1012, 15, 1, 15.50);

-- 8. customer_history
INSERT INTO customer_history VALUES (1, 1, 'Busan', '2004-03-12', 'Silver', 'Seoul', '2004-03-12', 'Gold', '2026-05-03 10:00:00'),
INSERT INTO customer_history VALUES (2, 2, 'Seoul', '1998-07-21', 'Bronze', 'Busan', '1998-07-21', 'Silver', '2026-05-04 10:00:00'),
INSERT INTO customer_history VALUES (3, 3, 'Daegu', '1995-11-05', 'Silver', 'Incheon', '1995-11-05', 'Bronze', '2026-05-02 10:00:00'),
INSERT INTO customer_history VALUES (4, 4, 'Incheon', '2001-02-17', 'Silver', 'Daegu', '2001-02-17', 'Gold', '2026-05-03 10:00:00'),
INSERT INTO customer_history VALUES (5, 5, 'Seoul', '1989-09-30', 'Bronze', 'Daejeon', '1989-09-30', 'Silver', '2026-05-04 10:00:00'),
INSERT INTO customer_history VALUES (6, 6, 'Busan', '1992-12-14', 'Silver', 'Gwangju', '1992-12-14', 'Gold', '2026-05-04 10:00:00'),
INSERT INTO customer_history VALUES (7, 7, 'Daejeon', '1985-04-08', 'Bronze', 'Seoul', '1985-04-08', 'Bronze', '2026-05-05 10:00:00'),
INSERT INTO customer_history VALUES (8, 8, 'Seoul', '2006-06-25', 'Bronze', 'Suwon', '2006-06-25', 'Silver', '2026-05-05 10:00:00'),
INSERT INTO customer_history VALUES (9, 9, 'Gwangju', '1979-01-19', 'Silver', 'Ulsan', '1979-01-19', 'Gold', '2026-05-06 10:00:00'),
INSERT INTO customer_history VALUES (10, 10, 'Busan', '2000-10-03', 'Silver', 'Jeju', '2000-10-03', 'Bronze', '2026-05-06 10:00:00');


-- 9. book_price_history

INSERT INTO book_price_history
(book_id, old_price, new_price, changed_at)
VALUES
(2, 15.00, 18.00, '2026-05-02 09:00:00');

INSERT INTO book_price_history
(book_id, old_price, new_price, changed_at)
VALUES
(3, 20.00, 22.00, '2026-05-04 09:00:00');

INSERT INTO book_price_history
(book_id, old_price, new_price, changed_at)
VALUES
(10, 25.00, 30.00, '2026-05-03 09:00:00');

INSERT INTO book_price_history
(book_id, old_price, new_price, changed_at)
VALUES
(15, 13.00, 15.50, '2026-05-05 09:00:00');
