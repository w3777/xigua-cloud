package com.xigua.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;
import com.xigua.common.core.util.BeanUtil;
import com.xigua.domain.thirdparty.GetWeatherRes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName WeatherTool
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/15 19:52
 */
@Slf4j
@Component
public class WeatherTool implements AITool {
    private final OkHttpClient client = new OkHttpClient();
    private final String url = "http://apis.juhe.cn/simpleWeather/query";
    @Value("${tool.weather.key}")
    private String key;

    @Override
    public String name() {
        return "weather";
    }

    @Override
    public String description() {
        return "查询天气，参数为 city";
    }

    @Override
    public ToolResult execute(ToolRequest request) {
        String city = (String) request.getParams().get("city");
        GetWeatherRes getWeatherRes = getWeather(city);
//        String output = "今天" + city + "天气晴朗，28℃";
        return ToolResult.success(name(), "", null, BeanUtil.convertToMap(getWeatherRes));
    }

    public GetWeatherRes getWeather(String city) {
        GetWeatherRes getWeatherRes = new GetWeatherRes();

        // todo 从缓存中获取结果

        HttpUrl url = HttpUrl.parse(this.url).newBuilder()
                .addQueryParameter("key", this.key)
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

            // todo 缓存结果

            return getWeatherRes;
        }catch (Exception e) {
            log.error("获取天气失败", e);
        }
        return getWeatherRes;
    }
}
