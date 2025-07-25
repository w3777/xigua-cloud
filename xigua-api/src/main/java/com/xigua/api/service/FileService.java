package com.xigua.api.service;

import com.xigua.domain.dto.MultipartFileDTO;

/**
 * @ClassName FileService
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 13:47
 */
public interface FileService {

    /**
     * 上传文件
     * @author wangjinfei
     * @date 2025/5/8 13:53
     * @param multipartFileDTO
     * @return String
    */
    String upload(MultipartFileDTO multipartFileDTO);
}
