-- 收藏/评论模块建表（若已执行完整建库脚本可跳过）
USE stamp_dams;

CREATE TABLE IF NOT EXISTS favorite_folder (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL,
  folder_name     VARCHAR(128) NOT NULL,
  is_public       TINYINT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_folder_user (user_id),
  CONSTRAINT fk_folder_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorite_item (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  folder_id       BIGINT NOT NULL,
  stamp_id        BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_folder_stamp (folder_id, stamp_id),
  KEY idx_item_folder (folder_id),
  CONSTRAINT fk_item_folder FOREIGN KEY (folder_id) REFERENCES favorite_folder(id),
  CONSTRAINT fk_item_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comment (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  user_id         BIGINT NOT NULL,
  content         TEXT NOT NULL,
  status          VARCHAR(16) NOT NULL DEFAULT 'NORMAL',
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_comment_stamp (stamp_id),
  KEY idx_comment_user (user_id),
  CONSTRAINT fk_comment_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comment_like (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  comment_id      BIGINT NOT NULL,
  user_id         BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_like (comment_id, user_id),
  KEY idx_like_comment (comment_id),
  CONSTRAINT fk_like_comment FOREIGN KEY (comment_id) REFERENCES comment(id),
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

