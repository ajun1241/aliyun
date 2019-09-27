package com.modcreater.tmstore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.StoreGoods;
import com.modcreater.tmbeans.pojo.StoreGoodsConsumable;
import com.modcreater.tmbeans.pojo.StoreInfo;
import com.modcreater.tmbeans.show.goods.ShowConsumableGoods;
import com.modcreater.tmbeans.show.goods.ShowGoodsPriceInfo;
import com.modcreater.tmbeans.show.goods.ShowGoodsStockInfo;
import com.modcreater.tmbeans.pojo.StoreGoodsType;
import com.modcreater.tmbeans.utils.Barcode;
import com.modcreater.tmbeans.utils.GetBarcode;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.store.ClaimGoodsVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.store.GoodsInfoVo;
import com.modcreater.tmbeans.vo.store.GoodsListVo;
import com.modcreater.tmdao.mapper.GoodsMapper;
import com.modcreater.tmdao.mapper.StoreMapper;
import com.modcreater.tmstore.service.GoodsService;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

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
    private StoreMapper storeMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto registerGoods(RegisterGoods registerGoods, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(registerGoods.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        goodsMapper.addNewGoods(registerGoods);
        goodsMapper.addNewGoodsStock(registerGoods.getId(), registerGoods.getGoodsNum(),"1");
        if (registerGoods.getConsumablesLists().length > 0) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setMaximumFractionDigits(2);
            for (ConsumablesList consumablesList : registerGoods.getConsumablesLists()) {
                StoreGoods goods = goodsMapper.getGoodsInfo(consumablesList.getConsumablesId());
                StoreGoodsConsumable consumable = new StoreGoodsConsumable();
                consumable.setGoodsId(Long.valueOf(registerGoods.getId()));
                consumable.setConsumableGoodsId(Long.valueOf(consumablesList.getConsumablesId()));
                consumable.setRegisteredRatioIn(consumablesList.getConsumablesNum());
                consumable.setRegisteredRationInUnit(goods.getGoodsUnit());
                consumable.setRegisteredRatioOut(Long.valueOf(consumablesList.getFinishedNum()));
                consumable.setRegisteredRationOutUnit(registerGoods.getGoodsUnit());
                consumable.setRegisteredTime(System.currentTimeMillis() / 1000);
                goodsMapper.addNewGoodsConsumable(consumable);
            }
        }
        return DtoUtil.getSuccesWithDataDto("添加成功", registerGoods.getId(), 100000);
    }

    @Override
    public Dto updateGoodsInfo(UpdateGoods updateGoods, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateGoods.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (reg(updateGoods.getUserId(),updateGoods.getStoreId())){
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        //此处要知道,商品修改了除条形码外的所有信息,那该商品是否还是原来的商品
        //相同的商品用什么来做标识,如果是条形码,不同的生产商条形码会不同导致商品不同
        goodsMapper.updateGoods(updateGoods);
        goodsMapper.updateGoodsStock(updateGoods.getGoodsId(),updateGoods.getGoodsNum());
        goodsMapper.cleanConsumablesList(updateGoods.getGoodsId());
        if (updateGoods.getConsumablesLists().length > 0){
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setMaximumFractionDigits(2);
            for (ConsumablesList consumablesList : updateGoods.getConsumablesLists()) {
                StoreGoods goods = goodsMapper.getGoodsInfo(consumablesList.getConsumablesId());
                StoreGoodsConsumable consumable = new StoreGoodsConsumable();
                consumable.setGoodsId(Long.valueOf(updateGoods.getGoodsId()));
                consumable.setRegisteredRatioIn(consumablesList.getConsumablesNum());
                consumable.setConsumableGoodsId(Long.valueOf(consumablesList.getConsumablesId()));
                consumable.setRegisteredRationInUnit(goods.getGoodsUnit());
                consumable.setRegisteredRatioOut(Long.valueOf(consumablesList.getFinishedNum()));
                consumable.setRegisteredTime(System.currentTimeMillis() / 1000);
                consumable.setRegisteredRationOutUnit(updateGoods.getGoodsUnit());
                goodsMapper.addNewGoodsConsumable(consumable);
            }
        }
        return null;
    }

    @Override
    public Dto getUpdatePriceInfo(ReceivedGoodsId receivedGoodsId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGoodsId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods goods = goodsMapper.getGoodsInfo(receivedGoodsId.getGoodsId());
        if (ObjectUtils.isEmpty(goods)){
            return DtoUtil.getFalseDto("商品信息未查到",90004);
        }
        if (!reg(receivedGoodsId.getUserId(),goods.getStoreId().toString())){
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("goodsName",goods.getGoodsName());
        result.put("goodsBrand",goods.getGoodsBrand());
        result.put("goodsUnit",goods.getGoodsUnit());
        result.put("goodsFUnit",goods.getGoodsFUnit());
        result.put("faUnitNum",goods.getFaUnitNum());
        return DtoUtil.getSuccesWithDataDto("操作成功",result,100000);
    }

    @Override
    public Dto updateGoodsPrice(UpdateGoodsPrice updateGoodsPrice, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateGoodsPrice.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods goods = goodsMapper.getGoodsInfo(updateGoodsPrice.getGoodsId());
        if (!reg(updateGoodsPrice.getUserId(),goods.getStoreId().toString())){
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        /*if (goods.getGoodsPrice() == null || goods.getGoodsPrice() == 0){
            goodsMapper.updateGoodsStatus(updateGoodsPrice.getGoodsId(),1);
        }*/
        if (goodsMapper.updateGoodsUnitPrice(updateGoodsPrice.getGoodsId(),updateGoodsPrice.getUnitPrice()) != 1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改价格失败",80005);
        }
        return DtoUtil.getSuccessDto("修改成功",100000);
    }

    @Override
    public Dto getGoodsTypes(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return DtoUtil.getSuccesWithDataDto("获取成功",goodsMapper.getGoodsAllTypeList(),100000);
    }

    /**
     * 收货完成交易
     * @param claimGoodsVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto claimGoods(ClaimGoodsVo claimGoodsVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(claimGoodsVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询商品库存
        /*goodsMapper.getGoodsStock();
        //减去出货商家的库存
        goodsMapper.deductionStock(claimGoodsVo.getSourceStoreId());*/
        //收货商户添加一批货物
        //保存交易记录
        //反馈交易双方
        return null;
    }

    @Override
    public Dto goodsDownShelf(GoodsDownShelf goodsDownShelf, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsDownShelf.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(goodsDownShelf.getUserId(), goodsDownShelf.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (goodsDownShelf.getGoodsId() == null){
            return DtoUtil.getFalseDto("参数错误",90006);
        }
        for (String goodsId : goodsDownShelf.getGoodsId()){
//            goodsMapper.updateGoodsStatus(goodsId,);
        }
        return null;
    }

    @Override
    public Dto getGoodsStockList(GetGoodsStockList getGoodsStockList, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getGoodsStockList.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getGoodsStockList.getUserId(), getGoodsStockList.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        getGoodsStockList.setPageNum(getGoodsStockList.getPageNum() - 1);
        Map<String, Object> result = new HashMap<>();
        StoreInfo storeInfo = storeMapper.getStoreInfo(getGoodsStockList.getStoreId());
        result.put("storeName", storeInfo.getStoreName());
        if ("stock".equals(getGoodsStockList.getGetType())) {
            List<ShowGoodsStockInfo> goodsStockInfos = goodsMapper.getGoodsStockList(getGoodsStockList);
            result.put("goodsList", goodsStockInfos);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else if ("price".equals(getGoodsStockList.getGetType())) {
            List<ShowGoodsPriceInfo> goodsPriceList = goodsMapper.getGoodsPriceList(getGoodsStockList);
            result.put("goodsList", goodsPriceList);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else if ("consumable".equals(getGoodsStockList.getGetType())) {
            List<ShowConsumableGoods> consumableGoods = goodsMapper.getConsumableGoods(getGoodsStockList);
            result.put("goodsList", consumableGoods);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else if ("son".equals(getGoodsStockList.getGetType())) {
            List<ShowConsumableGoods> consumableGoods = goodsMapper.getConsumableGoods(getGoodsStockList);
            result.put("goodsList", consumableGoods);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else {
            return DtoUtil.getFalseDto("参数有误", 90002);
        }
    }

    /**
     * 返回false为不符合
     * @param userId
     * @param storeId
     * @return
     */
    private boolean reg(String userId, String storeId) {
        return goodsMapper.getStoreMaster(userId, storeId) == 1;
    }

    /**
     * 根据类型查询商铺商品列表
     * @param goodsListVo
     * @param token
     * @return
     */
    @Override
    public Dto getGoodsList(GoodsListVo goodsListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        /*if (StringUtils.isEmpty(goodsListVo.getStoreId())){
            return DtoUtil.getFalseDto("抱歉，您尚未拥有店铺",27058);
        }*/
        int pageSize=Integer.parseInt(goodsListVo.getPageSize());
        int pageIndex=(Integer.parseInt(goodsListVo.getPageNumber())-1)*pageSize;
        Map<String,Object> map=new HashMap<>(2);
        List<StoreGoodsType> goodsTypeList=goodsMapper.getGoodsTypeList(goodsListVo.getStoreId());
        List<Map<String,Object>> mapList=new ArrayList<>();
        map.put("goodsTypeList",goodsTypeList);
        List<Map<String,Object>> goodsList=null;
        if ("1".equals(goodsListVo.getGoodsType())){
            //优惠
            goodsList=new ArrayList<>();
        }else if ("2".equals(goodsListVo.getGoodsType())){
            //热销
            goodsList=new ArrayList<>();
        }else {
            //普通分类
            if (StringUtils.isEmpty(goodsListVo.getGoodsType())){
                goodsListVo.setGoodsType(goodsTypeList.size()>0?goodsTypeList.get(0).getId().toString():"");
            }
            goodsList=goodsMapper.getGoodsList(goodsListVo.getStoreId(),goodsListVo.getGoodsName(),goodsListVo.getGoodsType(),pageIndex,pageSize);
            for (Map<String,Object> storeGoods:goodsList) {
                Map<String,Object> goodsMap=new HashMap<>(7);
                goodsMap.put("goodsId",storeGoods.get("id"));
                goodsMap.put("goodsPicture",storeGoods.get("goodsPicture"));
                goodsMap.put("goodsName",storeGoods.get("goodsBrand"));
                //周销量
                goodsMap.put("weekSalesVolume",0);
                goodsMap.put("goodsPrice",storeGoods.get("goodsPrice")==null ? 0 : storeGoods.get("goodsPrice"));
                goodsMap.put("goodsUnit",storeGoods.get("goodsUnit"));
                mapList.add(goodsMap);
            }
        }
        map.put("goodsList",mapList);
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    /**
     * 根据类型查询商铺商品列表（一次性查所有）
     * @param goodsListVo
     * @param token
     * @return
     */
    @Override
    public Dto getGoodsList2(GoodsListVo goodsListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<StoreGoodsType> goodsTypeList=goodsMapper.getGoodsTypeList(goodsListVo.getStoreId());
        List<Map<String,Object>> mapperGoodsList=goodsMapper.getGoodsList(goodsListVo.getStoreId(),goodsListVo.getGoodsName(),goodsListVo.getGoodsType(),-1,-1);
        List<List<Map<String,Object>>> resultList=new ArrayList<>();
        if ("1".equals(goodsListVo.getGoodsType())){
            //优惠
//            goodsList=new ArrayList<>();
        }else if ("2".equals(goodsListVo.getGoodsType())){
            //热销
//            goodsList=new ArrayList<>();
        }else {
            //普通分类
            if (StringUtils.isEmpty(goodsListVo.getGoodsType())){
                goodsListVo.setGoodsType(goodsTypeList.size()>0?goodsTypeList.get(0).getId().toString():"");
            }
//            goodsList=
            /*for (StoreGoods storeGoods:goodsList) {
                Map<String,Object> goodsMap=new HashMap<>(7);
                goodsMap.put("goodsId",storeGoods.getId());
                goodsMap.put("goodsPicture",storeGoods.getGoodsPicture());
                goodsMap.put("goodsName",storeGoods.getGoodsName()+storeGoods.getGoodsUnit()+"装");
                goodsMap.put("weekSalesVolume",0);
                goodsMap.put("goodsPrice",storeGoods.getGoodsPrice());
                goodsMap.put("goodsUnit",storeGoods.getGoodsUnit());
                mapList.add(goodsMap);
            }*/
        }
        for (StoreGoodsType storeGoodsType:goodsTypeList) {

        }
        return null;
    }

    /**
     * 根据Id查询商品详情
     * @param goodsInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto getGoodsInfo(GoodsInfoVo goodsInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods storeGoods=goodsMapper.getGoodsInfo(goodsInfoVo.getGoodsId());
        Map<String,Object> map=new HashMap<>(5);
        if (!ObjectUtils.isEmpty(storeGoods)){
            map.put("goodsId",storeGoods.getId());
            map.put("goodsPicture",storeGoods.getGoodsPicture());
            map.put("goodsName",storeGoods.getGoodsName()+storeGoods.getGoodsUnit()+"装");
            map.put("weekSalesVolume",0);
//            map.put("goodsPrice",storeGoods.getGoodsPrice());
            map.put("goodsUnit",storeGoods.getGoodsUnit());
        }else {
            return DtoUtil.getFalseDto("查询失败，该商品可能已下架",24105);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    @Override
    public Dto getBarcodeInfo(String barcode) {
        if (!StringUtils.hasText(barcode)){
            return DtoUtil.getFalseDto("barcode不存在",90003);
        }
        String url = "https://www.mxnzp.com/api/barcode/goods/details?barcode="+barcode;
        RestTemplate template = new RestTemplate();
        ResponseEntity responseEntity = template.getForEntity(url,String.class);
        GetBarcode getBarcode= JSONObject.parseObject(responseEntity.getBody().toString(),GetBarcode.class);
        if ("0".equals(getBarcode.getCode())){
            return DtoUtil.getFalseDto("获取失败",80004);
        }
        return DtoUtil.getSuccesWithDataDto("获取成功",getBarcode.getData(),100000);
    }
}