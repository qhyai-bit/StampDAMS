package cn.stamp.modules.category.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类节点视图对象
 * 用于构建树形结构的分类数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryNodeVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 父分类ID，根节点为null或0
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类层级，从1开始
     */
    private Integer categoryLevel;

    /**
     * 排序号，数值越小越靠前
     */
    private Integer sortNo;

    /**
     * 子分类列表
     */
    @Builder.Default
    private List<CategoryNodeVO> children = new ArrayList<>();
}

