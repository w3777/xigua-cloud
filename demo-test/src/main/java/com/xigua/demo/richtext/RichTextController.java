package com.xigua.demo.richtext;

import com.xigua.demo.domain.RichText;
import com.xigua.domain.dto.RichTextDTO;
import com.xigua.demo.mapper.RichTextMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName RichTextController
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/31 10:18
 */
// 允许来自 http://localhost:63342 的跨域请求
// 通过CDN引入 axios：直接在HTML页面中引入axios的CDN链接 后端会产生跨域问题
@CrossOrigin(origins = "http://localhost:63342")
@Slf4j
@RestController
@RequestMapping("/rich/text")
@RequiredArgsConstructor
public class RichTextController {
    private final RichTextMapper richTextMapper;

    /**
     * 保存富文本内容
     * @author wangjinfei
     * @date 2024/12/31 10:23
     * @param richTextDTO
     * @return String
    */
    @PostMapping("/saveContent")
    public String saveContent(@RequestBody RichTextDTO richTextDTO) {
        // 从前端获取富文本内容 这里是html格式的 (文本内容，如加粗字体、斜体、下划线、表情等)
        String htmlContent = richTextDTO.getContent();

        // 如果是图片、其他格式的文件，需要先上传到服务器上，然后返回图片的路径
        // 通过img标签<img src="https://xxxxxx" alt="示例图片" width="300" height="200" /> 就跟其他的文本一样，保存到数据库中

        // 保存
        RichText richText = new RichText();
        richText.setContent(htmlContent);
        richTextMapper.insert(richText);

        return "提交成功！";
    }

    // todo 查询回显富文本接口 只需要把id传过来，然后查询数据库，返回html内容即可
}
