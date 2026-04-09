package cn.stamp.modules.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MarketPriceCreateDTO {

    /**
     * 邮票ID
     */
    @NotNull(message = "邮票ID不能为空")
    private Long stampId;

    /**
     * 价格类型
     */
    @NotBlank(message = "价格类型不能为空")
    private String priceType;

    /**
     * 参考价
     */
    @NotNull(message = "参考价不能为空")
    private BigDecimal referencePrice;

    /**
     * 货币单位，默认CNY
     */
    private String currency = "CNY";

    /**
     * 记录日期
     */
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 备注
     */
    private String remark;
}
