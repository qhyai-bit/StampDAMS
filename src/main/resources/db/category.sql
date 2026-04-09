-- 分类索引模块：多级分类与关联表
USE stamp_dams;

CREATE TABLE IF NOT EXISTS category (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id       BIGINT NOT NULL DEFAULT 0,
  category_name   VARCHAR(128) NOT NULL,
  category_level  INT NOT NULL DEFAULT 1,
  sort_no         INT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS stamp_category_rel (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  category_id     BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_stamp_category (stamp_id, category_id),
  KEY idx_rel_stamp (stamp_id),
  KEY idx_rel_category (category_id),
  CONSTRAINT fk_rel_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  CONSTRAINT fk_rel_category FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

