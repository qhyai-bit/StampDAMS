package cn.stamp.modules.user.favorite.service;

import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.user.favorite.dto.FavoriteAddDTO;
import cn.stamp.modules.user.favorite.dto.FavoriteFolderSaveDTO;
import cn.stamp.modules.user.favorite.dto.ShareCreateDTO;
import cn.stamp.modules.user.favorite.entity.FavoriteFolder;
import cn.stamp.modules.user.favorite.vo.ShareInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface FavoriteService {

    Long createFolder(Long userId, FavoriteFolderSaveDTO dto);

    void updateFolder(Long userId, Long folderId, FavoriteFolderSaveDTO dto);

    void deleteFolder(Long userId, Long folderId);

    List<FavoriteFolder> listFolders(Long userId);

    void addToFolder(Long userId, Long folderId, FavoriteAddDTO dto);

    void removeFromFolder(Long userId, Long folderId, Long stampId);

    Page<Stamp> pageFolderItems(Long userId, Long folderId, Integer pageNum, Integer pageSize);

    // 生成分享码
    ShareInfoVO generateShare(Long userId, Long folderId, ShareCreateDTO dto);

    // 通过分享码获取收藏夹内容
    FavoriteFolder getFolderByShareCode(String shareCode);
    List<Stamp> getItemsByShareCode(String shareCode);

}

