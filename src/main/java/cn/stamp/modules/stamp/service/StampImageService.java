package cn.stamp.modules.stamp.service;

import cn.stamp.modules.stamp.vo.StampImageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StampImageService {

    StampImageVO upload(Long stampId, String imageType, MultipartFile file);

    List<StampImageVO> listByStampId(Long stampId);

    void delete(Long imageId);
}
