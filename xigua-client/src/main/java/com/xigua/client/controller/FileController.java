package com.xigua.client.controller;

import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.dto.MultipartFileDTO;
import com.xigua.domain.result.R;
import com.xigua.api.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName FileController
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/8 14:27
 */
@Tag(name = "文件接口")
@RequestMapping("/file")
@RestController
public class FileController {
    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     * @author wangjinfei
     * @date 2025/5/8 13:53
     * @param file
     * @return String
     */
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<String> upload(@RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest request){
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            throw new BusinessException("不是有效文件");
        }

        if(file == null || file.isEmpty()){
            throw new BusinessException("文件为空");
        }

        //MultipartFile dubbo不能直接序列化 用对象包装一下
        MultipartFileDTO multipartFileDTO = new MultipartFileDTO();
        BeanUtils.copyProperties(file,multipartFileDTO);

        // 上传并返回访问地址
        String url = fileService.upload(multipartFileDTO);
        return R.ok(url);
    }
}
