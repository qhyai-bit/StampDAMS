-- =========================
-- 邮票数字化鉴赏与信息管理系统
-- MySQL 8.x 建库建表脚本
-- =========================

CREATE DATABASE IF NOT EXISTS stamp_dams
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE stamp_dams;

-- -------------------------
-- 0. 通用说明
-- -------------------------
-- 建议所有时间统一用 DATETIME
-- 逻辑删除字段 deleted：0=未删除，1=已删除

-- -------------------------
-- 1. 用户与权限（RBAC）
-- -------------------------

DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  username        VARCHAR(64) NOT NULL UNIQUE,
  password        VARCHAR(255) NOT NULL,
  nickname        VARCHAR(64) DEFAULT NULL,
  phone           VARCHAR(32) DEFAULT NULL,
  email           VARCHAR(128) DEFAULT NULL,
  avatar          VARCHAR(255) DEFAULT NULL,
  status          VARCHAR(16) NOT NULL DEFAULT 'ENABLED', -- ENABLED/DISABLED
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted         TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE role (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code       VARCHAR(64) NOT NULL UNIQUE, -- ADMIN / USER / VISITOR
  role_name       VARCHAR(64) NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE permission (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  perm_code       VARCHAR(128) NOT NULL UNIQUE, -- stamp:add, stamp:edit...
  perm_name       VARCHAR(128) NOT NULL,
  module_name     VARCHAR(64) DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_role (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL,
  role_id         BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_role (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE role_permission (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id         BIGINT NOT NULL,
  permission_id   BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_perm (role_id, permission_id),
  CONSTRAINT fk_role_perm_role FOREIGN KEY (role_id) REFERENCES role(id),
  CONSTRAINT fk_role_perm_perm FOREIGN KEY (permission_id) REFERENCES permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 2. 邮票基础信息模块
-- -------------------------

DROP TABLE IF EXISTS stamp;

CREATE TABLE stamp (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  code            VARCHAR(64) NOT NULL UNIQUE,            -- 志号/编码
  name            VARCHAR(255) NOT NULL,
  country         VARCHAR(64) NOT NULL,
  year            INT NOT NULL,
  face_value      VARCHAR(64) DEFAULT NULL,               -- 面值
  type            VARCHAR(64) DEFAULT NULL,               -- 纪念票/特种票...
  perforation     VARCHAR(64) DEFAULT NULL,               -- 齿孔度数
  printing_tech   VARCHAR(128) DEFAULT NULL,              -- 印刷工艺
  theme           VARCHAR(128) DEFAULT NULL,              -- 人物/风景等
  background      TEXT,                                   -- 发行背景
  designer        VARCHAR(128) DEFAULT NULL,
  printer         VARCHAR(128) DEFAULT NULL,              -- 印刷厂
  issue_org       VARCHAR(128) DEFAULT NULL,              -- 发行机构
  issue_date      DATE DEFAULT NULL,
  status          VARCHAR(16) NOT NULL DEFAULT 'ENABLED',
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted         TINYINT NOT NULL DEFAULT 0,
  KEY idx_stamp_country_year (country, year),
  KEY idx_stamp_theme (theme),
  KEY idx_stamp_type (type),
  KEY idx_stamp_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 3. 图片与标注（数字化采集模块）
-- -------------------------

DROP TABLE IF EXISTS image_annotation;
DROP TABLE IF EXISTS stamp_image;
DROP TABLE IF EXISTS stamp_attachment;

CREATE TABLE stamp_image (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  image_type      VARCHAR(16) NOT NULL,        -- FRONT/BACK/DETAIL
  image_url       VARCHAR(512) NOT NULL,       -- 原图路径
  thumb_url       VARCHAR(512) DEFAULT NULL,   -- 缩略图路径
  dpi             INT DEFAULT NULL,            -- 分辨率
  width_px        INT DEFAULT NULL,
  height_px       INT DEFAULT NULL,
  file_size       BIGINT DEFAULT NULL,
  sort_no         INT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_stamp_image_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  KEY idx_stamp_image_stamp (stamp_id, image_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE image_annotation (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  image_id        BIGINT NOT NULL,
  ann_type        VARCHAR(16) NOT NULL,        -- POINT / POLYGON / RECT
  ann_data_json   JSON NOT NULL,               -- 坐标数据
  ann_text        VARCHAR(255) NOT NULL,       -- 标注内容
  color           VARCHAR(32) DEFAULT NULL,
  created_by      BIGINT DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ann_image FOREIGN KEY (image_id) REFERENCES stamp_image(id),
  KEY idx_ann_image (image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stamp_attachment (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  file_name       VARCHAR(255) NOT NULL,
  file_url        VARCHAR(512) NOT NULL,
  file_type       VARCHAR(64) DEFAULT NULL,    -- pdf/doc/image...
  file_size       BIGINT DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_attach_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  KEY idx_attach_stamp (stamp_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 4. 分类与检索模块
-- -------------------------

DROP TABLE IF EXISTS stamp_category_rel;
DROP TABLE IF EXISTS category;

CREATE TABLE category (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id       BIGINT NOT NULL DEFAULT 0,   -- 0=根节点
  category_name   VARCHAR(128) NOT NULL,
  category_level  INT NOT NULL DEFAULT 1,
  sort_no         INT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE stamp_category_rel (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  category_id     BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_stamp_category (stamp_id, category_id),
  CONSTRAINT fk_rel_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  CONSTRAINT fk_rel_category FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 5. 鉴赏模块
-- -------------------------

DROP TABLE IF EXISTS stamp_appreciation;

CREATE TABLE stamp_appreciation (
  id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id            BIGINT NOT NULL,
  appreciation_points TEXT,                   -- 鉴赏要点
  value_analysis      TEXT,                   -- 收藏价值分析
  rarity_level        VARCHAR(32) DEFAULT NULL,
  watermark_download  TINYINT NOT NULL DEFAULT 1, -- 是否支持水印下载
  created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_app_stamp (stamp_id),
  CONSTRAINT fk_app_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 6. 用户收藏与互动模块
-- -------------------------

DROP TABLE IF EXISTS comment_like;
DROP TABLE IF EXISTS comment_reply;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS favorite_item;
DROP TABLE IF EXISTS favorite_folder;
DROP TABLE IF EXISTS folder_share;

CREATE TABLE favorite_folder (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL,
  folder_name     VARCHAR(128) NOT NULL,
  is_public       TINYINT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_folder_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  KEY idx_folder_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE favorite_item (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  folder_id       BIGINT NOT NULL,
  stamp_id        BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_folder_stamp (folder_id, stamp_id),
  CONSTRAINT fk_fav_item_folder FOREIGN KEY (folder_id) REFERENCES favorite_folder(id),
  CONSTRAINT fk_fav_item_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE folder_share (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  folder_id       BIGINT NOT NULL,
  share_code      VARCHAR(64) NOT NULL UNIQUE,
  expire_at       DATETIME DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_share_folder FOREIGN KEY (folder_id) REFERENCES favorite_folder(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE comment (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  user_id         BIGINT NOT NULL,
  content         TEXT NOT NULL,
  status          VARCHAR(16) NOT NULL DEFAULT 'NORMAL', -- NORMAL/HIDDEN
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_comment_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  KEY idx_comment_stamp (stamp_id),
  KEY idx_comment_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE comment_reply (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  comment_id      BIGINT NOT NULL,
  user_id         BIGINT NOT NULL,
  content         TEXT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reply_comment FOREIGN KEY (comment_id) REFERENCES comment(id),
  CONSTRAINT fk_reply_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  KEY idx_reply_comment (comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE comment_like (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  comment_id      BIGINT NOT NULL,
  user_id         BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_comment_like (comment_id, user_id),
  CONSTRAINT fk_like_comment FOREIGN KEY (comment_id) REFERENCES comment(id),
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 7. 市场信息模块
-- -------------------------

DROP TABLE IF EXISTS price_alert;
DROP TABLE IF EXISTS auction_notice;
DROP TABLE IF EXISTS market_price;

CREATE TABLE market_price (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  price_type      VARCHAR(32) NOT NULL,       -- NEW/OLD/FULL/GOOD...
  reference_price DECIMAL(12,2) NOT NULL,
  currency        VARCHAR(16) NOT NULL DEFAULT 'CNY',
  record_date     DATE NOT NULL,
  source          VARCHAR(128) DEFAULT NULL,  -- 数据来源
  remark          VARCHAR(255) DEFAULT NULL,
  created_by      BIGINT DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_market_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  KEY idx_market_stamp_date (stamp_id, record_date),
  KEY idx_market_type (price_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE auction_notice (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  title           VARCHAR(255) NOT NULL,
  auction_house   VARCHAR(128) DEFAULT NULL,
  auction_time    DATETIME DEFAULT NULL,
  lot_list        TEXT,                        -- 拍品清单（可先文本）
  detail_url      VARCHAR(512) DEFAULT NULL,
  status          VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED',
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE price_alert (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL,
  stamp_id        BIGINT NOT NULL,
  target_price    DECIMAL(12,2) NOT NULL,
  direction       VARCHAR(8) NOT NULL,         -- UP/DOWN
  notify_channel  VARCHAR(16) NOT NULL DEFAULT 'SITE', -- SITE/EMAIL
  status          VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_alert_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_alert_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id),
  KEY idx_alert_user (user_id),
  KEY idx_alert_stamp (stamp_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 8. 系统管理（导入导出/备份记录）
-- -------------------------

DROP TABLE IF EXISTS backup_record;
DROP TABLE IF EXISTS import_task;

CREATE TABLE import_task (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_name       VARCHAR(128) NOT NULL,
  file_name       VARCHAR(255) DEFAULT NULL,
  total_count     INT NOT NULL DEFAULT 0,
  success_count   INT NOT NULL DEFAULT 0,
  fail_count      INT NOT NULL DEFAULT 0,
  status          VARCHAR(16) NOT NULL DEFAULT 'RUNNING', -- RUNNING/SUCCESS/FAILED
  error_msg       TEXT,
  created_by      BIGINT DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  finished_at     DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE backup_record (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  backup_type     VARCHAR(16) NOT NULL,      -- FULL/INCREMENT
  db_file_path    VARCHAR(512) DEFAULT NULL,
  image_path      VARCHAR(512) DEFAULT NULL,
  status          VARCHAR(16) NOT NULL DEFAULT 'SUCCESS',
  remark          VARCHAR(255) DEFAULT NULL,
  created_by      BIGINT DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- 9. 初始数据（可选）
-- -------------------------

INSERT INTO role(role_code, role_name) VALUES
('ADMIN', '管理员'),
('USER', '普通用户'),
('VISITOR', '游客');

INSERT INTO permission(perm_code, perm_name, module_name) VALUES
('stamp:add', '新增邮票', 'stamp'),
('stamp:edit', '编辑邮票', 'stamp'),
('stamp:delete', '删除邮票', 'stamp'),
('stamp:view', '查看邮票', 'stamp'),
('market:edit', '维护市场信息', 'market'),
('user:manage', '管理用户', 'admin');

-- 默认管理员账号：admin / 123456（建议启动后改密码）
-- 这里先写明文，后续你接入 Sa-Token 时可以改成加密存储
INSERT INTO sys_user(username, password, nickname, status)
VALUES ('admin', '123456', '系统管理员', 'ENABLED');

-- 给 admin 绑定 ADMIN 角色
INSERT INTO user_role(user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, role r
WHERE u.username='admin' AND r.role_code='ADMIN';

-- 给 ADMIN 绑定所有权限
INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.role_code='ADMIN';