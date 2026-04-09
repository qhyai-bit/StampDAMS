package cn.stamp.modules.stamp.dto;

import lombok.Data;

@Data
public class StampAppreciationSaveDTO {

    /** 鉴赏要点 */
    private String appreciationPoints;
    /** 价值分析 */
    private String valueAnalysis;
    /** 稀有度等级 */
    private String rarityLevel;
    /** 是否支持水印下载：0-否，1-是 */
    private Integer watermarkDownload;
}
