package cn.stamp.modules.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 导入结果视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultVO {
    /**
     * 总记录数
     */
    private int total;

    /**
     * 成功记录数
     */
    private int success;

    /**
     * 失败记录数
     */
    private int failed;

    /**
     * 导入结果消息
     */
    private String message;
}

