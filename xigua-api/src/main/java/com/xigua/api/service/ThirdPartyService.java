package com.xigua.api.service;

import com.xigua.domain.thirdparty.GetLocationRes;
import com.xigua.domain.thirdparty.GetWeatherRes;

/**
 * @ClassName ThirdPartyService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/11 14:05
 */
public interface ThirdPartyService {
    /**
     * 根据ip获取地址
     * @author wangjinfei
     * @date 2025/5/11 14:06
     * @param ip
     * @return String
    */
    GetLocationRes getLocation(String ip);

    /**
     * 根据城市获取天气
     * @author wangjinfei
     * @date 2025/5/11 15:29
     * @param city
     * @return GetWeatherRes
    */
    GetWeatherRes getWeather(String city);
}
