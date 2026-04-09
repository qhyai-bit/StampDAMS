package cn.stamp.modules.user.favorite.mapper;

import cn.stamp.modules.user.favorite.entity.FavoriteFolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏文件夹 Mapper 接口
 */
@Mapper
public interface FavoriteFolderMapper extends BaseMapper<FavoriteFolder> {
}

