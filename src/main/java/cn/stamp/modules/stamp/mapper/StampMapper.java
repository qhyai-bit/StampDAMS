package cn.stamp.modules.stamp.mapper;

import cn.stamp.modules.stamp.entity.Stamp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 印章 Mapper 接口
 */
public interface StampMapper extends BaseMapper<Stamp> {
}

