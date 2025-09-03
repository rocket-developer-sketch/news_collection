-- ========================
-- 개발/테스트 전용 초기 데이터 삽입 SQL
-- 대상 DB: news_api
-- ========================

USE `news_api`;

-- ------------------------
-- 1. intervals
-- ------------------------
INSERT INTO `intervals` (label, cron_expression) VALUES
('5분마다', '0 */5 * * * ?'),
('10분마다', '0 */10 * * * ?'),
('15분마다', '0 */15 * * * ?'),
('20분마다', '0 */20 * * * ?'),
('30분마다', '0 */30 * * * ?'),
('매 시 정각', '0 0 * * * ?');

-- ------------------------
-- 2. news_sites
-- ------------------------
INSERT INTO `news_sites` (site_name, url_template) VALUES
('NAVER', 'https://openapi.naver.com/v1/search/news.json?query={query}&display={display}&start={start}&sort=sim'),
('DAUM', 'https://search.daum.net/search?w=news&q={keyword}&p={page}'),
('GOOGLE', 'https://www.googleapis.com/customsearch/v1?key={apikey}&cx={cx}&q={query}&start={start}&num={num}');

-- ------------------------
-- 3. users
-- ------------------------
INSERT INTO `users` (id, user_id, email, password_updated_at, created_at, password_hash) VALUES
(1, 'tester', 'tester@example.com', '2025-07-29 11:47:17', '2025-07-29 11:47:17','$2a$10$GmKTQWldL/EWa8r521tf8uoELAL4ufYei1oWymBpG3cBnuKu4ugle');