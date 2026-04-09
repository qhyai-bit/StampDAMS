package cn.stamp.modules.user.favorite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("folder_share")
public class FolderShare {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件夹ID
     */
    private Long folderId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
