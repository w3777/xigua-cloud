package com.xigua.ai.tool;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.tool.dto.WebSearchRequest;
import com.xigua.ai.tool.dto.WebSearchResponse;
import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;
import com.xigua.common.core.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WebSearchTool
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/20 20:15
 */
@Slf4j
@Component
public class WebSearchTool implements AITool {
    private final OkHttpClient client = new OkHttpClient();
    private final String url = "https://qianfan.baidubce.com/v2/ai_search/web_search";

    @Value("${tool.web-search.key}")
    private String key;

    @Override
    public String name() {
        return ToolType.WEB_SEARCH.getName();
    }

    @Override
    public String description() {
        return "网络搜索，参数为 content";
    }

    @Override
    public ToolResult execute(ToolRequest toolRequest) {
        WebSearchResponse response = getWebSearch(toolRequest);
        return ToolResult.success(name(), "", null, BeanUtil.convertToMap(response));
    }

    private WebSearchResponse getWebSearch(ToolRequest toolRequest) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(build(toolRequest)));
        Request request = new Request.Builder()
                .url(this.url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + this.key)
                .build();
        Response response = null;
        WebSearchResponse responseBody = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                responseBody = JSONObject.parseObject(responseStr, WebSearchResponse.class);
            } else {
                log.error(("Request failed with code: {}" + response.code()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return responseBody;
    }

    // 封装WebSearchRequest请求体
    private WebSearchRequest build(ToolRequest toolRequest){
        Map<String, Object> params = toolRequest.getParams();
        String content = (String) params.getOrDefault("content", "");
        String resourceType = (String) params.getOrDefault("resourceType", "web");
        Integer topK = (Integer) params.getOrDefault("topK", "");
        List<String> sites = (List<String>) params.getOrDefault("sites", new ArrayList<>());
        String recency = (String) params.getOrDefault("recency", "semiyear");
        if(CollectionUtils.isEmpty(sites)){
            sites = List.of("www.baidu.com");
        }

        WebSearchRequest.Message message = new WebSearchRequest.Message();
        message.setContent(content);
        message.setRole("user");

        WebSearchRequest.SearchResource source = new WebSearchRequest.SearchResource();
        source.setType(resourceType);
        source.setTopK(topK);

        WebSearchRequest.SearchFilter filter = new WebSearchRequest.SearchFilter();
        WebSearchRequest.SearchFilter.Match match = new WebSearchRequest.SearchFilter.Match();
        match.setSite(sites);
        filter.setMatch(match);

        WebSearchRequest request = WebSearchRequest.builder()
                .messages(List.of(message))
                .searchSource("baidu_search_v2")
                .resourceTypeFilter(List.of(source))
                .searchFilter(filter)
                .searchRecencyFilter(recency)
                .build();

        return request;
    }
}
