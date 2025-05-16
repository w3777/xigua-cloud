package com.xigua.client.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.thirdparty.GetLocationRes;
import com.xigua.domain.thirdparty.GetWeatherRes;
import com.xigua.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.to;

/**
 * @ClassName ThirdPartyService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/11 14:06
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class ThirdPartyServiceImpl implements ThirdPartyService {
    private final RedisUtil redisUtil;
    private final OkHttpClient client = new OkHttpClient();

    // ip地址查询接口地址
    public static String LOCATION_API_URL = "http://apis.juhe.cn/ip/ipNewV3";
    // ip地址接口请求Key
    public static String LOCATION_API_KEY = "1946c6e1088c0c79d7957bbeb692c2f3";

    // 天气查询接口地址
    public static String WEATHER_API_URL = "http://apis.juhe.cn/simpleWeather/query";
    // 天气接口请求Key
    public static String WEATHER_API_KEY = "f84f1a02de1cbaebda0e00dde63644e3";

    /**
     * 根据ip获取地址
     * @author wangjinfei
     * @date 2025/5/11 14:06
     * @param ip
     * @return String
     */
    @Override
    public GetLocationRes getLocation(String ip) {
        GetLocationRes getLocationRes = new GetLocationRes();

        // 从缓存中获取结果
        String cachedResult = redisUtil.get(RedisEnum.LOCATION_IP.getKey() + ip);
        if (cachedResult != null) {
            return JSONObject.parseObject(cachedResult, GetLocationRes.class);
        }

        // 1. 构建请求URL
        HttpUrl url = HttpUrl.parse(LOCATION_API_URL).newBuilder()
                .addQueryParameter("key", LOCATION_API_KEY)
                .addQueryParameter("ip", ip)
                .build();

        // 2. 创建请求
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .get()
                .build();

        // 3. 执行请求并处理响应
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("请求失败，状态码：{}", response.code());
            }

            // 4. 解析JSON响应
            ResponseBody body = response.body();
            if (body == null) {
                log.error("响应体为空");
            }
            String json = body.string();
            JSONObject jsonObject = JSON.parseObject(json);

            // 5. 检查接口返回的错误码
            if (jsonObject.getString("resultcode").equals("200") == false) {
                log.error("接口返回错误：{}", jsonObject.getString("reason"));
            }

            String result = jsonObject.getString("result");
            getLocationRes = JSONObject.parseObject(result, GetLocationRes.class);

            // 6. 缓存结果
            redisUtil.set(RedisEnum.LOCATION_IP.getKey() + ip, result, 60 * 60 * 24L);
            return getLocationRes;
        }catch (Exception e) {
            log.error("获取地址失败", e);
        }
        return getLocationRes;
    }

    /**
     * 根据城市获取天气
     * @author wangjinfei
     * @date 2025/5/11 15:29
     * @param city
     * @return GetWeatherRes
     */
    @Override
    public GetWeatherRes getWeather(String city) {
        GetWeatherRes getWeatherRes = new GetWeatherRes();

        // 从缓存中获取结果
        String cachedResult = redisUtil.get(RedisEnum.WEATHER_CITY.getKey() + city);
        if (cachedResult != null) {
            return JSONObject.parseObject(cachedResult, GetWeatherRes.class);
        }


        HttpUrl url = HttpUrl.parse(WEATHER_API_URL).newBuilder()
                .addQueryParameter("key", WEATHER_API_KEY)
                .addQueryParameter("city", city)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .get()
                .build();


        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("请求失败，状态码：{}", response.code());
            }

            // 4. 解析JSON响应
            ResponseBody body = response.body();
            if (body == null) {
                log.error("响应体为空");
            }
            String json = body.string();
            JSONObject jsonObject = JSON.parseObject(json);

            // 5. 检查接口返回的错误码
            if (jsonObject.getString("reason").equals("查询成功!") == false) {
                log.error("接口返回错误：{}", jsonObject.getString("reason"));
            }

            String result = jsonObject.getJSONObject("result").getString("realtime");
            getWeatherRes = JSONObject.parseObject(result, GetWeatherRes.class);

            // 缓存结果
            LocalDateTime toDayLatestTime = LocalDate.now().atTime(23, 59, 59);
            long second = ChronoUnit.SECONDS.between(LocalDateTime.now(), toDayLatestTime);
            // 缓存有效期为当天23:59:59
            redisUtil.set(RedisEnum.WEATHER_CITY.getKey() + city, result, second);
            return getWeatherRes;
        }catch (Exception e) {
            log.error("获取天气失败", e);
        }
        return getWeatherRes;
    }
}
