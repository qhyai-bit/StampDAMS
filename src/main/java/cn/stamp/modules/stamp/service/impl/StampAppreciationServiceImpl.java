package cn.stamp.modules.stamp.service.impl;

import cn.stamp.modules.stamp.dto.StampAppreciationSaveDTO;
import cn.stamp.modules.stamp.entity.StampAppreciation;
import cn.stamp.modules.stamp.mapper.StampAppreciationMapper;
import cn.stamp.modules.stamp.service.StampAppreciationService;
import cn.stamp.modules.stamp.service.StampService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StampAppreciationServiceImpl implements StampAppreciationService {

    private final StampAppreciationMapper appreciationMapper;
    private final StampService stampService;

    /**
     * 根据邮票ID获取鉴赏信息
     *
     * @param stampId 邮票ID
     * @return 邮票鉴赏实体，若不存在则返回null
     */
    @Override
    public StampAppreciation getByStampId(Long stampId) {
        return appreciationMapper.selectOne(new LambdaQueryWrapper<StampAppreciation>()
                .eq(StampAppreciation::getStampId, stampId)
                .last("limit 1"));
    }

    /**
     * 保存或更新邮票鉴赏信息
     * <p>
     * 如果邮票不存在，则抛出异常。
     * 如果该邮票的鉴赏信息已存在，则进行更新操作；否则进行新增操作。
     *
     * @param stampId 邮票ID
     * @param dto     保存DTO，包含鉴赏积分、价值分析、稀有度等级和水印下载状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long stampId, StampAppreciationSaveDTO dto) {
        // 校验邮票是否存在
        if (stampService.findById(stampId) == null) {
            throw new IllegalArgumentException("邮票不存在");
        }

        // 查询现有的鉴赏记录
        StampAppreciation existing = getByStampId(stampId);
        LocalDateTime now = LocalDateTime.now();

        if (existing == null) {
            // 新增记录
            StampAppreciation row = new StampAppreciation();
            row.setStampId(stampId);
            row.setAppreciationPoints(dto.getAppreciationPoints());
            row.setValueAnalysis(dto.getValueAnalysis());
            row.setRarityLevel(dto.getRarityLevel());
            // 水印下载状态默认为1（开启），除非显式指定其他值
            row.setWatermarkDownload(dto.getWatermarkDownload() == null ? 1 : dto.getWatermarkDownload());
            row.setCreatedAt(now);
            row.setUpdatedAt(now);
            appreciationMapper.insert(row);
        } else {
            // 更新记录
            existing.setAppreciationPoints(dto.getAppreciationPoints());
            existing.setValueAnalysis(dto.getValueAnalysis());
            existing.setRarityLevel(dto.getRarityLevel());
            // 仅当DTO中提供了水印下载状态时才更新，否则保持原值
            if (dto.getWatermarkDownload() != null) {
                existing.setWatermarkDownload(dto.getWatermarkDownload());
            }
            existing.setUpdatedAt(now);
            appreciationMapper.updateById(existing);
        }
    }
}
