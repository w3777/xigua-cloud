package com.xigua.client.controller;

import com.xigua.client.utils.IpUtil;
import com.xigua.domain.result.R;
import com.xigua.domain.thirdparty.GetLocationRes;
import com.xigua.domain.thirdparty.GetWeatherRes;
import com.xigua.api.service.ThirdPartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ThirdPartyController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/11 14:28
 */
@Tag(name = "三方接口")
@RequiredArgsConstructor
@RequestMapping("/thirdParty")
@RestController
public class ThirdPartyController {
    @DubboReference
    private ThirdPartyService thirdPartyService;

    /**
     * 根据ip获取地址
     * @author wangjinfei
     * @date 2025/5/11 14:06
     * @param request
     * @return String
     */
    @Operation(summary = "根据ip获取地址")
    @GetMapping("/getLocation")
    public R<GetLocationRes> getLocation(HttpServletRequest request){
        String ip = IpUtil.getClientIp(request);
        return R.ok(thirdPartyService.getLocation(ip));
    }

    /**
     * 根据城市获取天气
     * @author wangjinfei
     * @date 2025/5/11 15:29
     * @param city
     * @return GetWeatherRes
     */
    @Operation(summary = "根据城市获取天气")
    @PostMapping("/getWeather")
    public R<GetWeatherRes> getWeather(@RequestParam("city") String city){
        return R.ok(thirdPartyService.getWeather(city));
    }
}
