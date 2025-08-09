package com.xigua.client.controller;

import com.xigua.api.service.HomeService;
import com.xigua.domain.result.R;
import com.xigua.domain.vo.HomeCountVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HomeController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/9 11:05
 */
@Tag(name = "首页接口")
@RequestMapping("/home")
@RestController
public class HomeController {
    @Autowired
    private HomeService homeService;

    /**
     * 获取首页统计信息
     * @author wangjinfei
     * @date 2025/8/9 11:06
     * @return HomeCountVO
     */
    @GetMapping("/getHomeCount")
    public R<HomeCountVO> getHomeCount(){
        return R.ok(homeService.getHomeCount());
    }
}
