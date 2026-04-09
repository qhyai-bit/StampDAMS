-- 数字化采集模块：图像与标注（若已执行完整建库脚本可跳过）
USE stamp_dams;

CREATE TABLE IF NOT EXISTS stamp_image (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  stamp_id        BIGINT NOT NULL,
  image_type      VARCHAR(16) NOT NULL,
  image_url       VARCHAR(512) NOT NULL,
  thumb_url       VARCHAR(512) DEFAULT NULL,
  dpi             INT DEFAULT NULL,
  width_px        INT DEFAULT NULL,
  height_px       INT DEFAULT NULL,
  file_size       BIGINT DEFAULT NULL,
  sort_no         INT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_stamp_image_stamp (stamp_id, image_type),
  CONSTRAINT fk_stamp_image_stamp FOREIGN KEY (stamp_id) REFERENCES stamp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS image_annotation (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  image_id        BIGINT NOT NULL,
  ann_type        VARCHAR(16) NOT NULL,
  ann_data_json   JSON NOT NULL,
  ann_text        VARCHAR(255) NOT NULL,
  color           VARCHAR(32) DEFAULT NULL,
  created_by      BIGINT DEFAULT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ann_image (image_id),
  CONSTRAINT fk_ann_image FOREIGN KEY (image_id) REFERENCES stamp_image(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
