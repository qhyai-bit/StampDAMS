package cn.stamp.modules.user.favorite.service;

import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.user.favorite.dto.FavoriteAddDTO;
import cn.stamp.modules.user.favorite.dto.FavoriteFolderSaveDTO;
import cn.stamp.modules.user.favorite.entity.FavoriteFolder;
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
}

