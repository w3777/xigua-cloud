package com.xigua.ai.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName WebSearch
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/20 20:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSearchRequest {
    private List<Message> messages;
    private String searchSource;
    private List<SearchResource> resourceTypeFilter;
    private SearchFilter searchFilter;
    private String searchRecencyFilter;

    @Data
    public static class Message {
        private String content;
        private String role;
    }

    @Data
    public static class SearchResource {
        private String type;
        private Integer topK;
    }

    @Data
    public static class SearchFilter {
        private Match match;

        @Data
        public static class Match {
            private List<String> site;
        }
    }
}
