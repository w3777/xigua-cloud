package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.api.service.BotService;
import com.xigua.client.mapper.BotMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.bo.LastMessageContentBO;
import com.xigua.domain.dto.BotDTO;
import com.xigua.domain.entity.Bot;
import com.xigua.domain.enums.ChatType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.vo.BotDetailVO;
import com.xigua.domain.vo.LastMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @ClassName BotServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/1 15:25
 */
@Slf4j
@Service
@DubboService
public class BotServiceImpl extends ServiceImpl<BotMapper, Bot> implements BotService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 创建机器人
     * @author wangjinfei
     * @date 2025/10/1 15:29
     * @param dto
     * @return Boolean
     */
    @Override
    public Boolean create(BotDTO dto) {
        Bot bot = new Bot();
        if(StringUtils.isEmpty(dto.getName())){
            throw new BusinessException("机器人名称不能为空");
        }
        if(StringUtils.isEmpty(dto.getAvatar())){
            throw new BusinessException("机器人头像不能为空");
        }
        if(StringUtils.isEmpty(dto.getPrompt())){
            throw new BusinessException("机器人提示词不能为空");
        }
        BeanUtils.copyProperties(dto, bot);
        bot.setId(sequence.nextNo());
        baseMapper.insert(bot);

        // 缓存机器人信息
        redisUtil.set(RedisEnum.BOT.getKey() + bot.getId(), JSONObject.toJSONString(bot));
        // 添加消息列表
        addLastMes(bot, UserContext.get().getUserId());

        return true;
    }

    private void addLastMes(Bot bot, String userId){
        // 映射vo
        LastMessageVO lastMessageVO = new LastMessageVO();
        lastMessageVO.setChatId(bot.getId());
        lastMessageVO.setChatType(ChatType.THREE.getType());
        lastMessageVO.setChatName(bot.getName());
        lastMessageVO.setAvatar(bot.getAvatar());
        LastMessageContentBO lastMessageContentBO = new LastMessageContentBO();
        lastMessageContentBO.setContent(String.format("你好！我是%s，很高兴认识你！", bot.getName()));
        lastMessageVO.setLastMessageContent(lastMessageContentBO);
        lastMessageVO.setUpdateTime(DateUtil.toEpochMilli(bot.getCreateTime()));

        // redis 最后消息
        redisUtil.zsadd(RedisEnum.LAST_MES.getKey() + userId, bot.getId(), DateUtil.toEpochMilli(bot.getCreateTime()));
        // redis 最后消息内容
        redisUtil.hashPut(RedisEnum.LAST_MES_CONTENT.getKey() + userId, bot.getId(), JSONObject.toJSONString(lastMessageVO));
    }

    /**
     * 根据用户id获取机器人数量
     * @author wangjinfei
     * @date 2025/10/1 16:45
     * @param userId
     * @return Integer
     */
    @Override
    public Integer getCountByUserId(String userId) {
        Integer count = baseMapper.getCountByUserId(userId);
        return count != null ? count : 0;
    }

    /**
     * 根据用户id获取机器人id列表
     * @author wangjinfei
     * @date 2025/10/1 16:45
     * @param userId
     * @return Set<String>
     */
    @Override
    public Set<String> getBotIdsByUserId(String userId) {
        return baseMapper.getBotIdsByUserId(userId);
    }

    /**
     * 获取机器人详情
     * @author wangjinfei
     * @date 2025/10/8 18:19
     * @param botId
     * @return BotDetailVO
     */
    @Override
    public BotDetailVO getBotDetail(String botId) {
        String botCache = redisUtil.get(RedisEnum.BOT.getKey() + botId);
        BotDetailVO botDetailVO = null;
        if(StringUtils.isNotEmpty(botCache)){
            botDetailVO = JSONObject.parseObject(botCache, BotDetailVO.class);
        }else{
            // todo query db
        }

        return botDetailVO;
    }
}
