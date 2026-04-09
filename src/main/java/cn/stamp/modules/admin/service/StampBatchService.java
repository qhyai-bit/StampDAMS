package cn.stamp.modules.admin.service;

import cn.stamp.modules.admin.vo.ImportResultVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StampBatchService {

    ImportResultVO importStampsCsv(MultipartFile file) throws IOException;

    void exportStampsCsv(HttpServletResponse response) throws IOException;
}

