package com.modcreater.tmstore.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.StoreGoods;
import com.modcreater.tmbeans.pojo.StoreGoods;
import com.modcreater.tmbeans.pojo.StoreGoodsConsumable;
import com.modcreater.tmbeans.vo.goods.ConsumablesList;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;
import com.modcreater.tmbeans.vo.store.GoodsListVo;
import com.modcreater.tmdao.mapper.GoodsMapper;
import com.modcreater.tmstore.service.GoodsService;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        goodsMapper.addNewGoodsStock(registerGoods.getId(),registerGoods.getGoodsNum(),0);
        if (registerGoods.getConsumablesLists().length > 0){
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setMaximumFractionDigits(2);
            for (ConsumablesList consumablesList : registerGoods.getConsumablesLists()){
                StoreGoods goods = goodsMapper.getGoodsInfo(consumablesList.getConsumablesId());
                StoreGoodsConsumable consumable = new StoreGoodsConsumable();
                consumable.setGoodsId(Long.valueOf(registerGoods.getId()));
                consumable.setConsumableGoodsId(Long.valueOf(consumablesList.getConsumablesId()));
                consumable.setRegisteredRatioIn(Long.valueOf(consumablesList.getConsumablesNum()));
                consumable.setRegisteredRationInUnit(goods.getGoodsUnit());
                consumable.setRegisteredRatioOut(Long.valueOf(consumablesList.getFinishedNum()));
                consumable.setRegisteredRationOutUnit(registerGoods.getGoodsUnit());
                consumable.setRegisteredTime(System.currentTimeMillis()/1000);
                goodsMapper.addNewGoodsConsumable(consumable);
            }
        }
        return DtoUtil.getSuccesWithDataDto("添加成功",registerGoods.getId(),100000);
    }

    /**
     * 根据类型查询商铺商品列表
     * @param goodsListVo
     * @param token
     * @return
     */
    @Override
    public Dto grtGoodsList(GoodsListVo goodsListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(goodsListVo.getPageSize());
        int pageIndex=(Integer.parseInt(goodsListVo.getPageNumber())-1)*pageSize;
        List<Map<String,String>> goodsList=null;
        if ("1".equals(goodsListVo.getGoodsType())){
            //优惠
            goodsList=new ArrayList<>();
        }else if ("2".equals(goodsListVo.getGoodsType())){
            //热销
            goodsList=new ArrayList<>();
        }else {
            //普通分类
            goodsList=goodsMapper.grtGoodsList(goodsListVo.getStoreId(),goodsListVo.getGoodsName(),goodsListVo.getGoodsType(),pageIndex,pageSize);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",goodsList,100000);
    }

}
