-- 注册/权限模块依赖的 RBAC 基础表（若已执行完整建库脚本可跳过）
USE stamp_dams;

CREATE TABLE IF NOT EXISTS role (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code       VARCHAR(64) NOT NULL UNIQUE,
  role_name       VARCHAR(64) NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_role (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL,
  role_id         BIGINT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_user_role_user (user_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO role(role_code, role_name) VALUES
('ADMIN', '管理员'),
('USER', '普通用户'),
('VISITOR', '游客');

