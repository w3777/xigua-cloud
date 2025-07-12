package com.xigua.client.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

/**
 * @ClassName FileUtil
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/12 11:09
 */
public class FileUtil {

    /**
     * BufferedImage 转 InputStream
     * @author wangjinfei
     * @date 2025/7/12 11:26
     * @param image
     * @param formatName
     * @return InputStream
    */
    public static InputStream convertToInputStream(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, os);  // 将图像写入输出流
        return new ByteArrayInputStream(os.toByteArray());  // 转为输入流
    }

    /**
     * InputStream 转 MultipartFile
     * @author wangjinfei
     * @date 2025/7/12 23:14
     * @param inputStream
     * @param fileName
     * @return MultipartFile
    */
    public static MultipartFile convertToMultipartFile(InputStream inputStream, String fileName) throws Exception {
        String contentType = MediaTypeFactory.getMediaType(fileName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM)
                .toString();

        InputStreamToMultipartFile multipartFile = new InputStreamToMultipartFile(
                inputStream,
                "file", // 表单字段名
                fileName,
                contentType
        );

        return multipartFile;
    }

}
