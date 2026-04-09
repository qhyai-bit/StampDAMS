-- 鉴赏扩展表 + 市场参考价表（若已执行完整建库脚本可跳过）
USE stamp_dams;

CREATE TABLE IF NOT EXISTS stamp_appreciation (
  id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id            BIGINT NOT NULL,
  appreciation_points TEXT,
  value_analysis      TEXT,
  rarity_level        VARCHAR(32) DEFAULT NULL,
  watermark_download  TINYINT NOT NULL DEFAULT 1,
  created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_app_stamp (stamp_id),
  CONSTRAINT fk_appreciation_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS market_price (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id         BIGINT NOT NULL,
  price_type       VARCHAR(32) NOT NULL,
  reference_price  DECIMAL(12,2) NOT NULL,
  currency         VARCHAR(16) NOT NULL DEFAULT 'CNY',
  record_date      DATE NOT NULL,
  source           VARCHAR(128) DEFAULT NULL,
  remark           VARCHAR(255) DEFAULT NULL,
  created_by       BIGINT DEFAULT NULL,
  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_market_stamp_date (stamp_id, record_date),
  CONSTRAINT fk_market_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
