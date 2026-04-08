package cn.stamp.modules.stamp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stamp")
public class Stamp {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;
    private String name;
    private String country;
    private Integer year;
    private String faceValue;
    private String type;
    private String perforation;
    private String printingTech;
    private String theme;
    private String background;
    private String designer;
    private String printer;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

