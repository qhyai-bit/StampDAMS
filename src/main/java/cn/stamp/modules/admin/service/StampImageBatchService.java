package cn.stamp.modules.admin.service;

import cn.stamp.modules.admin.vo.ImportResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StampImageBatchService {

    /**
     * 批量导入邮票图片（ZIP格式）
     * <p>
     * ZIP包内文件结构约定：
     * 1. 目录结构模式：{邮票编码}/front.jpg 和 {邮票编码}/back.jpg
     * 2. 文件名模式：文件名中包含 "front" 或 "back" 关键字以区分正反面
     * </p>
     *
     * @param zipFile 包含邮票图片的ZIP压缩文件
     * @return 导入结果，包含成功与失败的详细信息
     * @throws IOException 当文件读取或处理发生错误时抛出
     */
    ImportResultVO importImagesZip(MultipartFile zipFile) throws IOException;
}

