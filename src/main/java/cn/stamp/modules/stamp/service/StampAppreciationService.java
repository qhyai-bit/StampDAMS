package cn.stamp.modules.stamp.service;

import cn.stamp.modules.stamp.dto.StampAppreciationSaveDTO;
import cn.stamp.modules.stamp.entity.StampAppreciation;

public interface StampAppreciationService {

    StampAppreciation getByStampId(Long stampId);

    void saveOrUpdate(Long stampId, StampAppreciationSaveDTO dto);
}
