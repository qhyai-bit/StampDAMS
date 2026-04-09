package cn.stamp.modules.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("market_price")
public class MarketPrice {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 邮票ID
     */
    private Long stampId;

    /**
     * 品相类型 (例如: NEW-全新, OLD-旧品, FULL-全品, GOOD-好品等)
     */
    private String priceType;

    /**
     * 参考价格
     */
    private BigDecimal referencePrice;

    /**
     * 货币单位
     */
    private String currency;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 价格来源
     */
    private String source;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
