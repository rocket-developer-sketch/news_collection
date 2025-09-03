-- ========================
-- 개발/테스트 환경 전용 초기화용
-- ========================

USE news_api;

-- ========================
-- 1. users 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` varchar(30) NOT NULL,
    `email` varchar(100) NOT NULL,
    `password_updated_at` datetime DEFAULT NULL,
    `created_at` datetime NOT NULL,
    `password_hash` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_userId` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================
-- 2. refresh_tokens 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `refresh_tokens` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` varchar(30) NOT NULL,
    `revoked` TINYINT(1) NOT NULL,
    `issued_at` datetime NOT NULL,
    `expires_at` datetime(6) NOT NULL,
    `token_hash` varchar(128) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_refresh_token_hash` (`token_hash`),
    KEY `idx_refresh_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================
-- 3. news_sites 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `news_sites` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `site_name` varchar(100) NOT NULL,
    `url_template` varchar(1000) NOT NULL,
    CONSTRAINT `uk_news_sites_site_name` UNIQUE (`site_name`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================
-- 4. keyword 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `keywords` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `word` varchar(100) NOT NULL,
    `created_at` datetime NOT NULL,
    CONSTRAINT `uk_keywords_word` UNIQUE (`word`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================
-- 5. intervals 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `intervals` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `label` varchar(50) NOT NULL,
    `cron_expression` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================
-- 6. news_data_configs 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `news_data_configs` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `keyword_id` bigint NOT NULL,
    `site_id` bigint NOT NULL,
    `interval_id` bigint NOT NULL,
    `is_active` tinyint(1) NOT NULL,
    `last_run_at` datetime NOT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_active` (`user_id`,`is_active`),
    CONSTRAINT `fk_job_interval` FOREIGN KEY (`interval_id`) REFERENCES `intervals` (`id`),
    CONSTRAINT `fk_job_keyword` FOREIGN KEY (`keyword_id`) REFERENCES `keywords` (`id`),
    CONSTRAINT `fk_job_site` FOREIGN KEY (`site_id`) REFERENCES `news_sites` (`id`),
    CONSTRAINT `fk_job_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================
-- 7. news_articles 테이블
-- ========================
CREATE TABLE IF NOT EXISTS `news_articles` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `news_id` bigint DEFAULT NULL,
    `site_id` bigint NOT NULL,
    `job_config_id` bigint NOT NULL,
    `keyword` varchar(100) DEFAULT NULL,
    `published_at` datetime DEFAULT NULL,
    `crawled_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `news_url` varchar(1000) DEFAULT NULL,
    `title` varchar(500) DEFAULT NULL,
    `content` text,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_article_jobconfig` FOREIGN KEY (`job_config_id`) REFERENCES `news_data_configs` (`id`),
    CONSTRAINT `fk_article_site` FOREIGN KEY (`site_id`) REFERENCES `news_sites` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
