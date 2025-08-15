package com.xigua.client.service;

import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.MultipartFileDTO;
import com.xigua.domain.enums.MediaTypeEnum;
import com.xigua.api.service.FileService;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @ClassName FileServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 13:54
 */
@Slf4j
@Service
@DubboService
public class FileServiceImpl implements FileService {
    // 允许上传的文件后缀名
    private final static Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "png");

    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.proxy}")
    private String proxy;

    @Autowired
    private Sequence sequence;
    @Autowired
    private MinioClient minioClient;


    /**
     * 上传文件
     * @author wangjinfei
     * @date 2025/5/8 13:53
     * @param multipartFileDTO
     * @return String
     */
    @Override
    public String upload(MultipartFileDTO multipartFileDTO) {
        String originalFilename = multipartFileDTO.getOriginalFilename();
        String contentType = multipartFileDTO.getContentType();
        byte[] bytes = multipartFileDTO.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);

        // 校验文件
        checkFile(multipartFileDTO);

        try {
            // 当前日期作为目录
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String dateStr = currentDate.format(formatter);

            // 文件key = 日期目录 + 序号_文件名 （序号防止目录下有重复文件名）
            String fileKey = dateStr + "/" + sequence.nextNo() + "_" + originalFilename ;

            // minio上传
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .contentType(contentType)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());

            // 用代理前缀拼接访问地址  nginx做了代理会直接访问minio服务器
            String url = proxy + "/" + bucketName + "/" + fileKey;
            return url;
        } catch (Exception e) {
            log.error("上传失败", e);
            return "上传失败";
        }
    }

    /**
     * 校验文件
     * @author wangjinfei
     * @date 2025/6/1 3:16
     * @param multipartFileDTO
    */
    private void checkFile(MultipartFileDTO multipartFileDTO){
        String originalFilename = multipartFileDTO.getOriginalFilename();
        String contentType = multipartFileDTO.getContentType();
        String suffix = FilenameUtils.getExtension(originalFilename);

        // 校验文件大小
        if(multipartFileDTO.getSize() == 0){
            throw new BusinessException("文件为空");
        }

        // 校验文件名称，防止伪装脚本(111.php.jpg)
        if (originalFilename.matches("(?i).+\\.(php|jsp|asp|aspx|exe|sh|html|js)(\\..*)?")) {
            throw new BusinessException("非法文件名");
        }

        // 检查文件后缀名
        if (!ALLOWED_EXTENSIONS.contains(suffix)) {
            throw new BusinessException("不允许的文件类型");
        }

        // 检测媒体类型
        MediaTypeEnum mediaTypeE = MediaTypeEnum.getBySuffix(suffix);
        if(mediaTypeE == null){
            throw new BusinessException("不允许的文件类型");
        }
        if(mediaTypeE.getContentType().equals(contentType) == false){
            throw new BusinessException("不允许的文件类型");
        }

        /**
         * todo 文件内容检测，暂时不做
         * 一般云厂商会提供文件内容检测服务，阿里云的oss、腾讯云cos、华为云obs、亚马逊云s3
        */
    }
}
