package com.xigua.client.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @ClassName InputStreamToMultipartFile
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/12 11:21
 */
public class InputStreamToMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    public InputStreamToMultipartFile(InputStream inputStream, String name, String originalFilename, String contentType) throws IOException {
        this.content = inputStream.readAllBytes();
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}
