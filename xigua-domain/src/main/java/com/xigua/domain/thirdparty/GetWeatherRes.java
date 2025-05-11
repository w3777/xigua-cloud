package com.xigua.domain.thirdparty;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GetWeatherRes
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/11 15:27
 */
@Data
public class GetWeatherRes implements Serializable {

    private static final long serialVersionUID = 1L;
    // 天气情况，如：晴、多云
    private String info;
    // 天气标识id，可参考小接口2
    private String wid;
    // 温度，可能为空
    private String temperature;
    // 湿度，可能为空
    private String humidity;
    // 风向，可能为空
    private String direct;
    // 风力，可能为空
    private String power;
    // 空气质量指数，可能为空
    private String aqi;
}
