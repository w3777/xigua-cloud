package com.xigua.demo.sequence;

import com.xigua.common.sequence.sequence.Sequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName sequenceController
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/21 10:55
 */
@Slf4j
@RestController
@RequestMapping("/sequence")
@RequiredArgsConstructor
public class SequenceController {
    private final Sequence sequence;

    @PostMapping("/test01")
    public void test01(){
        log.info("sequence:{}", sequence.nextValue());
    }
}
