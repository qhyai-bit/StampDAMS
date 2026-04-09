package cn.stamp.modules.user.favorite.mapper;

import cn.stamp.modules.user.favorite.entity.FavoriteItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏项 Mapper 接口
 *
 * @author StampDAMS
 */
@Mapper
public interface FavoriteItemMapper extends BaseMapper<FavoriteItem> {
}

