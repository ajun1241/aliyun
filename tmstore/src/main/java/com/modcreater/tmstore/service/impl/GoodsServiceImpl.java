package com.modcreater.tmstore.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;
import com.modcreater.tmdao.mapper.GoodsMapper;
import com.modcreater.tmstore.service.GoodsService;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-20
 * Time: 10:02
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto registerGoods(RegisterGoods registerGoods, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(registerGoods.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        goodsMapper.addNewGoods(registerGoods);
        return DtoUtil.getSuccesWithDataDto("添加成功",registerGoods.getId(),100000);
    }
}
