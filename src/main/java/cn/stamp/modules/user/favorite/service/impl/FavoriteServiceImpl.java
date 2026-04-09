package cn.stamp.modules.user.favorite.service.impl;

import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.service.StampService;
import cn.stamp.modules.user.favorite.dto.FavoriteAddDTO;
import cn.stamp.modules.user.favorite.dto.FavoriteFolderSaveDTO;
import cn.stamp.modules.user.favorite.entity.FavoriteFolder;
import cn.stamp.modules.user.favorite.entity.FavoriteItem;
import cn.stamp.modules.user.favorite.mapper.FavoriteFolderMapper;
import cn.stamp.modules.user.favorite.mapper.FavoriteItemMapper;
import cn.stamp.modules.user.favorite.service.FavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteFolderMapper folderMapper;
    private final FavoriteItemMapper itemMapper;
    private final StampService stampService;

    /**
     * 创建收藏夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFolder(Long userId, FavoriteFolderSaveDTO dto) {
        FavoriteFolder row = new FavoriteFolder();
        row.setUserId(userId);
        row.setFolderName(dto.getFolderName());
        // 默认为私有
        row.setIsPublic(dto.getIsPublic() == null ? 0 : dto.getIsPublic());
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        folderMapper.insert(row);
        return row.getId();
    }

    /**
     * 更新收藏夹信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFolder(Long userId, Long folderId, FavoriteFolderSaveDTO dto) {
        FavoriteFolder folder = folderMapper.selectById(folderId);
        requireOwner(userId, folder);
        folder.setFolderName(dto.getFolderName());
        if (dto.getIsPublic() != null) {
            folder.setIsPublic(dto.getIsPublic());
        }
        folder.setUpdatedAt(LocalDateTime.now());
        folderMapper.updateById(folder);
    }

    /**
     * 删除收藏夹及其下所有收藏项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFolder(Long userId, Long folderId) {
        FavoriteFolder folder = folderMapper.selectById(folderId);
        requireOwner(userId, folder);
        // 先删除关联的收藏项
        itemMapper.delete(new LambdaQueryWrapper<FavoriteItem>().eq(FavoriteItem::getFolderId, folderId));
        // 再删除收藏夹
        folderMapper.deleteById(folderId);
    }

    /**
     * 查询用户的收藏夹列表
     */
    @Override
    public List<FavoriteFolder> listFolders(Long userId) {
        return folderMapper.selectList(new LambdaQueryWrapper<FavoriteFolder>()
                .eq(FavoriteFolder::getUserId, userId)
                .orderByDesc(FavoriteFolder::getUpdatedAt)
                .orderByDesc(FavoriteFolder::getId));
    }

    /**
     * 添加邮票到收藏夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToFolder(Long userId, Long folderId, FavoriteAddDTO dto) {
        FavoriteFolder folder = folderMapper.selectById(folderId);
        requireOwner(userId, folder);
        // 校验邮票是否存在
        if (stampService.findById(dto.getStampId()) == null) {
            throw new IllegalArgumentException("邮票不存在");
        }
        FavoriteItem row = new FavoriteItem();
        row.setFolderId(folderId);
        row.setStampId(dto.getStampId());
        row.setCreatedAt(LocalDateTime.now());
        // 若重复插入会触发唯一约束异常，捕获后转换为业务异常提示
        try {
            itemMapper.insert(row);
        } catch (Exception e) {
            throw new IllegalArgumentException("该邮票已在收藏夹中");
        }
    }

    /**
     * 从收藏夹移除邮票
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromFolder(Long userId, Long folderId, Long stampId) {
        FavoriteFolder folder = folderMapper.selectById(folderId);
        requireOwner(userId, folder);
        itemMapper.delete(new LambdaQueryWrapper<FavoriteItem>()
                .eq(FavoriteItem::getFolderId, folderId)
                .eq(FavoriteItem::getStampId, stampId));
    }

    /**
     * 分页查询收藏夹内的邮票详情
     */
    @Override
    public Page<Stamp> pageFolderItems(Long userId, Long folderId, Integer pageNum, Integer pageSize) {
        FavoriteFolder folder = folderMapper.selectById(folderId);
        requireOwner(userId, folder);
        
        // 查询收藏项分页数据
        Page<FavoriteItem> page = new Page<>(pageNum, pageSize);
        Page<FavoriteItem> itemPage = itemMapper.selectPage(page, new LambdaQueryWrapper<FavoriteItem>()
                .eq(FavoriteItem::getFolderId, folderId)
                .orderByDesc(FavoriteItem::getCreatedAt)
                .orderByDesc(FavoriteItem::getId));

        // 组装邮票详情列表
        List<Stamp> stamps = new ArrayList<>();
        for (FavoriteItem it : itemPage.getRecords()) {
            Stamp s = stampService.findById(it.getStampId());
            if (s != null) {
                stamps.add(s);
            }
        }

        // 构建返回的分页对象
        Page<Stamp> out = new Page<>(pageNum, pageSize);
        out.setTotal(itemPage.getTotal());
        out.setRecords(stamps);
        return out;
    }

    /**
     * 校验用户是否为收藏夹所有者
     */
    private static void requireOwner(Long userId, FavoriteFolder folder) {
        if (folder == null) {
            throw new IllegalArgumentException("收藏夹不存在");
        }
        if (!userId.equals(folder.getUserId())) {
            throw new IllegalArgumentException("无权限操作该收藏夹");
        }
    }
}

