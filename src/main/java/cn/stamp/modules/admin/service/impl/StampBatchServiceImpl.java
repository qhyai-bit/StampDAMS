package cn.stamp.modules.admin.service.impl;

import cn.stamp.modules.admin.service.StampBatchService;
import cn.stamp.modules.admin.vo.ImportResultVO;
import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.mapper.StampMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮票批量处理服务实现类
 * 提供邮票数据的 CSV 导入和导出功能
 */
@Service
@RequiredArgsConstructor
public class StampBatchServiceImpl implements StampBatchService {

    private final StampMapper stampMapper;

    /**
     * 从 CSV 文件导入邮票数据
     * 支持新增和更新操作，根据 code 字段判断是否存在
     *
     * @param file 上传的 CSV 文件
     * @return 导入结果统计信息
     * @throws IOException 文件读取异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO importStampsCsv(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return new ImportResultVO(0, 0, 0, "CSV 文件为空");
        }

        int total = 0;
        int success = 0;
        int failed = 0;

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord r : parser) {
                total++;
                try {
                    String code = get(r, "code");
                    String name = get(r, "name");
                    String country = get(r, "country");
                    Integer year = parseInt(get(r, "year"));

                    if (isBlank(code) || isBlank(name) || isBlank(country) || year == null) {
                        throw new IllegalArgumentException("code/name/country/year 必填");
                    }

                    // 查询是否已存在该编码的邮票
                    Stamp stamp = stampMapper.selectOne(new LambdaQueryWrapper<Stamp>()
                            .eq(Stamp::getCode, code)
                            .last("limit 1"));

                    LocalDateTime now = LocalDateTime.now();
                    if (stamp == null) {
                        stamp = new Stamp();
                        stamp.setCode(code);
                        stamp.setCreatedAt(now);
                    }
                    // 更新或设置邮票属性
                    stamp.setName(name);
                    stamp.setCountry(country);
                    stamp.setYear(year);
                    stamp.setFaceValue(get(r, "faceValue"));
                    stamp.setType(get(r, "type"));
                    stamp.setPerforation(get(r, "perforation"));
                    stamp.setPrintingTech(get(r, "printingTech"));
                    stamp.setTheme(get(r, "theme"));
                    stamp.setBackground(get(r, "background"));
                    stamp.setDesigner(get(r, "designer"));
                    stamp.setPrinter(get(r, "printer"));
                    stamp.setStatus(defaultIfBlank(get(r, "status"), "ENABLED"));
                    stamp.setUpdatedAt(now);

                    // 根据 ID 是否存在决定插入或更新
                    if (stamp.getId() == null) {
                        stampMapper.insert(stamp);
                    } else {
                        stampMapper.updateById(stamp);
                    }
                    success++;
                } catch (Exception e) {
                    failed++;
                }
            }
        }

        return new ImportResultVO(total, success, failed, "导入完成");
    }

    /**
     * 导出所有邮票数据为 CSV 文件
     * 按年份降序、编码升序排列
     *
     * @param response HTTP 响应对象，用于输出 CSV 文件流
     * @throws IOException 文件写入异常
     */
    @Override
    public void exportStampsCsv(HttpServletResponse response) throws IOException {
        List<Stamp> list = stampMapper.selectList(new LambdaQueryWrapper<Stamp>()
                .orderByDesc(Stamp::getYear)
                .orderByAsc(Stamp::getCode));

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"stamps.csv\"");

        try (OutputStream os = response.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("code", "name", "country", "year", "faceValue", "type", "perforation",
                             "printingTech", "theme", "background", "designer", "printer", "status")
                     .build())) {
            for (Stamp s : list) {
                printer.printRecord(
                        s.getCode(),
                        s.getName(),
                        s.getCountry(),
                        s.getYear(),
                        s.getFaceValue(),
                        s.getType(),
                        s.getPerforation(),
                        s.getPrintingTech(),
                        s.getTheme(),
                        s.getBackground(),
                        s.getDesigner(),
                        s.getPrinter(),
                        s.getStatus()
                );
            }
            printer.flush();
        }
    }

    /**
     * 安全获取 CSV 记录中的字段值
     *
     * @param r   CSV 记录
     * @param key 字段名
     * @return 字段值，若不存在或出错则返回 null
     */
    private static String get(CSVRecord r, String key) {
        try {
            return r.isMapped(key) ? r.get(key) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将字符串转换为整数
     *
     * @param s 待转换的字符串
     * @return 转换后的整数，若为空或格式错误则返回 null
     */
    private static Integer parseInt(String s) {
        if (isBlank(s)) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断字符串是否为空或空白
     *
     * @param s 待判断的字符串
     * @return 若为 null 或仅包含空白字符则返回 true
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 若字符串为空或空白，则返回默认值
     *
     * @param s   原始字符串
     * @param def 默认值
     * @return 原始字符串（若非空）或默认值
     */
    private static String defaultIfBlank(String s, String def) {
        return isBlank(s) ? def : s;
    }
}

