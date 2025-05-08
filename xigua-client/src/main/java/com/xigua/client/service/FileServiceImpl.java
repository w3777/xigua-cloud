package com.xigua.client.service;

import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.MultipartFileDTO;
import com.xigua.service.FileService;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName FileServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 13:54
 */
@Slf4j
@RequiredArgsConstructor
@DubboService
public class FileServiceImpl implements FileService {
    private final Sequence sequence;
    private final MinioClient minioClient;
    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.endpoint}")
    private String endpoint;

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

            String url = endpoint + "/" + bucketName + "/" + fileKey;
            return url;
        } catch (Exception e) {
            log.error("上传失败", e);
            return "上传失败";
        }
    }
}
