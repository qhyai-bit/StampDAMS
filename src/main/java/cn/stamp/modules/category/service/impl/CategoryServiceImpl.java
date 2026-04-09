package cn.stamp.modules.category.service.impl;

import cn.stamp.modules.category.dto.CategorySaveDTO;
import cn.stamp.modules.category.entity.Category;
import cn.stamp.modules.category.mapper.CategoryMapper;
import cn.stamp.modules.category.mapper.StampCategoryRelMapper;
import cn.stamp.modules.category.service.CategoryService;
import cn.stamp.modules.category.vo.CategoryNodeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final StampCategoryRelMapper relMapper;

    /**
     * 创建新分类
     *
     * @param dto 分类保存数据传输对象
     * @return 新创建分类的ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CategorySaveDTO dto) {
        // 确定父级ID，默认为0（根节点）
        Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
        // 计算当前分类层级：根节点为1级，子节点为父节点层级+1
        int level = parentId == 0L ? 1 : calcLevel(parentId);

        Category row = new Category();
        row.setParentId(parentId);
        row.setCategoryName(dto.getCategoryName());
        // 排序号默认为0
        row.setSortNo(dto.getSortNo() == null ? 0 : dto.getSortNo());
        row.setCategoryLevel(level);
        
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        
        categoryMapper.insert(row);
        return row.getId();
    }

    /**
     * 更新分类信息
     *
     * @param id  分类ID
     * @param dto 分类保存数据传输对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CategorySaveDTO dto) {
        Category c = categoryMapper.selectById(id);
        if (c == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        
        c.setCategoryName(dto.getCategoryName());
        if (dto.getSortNo() != null) {
            c.setSortNo(dto.getSortNo());
        }
        c.setUpdatedAt(LocalDateTime.now());
        
        categoryMapper.updateById(c);
    }

    /**
     * 删除分类
     * 若存在子分类或关联印章，则禁止删除
     *
     * @param id 分类ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查是否存在子节点，若有则禁止删除以维护树结构完整性
        Long childCnt = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id));
        if (childCnt != null && childCnt > 0) {
            throw new IllegalArgumentException("请先删除子分类");
        }
        
        // 删除该分类与印章的关联关系
        relMapper.delete(new LambdaQueryWrapper<cn.stamp.modules.category.entity.StampCategoryRel>()
                .eq(cn.stamp.modules.category.entity.StampCategoryRel::getCategoryId, id));
        
        // 删除分类本身
        categoryMapper.deleteById(id);
    }

    /**
     * 获取分类树形结构
     *
     * @return 分类节点列表（树形结构）
     */
    @Override
    public List<CategoryNodeVO> tree() {
        // 查询所有分类，按层级、排序号和ID升序排列
        List<Category> all = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getCategoryLevel)
                .orderByAsc(Category::getSortNo)
                .orderByAsc(Category::getId));
        
        // 构建ID到节点VO的映射
        Map<Long, CategoryNodeVO> map = new LinkedHashMap<>();
        for (Category c : all) {
            map.put(c.getId(), CategoryNodeVO.builder()
                    .id(c.getId())
                    .parentId(c.getParentId())
                    .categoryName(c.getCategoryName())
                    .categoryLevel(c.getCategoryLevel())
                    .sortNo(c.getSortNo())
                    .children(new ArrayList<>())
                    .build());
        }
        
        // 组装树形结构
        List<CategoryNodeVO> roots = new ArrayList<>();
        for (CategoryNodeVO node : map.values()) {
            Long pid = node.getParentId() == null ? 0L : node.getParentId();
            if (pid == 0L) {
                // 根节点直接加入结果集
                roots.add(node);
            } else {
                CategoryNodeVO parent = map.get(pid);
                if (parent != null) {
                    // 找到父节点，将当前节点加入父节点的子节点列表
                    parent.getChildren().add(node);
                } else {
                    // 父节点不存在时，降级作为根节点处理
                    roots.add(node);
                }
            }
        }
        return roots;
    }

    /**
     * 计算分类层级
     *
     * @param parentId 父分类ID
     * @return 分类层级，最大不超过10级
     */
    private int calcLevel(Long parentId) {
        Category p = categoryMapper.selectById(parentId);
        if (p == null) {
            return 1;
        }
        int lvl = p.getCategoryLevel() == null ? 1 : p.getCategoryLevel();
        // 限制最大层级为10，防止过深嵌套
        return Math.min(lvl + 1, 10);
    }
}

