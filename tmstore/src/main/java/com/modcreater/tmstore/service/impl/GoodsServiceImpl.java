package com.modcreater.tmstore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.databaseparam.goods.AddNewGoodsPromoteSales;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.goods.*;
import com.modcreater.tmbeans.show.store.ShowPromoteSalesInfo;
import com.modcreater.tmbeans.utils.GetBarcode;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.store.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.GoodsMapper;
import com.modcreater.tmdao.mapper.StoreMapper;
import com.modcreater.tmstore.service.GoodsService;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.SingleEventUtil;
import com.modcreater.tmutils.messageutil.RefreshMsg;
import com.modcreater.tmutils.pay.PayUtil;
import com.modcreater.tmutils.pay.PaymentCodeUtil;
import com.modcreater.tmutils.store.StoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Override
    public synchronized Dto registerGoods(RegisterGoods registerGoods, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(registerGoods.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (StringUtils.hasText(registerGoods.getGoodsBarCode()) && goodsMapper.isBarCodeExists(registerGoods.getStoreId(), registerGoods.getGoodsBarCode()) >= 1) {
            return DtoUtil.getFalseDto("请勿重复录入相同的条形码", 90007);
        }
        if (goodsMapper.getCorRelation(registerGoods.getCorGoodsId()) >= 1) {
            return DtoUtil.getFalseDto("当前选中的转换商品已被其他产品绑定", 80006);
        }
        if (registerGoods.getFaUnitNum() == null) {
            registerGoods.setFaUnitNum(0L);
        }
        if (!StringUtils.hasText(registerGoods.getGoodsFUnit())) {
            registerGoods.setGoodsFUnit("");
        }
        goodsMapper.addNewGoods(registerGoods);
        goodsMapper.addNewGoodsStock(registerGoods.getId(), registerGoods.getStoreId(), registerGoods.getGoodsNum(), "3", registerGoods.getGoodsBarCode());
        if (StringUtils.hasText(registerGoods.getGoodsFUnit()) && !StringUtils.hasText(registerGoods.getCorGoodsId())) {
            return DtoUtil.getFalseDto("缺少绑定商品", 90012);
        }
        if (StringUtils.hasText(registerGoods.getCorGoodsId())) {
            goodsMapper.bindingGoods(registerGoods.getId(), registerGoods.getCorGoodsId());
        }
        if (registerGoods.getConsumablesLists().size() > 0) {
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
                consumable.setConsumptionRate(consumable.getRegisteredRatioIn() / consumable.getRegisteredRatioOut());
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
        if (!reg(updateGoods.getUserId(), updateGoods.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        try {
            StoreGoods storeGoods = goodsMapper.getGoodsInfo(updateGoods.getGoodsId());
            if (ObjectUtils.isEmpty(storeGoods)) {
                return DtoUtil.getFalseDto("商品未找到", 90009);
            }
            if (StringUtils.hasText(updateGoods.getCorGoodsId())) {
                if (goodsMapper.updateCorRelation(storeGoods.getId().toString(), updateGoods.getCorGoodsId()) != 1) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("修改失败", 90011);
                }
            } else if (!StringUtils.hasText(updateGoods.getGoodsFUnit())) {
                goodsMapper.deleteCorRelation(storeGoods.getId().toString());
            }
            int updateGoodsResult = goodsMapper.updateGoods(updateGoods);
            int updateGoodsStockResult = goodsMapper.updateGoodsStock(updateGoods.getGoodsId(), updateGoods.getGoodsNum(), updateGoods.getGoodsBarCode(), updateGoods.getStoreId());
            if (updateGoodsResult == 0 || updateGoodsStockResult == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("修改失败", 90011);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改失败", 90011);
        }
        return DtoUtil.getSuccessDto("操作成功", 100000);
    }

    @Override
    public Dto getUpdatePriceInfo(ReceivedGoodsId receivedGoodsId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGoodsId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods goods = goodsMapper.getGoodsInfo(receivedGoodsId.getGoodsId());
        if (ObjectUtils.isEmpty(goods)) {
            return DtoUtil.getFalseDto("商品信息未查到", 90004);
        }
        if (!reg(receivedGoodsId.getUserId(), goods.getStoreId().toString())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("goodsName", goods.getGoodsName());
        result.put("goodsBrand", goods.getGoodsBrand());
        result.put("goodsUnit", goods.getGoodsUnit());
        result.put("goodsFUnit", goods.getGoodsFUnit() == null ? "" : goods.getGoodsFUnit());
        result.put("faUnitNum", goods.getFaUnitNum() == null ? "" : goods.getFaUnitNum());
        return DtoUtil.getSuccesWithDataDto("操作成功", result, 100000);
    }

    @Override
    public Dto updateGoodsPrice(UpdateGoodsPrice updateGoodsPrice, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateGoodsPrice.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoodsStock storeGoodsStock = goodsMapper.getGoodsStock(updateGoodsPrice.getGoodsId(), updateGoodsPrice.getStoreId());
        if (!reg(updateGoodsPrice.getUserId(), storeGoodsStock.getStoreId().toString())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (updateGoodsPrice.getUnitPrice() <= 0){
            return DtoUtil.getFalseDto("价格不能小于0",80015);
        }
        //注意父子商品之间价格的影响关系
        if (goodsMapper.updateGoodsUnitPrice(updateGoodsPrice.getGoodsId(), updateGoodsPrice.getUnitPrice(), storeGoodsStock.getStoreId(),storeGoodsStock.getGoodsPrice() == 0 ? 1 : null) != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改价格失败", 80005);
        }
        return DtoUtil.getSuccessDto("修改成功", 100000);
    }

    @Override
    public Dto getGoodsTypes(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return DtoUtil.getSuccesWithDataDto("获取成功", goodsMapper.getGoodsAllTypeList(), 100000);
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
        if (claimGoodsVo.getSourceStoreId().equals(claimGoodsVo.getTargetStoreId())){
            return DtoUtil.getFalseDto("不能与自己交易",95016);
        }
        try {
            //判断商品库存
            for (Map<String,String> map:claimGoodsVo.getSourceGoods()) {
                StoreGoodsStock goodsStock=goodsMapper.queryGoodsStock(map.get("goodsId"),claimGoodsVo.getSourceStoreId());
                if (goodsStock.getStockNum() < Long.parseLong(map.get("num"))){
                    StoreGoods storeGoods=goodsMapper.getGoodsInfo(map.get("goodsId"));
                    return DtoUtil.getFalseDto("商品"+storeGoods.getGoodsName()+"库存不足,交易未完成",95001);
                }
            }
            //减去出货商家的库存
            int a=goodsMapper.deductionStock(claimGoodsVo.getSourceStoreId(),claimGoodsVo.getSourceGoods());
            //收货商户添加一批货物
            addGoodsStock(claimGoodsVo.getTargetStoreId(),claimGoodsVo.getSourceGoods());
            //保存交易记录
            String orderNumber=(System.currentTimeMillis()/1000+claimGoodsVo.getSourceStoreId()+claimGoodsVo.getTargetStoreId());
            int b=storeMapper.saveTradingRecord(claimGoodsVo.getSourceGoods(),claimGoodsVo.getSourceStoreId(),claimGoodsVo.getTargetStoreId(),claimGoodsVo.getTransactionPrice(),orderNumber,1);
            if (a<=0 || b<=0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("交易异常",94014);
            }
            //反馈交易双方
            StoreInfo storeInfo=storeMapper.getStoreInfo(claimGoodsVo.getSourceStoreId());
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            RefreshMsg refreshMsg=new RefreshMsg("2");
            rongCloudMethodUtil.sendSystemMessage(claimGoodsVo.getUserId(),new String[]{storeInfo.getUserId().toString()},refreshMsg,"","");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("交易异常",94014);
        }
        return DtoUtil.getSuccessDto("收货成功",100000);
    }

    /**
     * 单位转换
     * @param conversionUnitVo
     * @param token
     * @return
     */
    @Override
    public Dto conversionUnit(ConversionUnitVo conversionUnitVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(conversionUnitVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods storeGoods=goodsMapper.getGoodsInfo(conversionUnitVo.getGoodsId());
        if (ObjectUtils.isEmpty(storeGoods)){
            return DtoUtil.getFalseDto("转换失败",91016);
        }
        StoreGoods targetStoreGoods;
        Map<String,String> map=new HashMap<>(3);
        if (StringUtils.isEmpty(storeGoods.getGoodsFUnit())){
            //如果没有子单位则自己是子单位
            /*StoreGoodsCorrelation goodsCorrelation=goodsMapper.getParentGoodsInfo(conversionUnitVo.getGoodsId());
            targetStoreGoods=goodsMapper.getGoodsInfo(goodsCorrelation.getGoodsParentId().toString());
            //则转换数量在转换后的商品信息里
            map.put("convertNum",ObjectUtils.isEmpty(targetStoreGoods) ? "" : String.valueOf((Long.parseLong(conversionUnitVo.getNum())/targetStoreGoods.getFaUnitNum())));*/
            return DtoUtil.getFalseDto("该商品已是最小单位，不能转换",95012);
        }else {
            //如果有子单位则自己是父单位
            StoreGoodsCorrelation goodsCorrelation=goodsMapper.getSonGoodsInfo(conversionUnitVo.getGoodsId());
            targetStoreGoods=goodsMapper.getGoodsInfo(goodsCorrelation.getGoodsSonId().toString());
            //则转换数量在转换前的商品信息中
            map.put("convertNum",ObjectUtils.isEmpty(storeGoods) ? "" :String.valueOf((storeGoods.getFaUnitNum()*Long.parseLong(conversionUnitVo.getNum()))));
        }
        if (ObjectUtils.isEmpty(targetStoreGoods)){
            return DtoUtil.getFalseDto("转换失败",91016);
        }else {
            map.put("goodsId",targetStoreGoods.getId().toString());
            map.put("goodsName",targetStoreGoods.getGoodsName());
            map.put("goodsUnit",targetStoreGoods.getGoodsUnit());
            return DtoUtil.getSuccesWithDataDto("操作成功",map,100000);
        }
    }

    /**
     * 保存订单信息
     * @param orderInfoVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto saveOrderInfo(OrderInfoVo orderInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(orderInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreQrCode storeQrCode=new StoreQrCode();
        storeQrCode.setId("OQR"+orderInfoVo.getUserId()+System.currentTimeMillis());
        storeQrCode.setCodeContent(orderInfoVo.getCodeContent());
        int i=goodsMapper.saveQrCode(storeQrCode);
        Map<String,String> map=new HashMap<>(1);
        map.put("code",storeQrCode.getId().toString());
        if (i>0){
            return DtoUtil.getSuccesWithDataDto("请求成功",map,100000);
        }
        return DtoUtil.getFalseDto("请求失败",91011);
    }

    /**
     * 查询订单二维码信息
     * @param orderInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto queryOrderQrInfo(OrderInfoVo orderInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(orderInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        String codeContent=goodsMapper.queryQrCodeContent(orderInfoVo.getCode());
        Map<String,String> map=new HashMap<>(1);
        map.put("codeContent",codeContent);
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    @Override
    public Dto goodsDownShelf(GoodsDownShelf goodsDownShelf, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsDownShelf.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(goodsDownShelf.getUserId(), goodsDownShelf.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (goodsDownShelf.getGoodsId() == null) {
            return DtoUtil.getFalseDto("参数错误", 90006);
        }
        int i = 0;
        for (String goodsId : goodsDownShelf.getGoodsId()) {
            i += goodsMapper.updateGoodsStatus(goodsId, 0, goodsDownShelf.getStoreId());
        }
        if (i == goodsDownShelf.getGoodsId().length) {
            return DtoUtil.getSuccessDto("操作成功", 100000);
        } else {
            return DtoUtil.getFalseDto("操作失败", 90008);
        }
    }

    @Override
    public Dto getUpdateGoodsInfo(ReceivedGoodsId receivedGoodsId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGoodsId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //如果店铺和用户不是一对一,此处需要传storeId
        Long storeId = storeMapper.getStoreIdByUserId(receivedGoodsId.getUserId());
        GoodsInfoToUpdate goodsInfoToUpdate = goodsMapper.getGoodsInfoToUpdate(receivedGoodsId.getGoodsId(), storeId);
        if (ObjectUtils.isEmpty(goodsInfoToUpdate)) {
            return DtoUtil.getSuccessDto("未查询到数据", 200000);
        }
        List<ShowConsumable> showConsumables = goodsMapper.getGoodsConsumablesList(receivedGoodsId.getGoodsId(), null, 0L, 3L);
        goodsInfoToUpdate.setShowConsumables(showConsumables);
        StoreGoodsCorrelation correlation = goodsMapper.getSonGoodsInfo(receivedGoodsId.getGoodsId());
        if (correlation != null) {
            goodsInfoToUpdate.setCorGoodsId(correlation.getGoodsSonId().toString());
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", goodsInfoToUpdate, 100000);
    }

    @Override
    public Dto getGoodsConsumable(GetGoodsConsumables getGoodsConsumables, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getGoodsConsumables.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreInfo storeInfo = storeMapper.getStoreInfo(getGoodsConsumables.getStoreId());
        getGoodsConsumables.setPageNum(getGoodsConsumables.getPageNum() - 1);
        Map<String, Object> result = new HashMap<>();
        result.put("storeName", storeInfo.getStoreName());
        List<ShowConsumable> consumables = goodsMapper.getGoodsConsumablesList(getGoodsConsumables.getGoodsId(), getGoodsConsumables.getGoodsName(), getGoodsConsumables.getPageNum(), getGoodsConsumables.getPageSize());
        if (consumables.size() == 0) {
            return DtoUtil.getSuccessDto("未找到消耗品", 200000);
        }
        result.put("goodsList", consumables);
        return DtoUtil.getSuccesWithDataDto("获取成功", result, 100000);
    }

    @Override
    public Dto deleteGoodsConsumables(DeleteGoodsConsumables deleteGoodsConsumables, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteGoodsConsumables.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int i = 0;
        for (String consumableId : deleteGoodsConsumables.getConsumableIds()) {
            i += goodsMapper.deleteGoodsConsumable(consumableId);
        }
        if (i == deleteGoodsConsumables.getConsumableIds().length) {
            return DtoUtil.getSuccessDto("删除成功", 100000);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getFalseDto("删除失败", 90008);
    }

    @Override
    public Dto getUpdateConsumableInfo(ReceivedConsumableId receivedConsumableId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedConsumableId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowUpdateConsumableInfo consumable = goodsMapper.getUpdateConsumableInfo(receivedConsumableId.getConsumableId());
        if (ObjectUtils.isEmpty(consumable)) {
            return DtoUtil.getSuccessDto("暂无数据", 200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", consumable, 100000);
    }

    @Override
    public Dto updateConsumable(UpdateConsumable updateConsumable, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateConsumable.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        updateConsumable.setConsumptionRate((double) updateConsumable.getRegisteredRatioIn() / updateConsumable.getRegisteredRatioOut());
        if (goodsMapper.updateConsumable(updateConsumable) == 1) {
            return DtoUtil.getSuccessDto("修改成功", 100000);
        }
        return DtoUtil.getFalseDto("修改失败", 90008);
    }

    /**
     * 查询商铺列表
     * @param getStoreListVo
     * @param token
     * @return
     */
    @Override
    public Dto getStoreList(GetStoreListVo getStoreListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getStoreListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<StoreInfo> storeList=storeMapper.getStoreList();
        List<Map<String,String>> resultMapList=new ArrayList<>();
        for (StoreInfo storeInfo:storeList) {
            Map<String,String> map=new HashMap<>();
            map.put("storeId",storeInfo.getId().toString());
            map.put("storeName",storeInfo.getStoreName());
            map.put("storePicture",storeInfo.getStorePicture());
            map.put("storeAddress",storeInfo.getStoreAddress());
            resultMapList.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",resultMapList,100000);
    }

    /**
     * 扫描条形码获取商品信息
     * @param getGoodsInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto getGoodsInfoByBarCode(GetGoodsInfoVo getGoodsInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getGoodsInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (StringUtils.isEmpty(getGoodsInfoVo.getGoodsBarCode())){
            return DtoUtil.getFalseDto("商品条码格式不正确",91001);
        }
        Map<String,Object> goods=storeMapper.getStoreInfoByBarCode(getGoodsInfoVo.getGoodsBarCode(),getGoodsInfoVo.getStoreId());
        if (ObjectUtils.isEmpty(goods)){
            return DtoUtil.getFalseDto("商品暂未录入",91002);
        }
        Map<String,String> map=new HashMap<>();
        map.put("goodsId",goods.get("id").toString());
        map.put("goodsName",goods.get("goodsName").toString());
        map.put("goodsPrice",goods.get("goodsPrice").toString());
        map.put("goodsPicture",goods.get("goodsPicture").toString());
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    /**
     * 线下交易生成订单
     * @param createOfflineOrderVo
     * @param token
     * @return
     */
    @Override
    public Dto createOfflineOrder(CreateOfflineOrderVo createOfflineOrderVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(createOfflineOrderVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //存下商品信息列表
        StoreQrCode storeQrCode=new StoreQrCode();
        storeQrCode.setId("SQR"+createOfflineOrderVo.getUserId()+System.currentTimeMillis());
        storeQrCode.setCodeContent(createOfflineOrderVo.getCodeContent());
        int i=goodsMapper.saveQrCode(storeQrCode);
        //生成订单信息
        StoreOfflineOrders storeOfflineOrders=new StoreOfflineOrders();
        storeOfflineOrders.setOrderNumber("sot"+createOfflineOrderVo.getStoreId()+createOfflineOrderVo.getUserId()+System.currentTimeMillis());
        storeOfflineOrders.setSourceStoreId(Long.valueOf(createOfflineOrderVo.getStoreId()));
        storeOfflineOrders.setUserId(Long.valueOf(createOfflineOrderVo.getUserId()));
        storeOfflineOrders.setGoodsListId(storeQrCode.getId());
        storeOfflineOrders.setPaymentAmount(Double.valueOf(createOfflineOrderVo.getPaymentAmount()));
        int j=goodsMapper.saveStoreOfflineOrders(storeOfflineOrders);
        //商品添加进购物车并扣减库存
        String result=makeGoodsStockReduce(storeOfflineOrders.getOrderNumber(),storeQrCode.getCodeContent());
        if ("success".equals(result)){
            Map<String,String> map=new HashMap<>(1);
            map.put("code",storeQrCode.getId());
            map.put("orderNumber",storeOfflineOrders.getOrderNumber());
            if ( i>0 && j>0 ){
                return DtoUtil.getSuccesWithDataDto("下单成功",map,100000);
            }
        }
        return DtoUtil.getFalseDto(result,95401);
    }

    /**
     * 商家扫码确认商品信息
     * @param orderInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto checkGoodsList(OrderInfoVo orderInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(orderInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        String codeContent=goodsMapper.queryQrCodeContent(orderInfoVo.getCode());
        Map goods=JSONObject.parseObject(codeContent,Map.class);
        StoreInfo storeInfo=storeMapper.getStoreInfo(goods.get("storeId").toString());
        if (!ObjectUtils.isEmpty(storeInfo)){
            if (orderInfoVo.getUserId().equals(storeInfo.getUserId().toString())){
                Map<String,String> map=new HashMap<>(1);
                map.put("codeContent",codeContent);
                return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
            }
            return DtoUtil.getFalseDto("扫码失败,您不是店主",98013);
        }
        return DtoUtil.getFalseDto("扫码失败，商店信息不存在",98012);
    }

    /**
     * 商家确认订单
     * @param checkOrderVo
     * @param token
     * @return
     */
    @Override
    public Dto checkOrder(CheckOrderVo checkOrderVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(checkOrderVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //发送信息让用户去支付
        try {
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            RefreshMsg refreshMsg=new RefreshMsg("3");
            rongCloudMethodUtil.sendSystemMessage(checkOrderVo.getUserId(),new String[]{checkOrderVo.getTargetId()},refreshMsg,"","");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("发送消息失败",95400);
        }
        return DtoUtil.getSuccessDto("发送成功",100000);
    }

    /**
     * 商家扫描付款码完成交易
     * @param merchantGatheringVo
     * @param token
     * @return
     */
    @Override
    public Dto merchantGathering(MerchantGatheringVo merchantGatheringVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(merchantGatheringVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreOfflineOrders offlineOrders = goodsMapper.getOfflineOrderByGoodsCode(merchantGatheringVo.getCode());
        boolean flag=false;
        String payType=null;
        Dto dto=null;
        try {
            if (offlineOrders.getOrderStatus()!=0){
                return DtoUtil.getFalseDto("订单号无效请重新下单",26410);
            }
            StoreInfo storeInfo=storeMapper.getStoreInfo(offlineOrders.getSourceStoreId().toString());
            int authCode=Integer.valueOf(merchantGatheringVo.getAuthCode().substring(0,2));
            if (authCode>=25 && authCode<=30){
                //支付宝
                dto=PaymentCodeUtil.aliPaymentCodeToPay(merchantGatheringVo.getAuthCode(),offlineOrders.getOrderNumber(),offlineOrders.getPaymentAmount(),storeInfo.getStoreName(),offlineOrders.getSourceStoreId().toString());
                if (dto.getResCode()==100000){
                    flag=true;
                }
                payType="支付宝支付";
            }else if (authCode>=10 && authCode<=15){
                //微信
                dto=PaymentCodeUtil.wxPaymentCodeToPay(merchantGatheringVo.getAuthCode(),offlineOrders.getOrderNumber(),offlineOrders.getPaymentAmount(),storeInfo.getStoreName());
                if (dto.getResCode()==100000){
                    flag=true;
                }
                payType="微信支付";
            }else {
                return DtoUtil.getFalseDto("付款码错误",22301);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        //如果支付成功
        if (flag){
            //修改订单状态
            //此处处理商铺模块用户线下扫码支付
            offlineOrders.setOrderStatus(2L);
            offlineOrders.setPayTime(System.currentTimeMillis());
            offlineOrders.setPayChannel(payType);
            offlineOrders.setOutTradeNo(offlineOrders.getOrderNumber());
            goodsMapper.updateOfflineOrder(offlineOrders);
            //支付成功增加商铺余额
            storeMapper.updWallet(offlineOrders.getPaymentAmount(),offlineOrders.getSourceStoreId());
            //订单成功后解析已卖出商品并将数量记录到销量表中
            //(因为要根据时间计算商品销量,所以该表中同一商铺下会有多个商品及对应数量)
            List<Map> goodsList = JSONObject.parseArray(goodsMapper.getTemStock(offlineOrders.getOrderNumber()),Map.class);
            for (Map goods : goodsList){
                StoreSalesVolume storeSalesVolume = new StoreSalesVolume();
                storeSalesVolume.setGoodsId(goodsMapper.getGoodsStockByGoodsBarCode(offlineOrders.getSourceStoreId().toString(),goods.get("goodsBarCode").toString()).getGoodsId().toString());
                storeSalesVolume.setNum(Long.valueOf(goods.get("num").toString()));
                storeSalesVolume.setStoreId(offlineOrders.getSourceStoreId().toString());
                storeSalesVolume.setOrderNumber(offlineOrders.getOrderNumber());
                goodsMapper.addNewSalesVolume(storeSalesVolume);
            }
            return DtoUtil.getFalseDto("支付成功",100000);
        }else {
            //返还库存
            String temStock = goodsMapper.getTemStock(offlineOrders.getOrderNumber());
            List<Map> result = JSONObject.parseArray(temStock,Map.class);
            for (Map map : result){
                String storeId = (String) map.get("storeId");
                String goodsBarCode = (String) map.get("goodsBarCode");
                Object num = map.get("num");
                goodsMapper.resumeStock(storeId,goodsBarCode,num);
            }
            //修改订单状态
            goodsMapper.updateOfflineOrderStatus(offlineOrders.getOrderNumber(),5);
            return DtoUtil.getFalseDto("支付失败，请重新下单",23202);
        }
    }


    @Override
    public synchronized Dto addNewConsumable(AddNewConsumable addNewConsumable, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(addNewConsumable.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods consumableInfo = goodsMapper.getGoodsInfo(addNewConsumable.getConsumablesId());
        StoreGoods goods = goodsMapper.getGoodsInfo(addNewConsumable.getGoodsId());
        StoreGoodsConsumable consumable = new StoreGoodsConsumable();
        consumable.setGoodsId(Long.valueOf(addNewConsumable.getGoodsId()));
        consumable.setConsumableGoodsId(Long.valueOf(addNewConsumable.getConsumablesId()));
        consumable.setRegisteredRationInUnit(consumableInfo.getGoodsUnit());
        consumable.setRegisteredRatioIn(addNewConsumable.getConsumablesNum());
        consumable.setRegisteredRationOutUnit(goods.getGoodsUnit());
        consumable.setRegisteredRatioOut(Long.valueOf(addNewConsumable.getFinishedNum()));
        consumable.setRegisteredTime(System.currentTimeMillis() / 1000);
        consumable.setConsumptionRate(consumable.getRegisteredRatioIn() / consumable.getRegisteredRatioOut());
        if (goodsMapper.addNewGoodsConsumable(consumable) == 1) {
            return DtoUtil.getSuccessDto("添加成功", 100000);
        } else {
            return DtoUtil.getFalseDto("添加失败", 90013);
        }
    }

    @Override
    public synchronized Dto wxOfflinePay(ReceivedOrderNumber receivedOrderNumber, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderNumber.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreOfflineOrders offlineOrders = goodsMapper.getOfflineOrder(receivedOrderNumber.getOrderNumber());
        try {
            return PayUtil.wxOrderMaker(offlineOrders.getOrderNumber(), offlineOrders.getPaymentAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getFalseDto("生成订单异常", 60013);
    }

    @Override
    public synchronized Dto aliOfflinePay(ReceivedOrderNumber receivedOrderNumber, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderNumber.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreOfflineOrders offlineOrders = goodsMapper.getOfflineOrder(receivedOrderNumber.getOrderNumber());
        try {
            return PayUtil.aliOrderMaker(offlineOrders.getOrderNumber(), "线下", offlineOrders.getPaymentAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getFalseDto("生成订单异常", 60013);
    }

    @Override
    public Dto getGoodsTracking(GetGoodsTracking getGoodsTracking, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getGoodsTracking.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getGoodsTracking.getUserId(), getGoodsTracking.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        List<Map> result = new ArrayList<>();
        List<StorePurchaseRecords> orderNumbers = goodsMapper.getOrderNumbersGroupByOrderNumber(getGoodsTracking.getStoreId(), getGoodsTracking.getStoreName(),
                getGoodsTracking.getPageNum() - 1, getGoodsTracking.getPageSize());
        if (orderNumbers.size() == 0) {
            return DtoUtil.getSuccessDto("暂无数据", 200000);
        }
        for (StorePurchaseRecords records : orderNumbers) {
            List<StorePurchaseRecords> recordsList = goodsMapper.getPurchaseRecordsByOrderNumber(records.getOrderNumber().toString(), null, null, null);
            StoreInfo storeInfo = storeMapper.getStoreInfo(records.getTargetStoreId().toString());
            Map<String, Object> order = new HashMap<>();
            order.put("storeName", storeInfo.getStoreName());
            order.put("storePicture", storeInfo.getStorePicture());
            result.add(getStoreFirstGoods(order, recordsList, getGoodsTracking.getStoreId(), storeInfo.getId().toString()));
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public Dto getGoodsTrackingInStore(GetGoodsTrackingInStore getGoodsTrackingInStore, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getGoodsTrackingInStore.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getGoodsTrackingInStore.getUserId(), getGoodsTrackingInStore.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<StorePurchaseRecords> records = goodsMapper.getPurchaseRecordsByOrderNumber(getGoodsTrackingInStore.getOrderNumber(), getGoodsTrackingInStore.getGoodsName(),
                getGoodsTrackingInStore.getPageNum(), getGoodsTrackingInStore.getPageSize());
        if (records.size() == 0) {
            return DtoUtil.getSuccessDto("暂无数据", 200000);
        }
        List<Map<String, Object>> goodsList = new ArrayList<>();
        StorePurchaseRecords temp;
        for (int i = 0; i < records.size() - 1; i++) {
            for (int j = 0; j < records.size() - i - 1; j++) {
                if (Long.valueOf(records.get(j + 1).getGoodsCount().toString()) < Long.valueOf(records.get(j).getGoodsCount().toString())) {
                    temp = records.get(j);
                    records.set(j, records.get(j + 1));
                    records.set(j + 1, temp);
                }
            }
        }
        for (StorePurchaseRecords record : records) {
            Map<String, Object> goods = new HashMap<>();
            Date time = goodsMapper.getGoodsFirstPurchaseTime(getGoodsTrackingInStore.getStoreId(), getGoodsTrackingInStore.getTargetStoreId(), record.getGoodsId());
            StoreGoodsStock storeGoodsStock = goodsMapper.getGoodsStock(record.getGoodsId().toString(), getGoodsTrackingInStore.getTargetStoreId());
            StoreGoods storeGoods = goodsMapper.getGoodsInfo(record.getChangeGoodsId().toString());
            Map salesVolume = goodsMapper.getSalesVolumeByCreateTime(record.getChangeGoodsId().toString(), time);
            goods.put("goodsId", storeGoods.getId());
            goods.put("goodsName", storeGoods.getGoodsName());
            goods.put("purchaseNum", record.getGoodsCount());
            goods.put("purchaseUnit", storeGoods.getFaUnitNum() == 0 ? storeGoods.getGoodsUnit() : storeGoods.getGoodsFUnit());
            goods.put("soldNum", ObjectUtils.isEmpty(salesVolume) ? 0 : salesVolume.get("num"));
            goods.put("soldUnit", storeGoods.getFaUnitNum() == 0 ? storeGoods.getGoodsUnit() : storeGoods.getGoodsFUnit());
            goods.put("stock", ObjectUtils.isEmpty(storeGoodsStock) ? 0 : storeGoodsStock.getStockNum());
            goods.put("createTime", simpleDateFormat.format(record.getCreateDate()));
            goodsList.add(goods);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", goodsList, 100000);
    }

    @Override
    public Dto getManageGoods(ReceivedStoreId receivedStoreId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedStoreId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(receivedStoreId.getUserId(), receivedStoreId.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("forSale",goodsMapper.getForSaleGoodsNum(receivedStoreId.getStoreId()));
        result.put("soldOut",goodsMapper.getSoldOutGoodsNum(receivedStoreId.getStoreId()));
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public Dto getManageGoodsByType(GetManageGoodsByType getManageGoodsByType, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getManageGoodsByType.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getManageGoodsByType.getUserId(), getManageGoodsByType.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        getManageGoodsByType.setPageNum(getManageGoodsByType.getPageNum() - 1);
        if ("forSale".equals(getManageGoodsByType.getGetType())){
            List<Map<String,Object>> list = goodsMapper.getForSaleGoodsList(getManageGoodsByType.getStoreId(),getManageGoodsByType.getPageNum(),
                    getManageGoodsByType.getPageSize(),getManageGoodsByType.getGoodsName());
            if (list.size() == 0){
                return DtoUtil.getSuccessDto("暂无数据",200000);
            }
            return DtoUtil.getSuccesWithDataDto("查询成功",list,100000);
        }else if ("soldOut".equals(getManageGoodsByType.getGetType())){
            List<Map<String,Object>> list = goodsMapper.getSoldOutGoodsList(getManageGoodsByType.getStoreId(),getManageGoodsByType.getPageNum(),
                    getManageGoodsByType.getPageSize(),getManageGoodsByType.getGoodsName());
            if (list.size() == 0){
                return DtoUtil.getSuccessDto("暂无数据",200000);
            }
            return DtoUtil.getSuccesWithDataDto("查询成功",list,100000);
        }else {
            return DtoUtil.getFalseDto("缺少参数gt",90016);
        }
    }

    @Override
    public Dto getManageGoodsGroupByGoodsType(ReceivedStoreId receivedStoreId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedStoreId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(receivedStoreId.getUserId(), receivedStoreId.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        List<Long> typeIds = goodsMapper.getMyGoodsTypes(receivedStoreId.getStoreId());
        if (typeIds.size() == 0){
            return DtoUtil.getSuccessDto("暂无数据",200000);
        }
        List<Map<String,Object>> result = new ArrayList<>();
        for (int i = 0; i < typeIds.size(); i++) {
            Long typeId = typeIds.get(i);
            Map<String,Object> type = new HashMap<>();
            if (i == 0){
                type.put("selected",true);
            }else {
                type.put("selected",false);
            }
            type.put("typeName",goodsMapper.getTypeName(typeId));
            type.put("goodsTypeId",typeId);
            type.put("num",goodsMapper.getManageGoodsGroupByGoodsTypeIdNum(receivedStoreId.getStoreId(), typeId));
            result.add(type);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public Dto getManageGoodsWithGoodsType(GetManageGoodsWithGoodsType getManageGoodsWithGoodsType, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getManageGoodsWithGoodsType.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getManageGoodsWithGoodsType.getUserId(), getManageGoodsWithGoodsType.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        List<Map<String, Object>> manageGoodsGroupByGoodsTypeId = goodsMapper.getManageGoodsGroupByGoodsTypeId(getManageGoodsWithGoodsType.getStoreId(), Long.valueOf(getManageGoodsWithGoodsType.getGoodsTypeId()));
        if (manageGoodsGroupByGoodsTypeId.size() == 0){
            return DtoUtil.getSuccessDto("暂无数据",200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",manageGoodsGroupByGoodsTypeId,100000);
    }

    @Override
    public Dto getManagePriceByType(GetManagePriceByType getManagePriceByType, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getManagePriceByType.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getManagePriceByType.getUserId(), getManagePriceByType.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        getManagePriceByType.setPageNum(getManagePriceByType.getPageNum() - 1);
        if ("priced".equals(getManagePriceByType.getGetType())){
            List<Map<String,Object>> list = goodsMapper.getPricedGoodsList(getManagePriceByType.getStoreId(),getManagePriceByType.getPageNum(),
                    getManagePriceByType.getPageSize());
            if (list.size() == 0){
                return DtoUtil.getSuccessDto("暂无数据",200000);
            }
            return DtoUtil.getSuccesWithDataDto("查询成功",list,100000);
        }else if ("noPricing".equals(getManagePriceByType.getGetType())){
            List<Map<String,Object>> list = goodsMapper.getNoPricingGoodsList(getManagePriceByType.getStoreId(),getManagePriceByType.getPageNum(),
                    getManagePriceByType.getPageSize());
            if (list.size() == 0){
                return DtoUtil.getSuccessDto("暂无数据",200000);
            }
            return DtoUtil.getSuccesWithDataDto("查询成功",list,100000);
        }else {
            return DtoUtil.getFalseDto("缺少参数gt",90016);
        }
    }

    @Override
    public synchronized Dto deleteGoods(DeleteGoods deleteGoods, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteGoods.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(deleteGoods.getUserId(), deleteGoods.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        for (String goodsId : deleteGoods.getGoodsId()){
            if (goodsMapper.deleteGoods(deleteGoods.getStoreId(),goodsId) != 1){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("删除失败",90017);
            }
        }
        return DtoUtil.getSuccessDto("删除成功",100000);
    }

    @Override
    public synchronized Dto goodsDiscountPromoteSales(GoodsDiscountPromoteSales goodsDiscountPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsDiscountPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(goodsDiscountPromoteSales.getUserId(), goodsDiscountPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (!verGoodsPromoteSales(goodsDiscountPromoteSales.getGoodsId(),goodsDiscountPromoteSales.getStartTime(),
                goodsDiscountPromoteSales.getEndTime(),goodsDiscountPromoteSales.getStoreId())){
            return DtoUtil.getFalseDto("请合理安排商品及促销时间!",90027);
        }
        int verValue = goodsMapper.verGoodsPromoteSalesRepetitive(goodsDiscountPromoteSales.getGoodsId(),goodsDiscountPromoteSales.getStartTime(),
                goodsDiscountPromoteSales.getEndTime(),goodsDiscountPromoteSales.getStoreId(),
                System.currentTimeMillis()/1000,2);
        if (verValue >= 1){
            return DtoUtil.getFalseDto("同一商品只能参与一种折扣方式,请勿重复添加",90028);
        }
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMaximumFractionDigits(2);
        goodsDiscountPromoteSales.setValue(Double.valueOf(nf.format(goodsDiscountPromoteSales.getValue() / 10)));
        if (goodsDiscountPromoteSales.getValue() >= 1){
            return DtoUtil.getFalseDto("请输入有效的折扣",90020);
        }
        String id = null;
        String[] goodsId1 = goodsDiscountPromoteSales.getGoodsId();
        for (int i = 0; i < goodsId1.length; i++) {
            String goodsId = goodsId1[i];
            AddNewGoodsPromoteSales addNewGoodsPromoteSales = new AddNewGoodsPromoteSales();
            addNewGoodsPromoteSales.setGoodsId(goodsId);
            addNewGoodsPromoteSales.setValue(goodsDiscountPromoteSales.getValue() + "");
            addNewGoodsPromoteSales.setDiscountedType(1);
            addNewGoodsPromoteSales.setStartTime(goodsDiscountPromoteSales.getStartTime());
            addNewGoodsPromoteSales.setEndTime(goodsDiscountPromoteSales.getEndTime());
            addNewGoodsPromoteSales.setStoreId(goodsDiscountPromoteSales.getStoreId());
            int i2 = goodsMapper.addNewGoodsPromoteSales(addNewGoodsPromoteSales);
            if (i2 != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //angdf:adding new goods discount failed
                return DtoUtil.getFalseDto("操作失败angdf", 90019);
            }
            if (i == 0) {
                id = addNewGoodsPromoteSales.getId();
            }
            int i2_1 = goodsMapper.addBindingIdToGoodsDiscount(id,addNewGoodsPromoteSales.getId());
            if (i2_1 == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //angdf:adding binding goods discount failed
                return DtoUtil.getFalseDto("操作失败abgdf", 90019);
            }
        }

        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public synchronized Dto goodsFullReductionPromoteSales(GoodsFullReductionPromoteSales goodsFullReductionPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsFullReductionPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(goodsFullReductionPromoteSales.getUserId(), goodsFullReductionPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (!verGoodsPromoteSales(goodsFullReductionPromoteSales.getGoodsId(),goodsFullReductionPromoteSales.getStartTime(),
                goodsFullReductionPromoteSales.getEndTime(),goodsFullReductionPromoteSales.getStoreId())){
            return DtoUtil.getFalseDto("请合理安排商品及促销时间!",90027);
        }
        int verValue = goodsMapper.verGoodsPromoteSalesRepetitive(goodsFullReductionPromoteSales.getGoodsId(),goodsFullReductionPromoteSales.getStartTime(),
                goodsFullReductionPromoteSales.getEndTime(),goodsFullReductionPromoteSales.getStoreId(),
                System.currentTimeMillis()/1000,1);
        if (verValue >= 1){
            return DtoUtil.getFalseDto("同一商品只能参与一种折扣方式,请勿重复添加",90028);
        }
        Double[] fullValues = goodsFullReductionPromoteSales.getFullValue();
        Double[] disValues = goodsFullReductionPromoteSales.getDisValue();
        if (fullValues.length != disValues.length){
            //plau:params length are unequal
            return DtoUtil.getFalseDto("操作失败plau",90024);
        }
        for (int i = 0; i < fullValues.length; i++) {
            if (disValues[i] > fullValues[i]){
                return DtoUtil.getFalseDto("折扣金额不能大于消费金额",90022);
            }
        }
        String id = null;
        String[] goodsId1 = goodsFullReductionPromoteSales.getGoodsId();
        for (int i = 0; i < goodsId1.length; i++) {
            String goodsId = goodsId1[i];
            AddNewGoodsPromoteSales addNewGoodsPromoteSales = new AddNewGoodsPromoteSales();
            addNewGoodsPromoteSales.setGoodsId(goodsId);
            addNewGoodsPromoteSales.setValue("-1");
            addNewGoodsPromoteSales.setDiscountedType(2);
            addNewGoodsPromoteSales.setStartTime(goodsFullReductionPromoteSales.getStartTime());
            addNewGoodsPromoteSales.setEndTime(goodsFullReductionPromoteSales.getEndTime());
            addNewGoodsPromoteSales.setStoreId(goodsFullReductionPromoteSales.getStoreId());
            int i2 = goodsMapper.addNewGoodsPromoteSales(addNewGoodsPromoteSales);
            if (i2 != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //angdf:adding new goods discount failed
                return DtoUtil.getFalseDto("操作失败angdf", 90019);
            }
            if (i == 0){
                id = addNewGoodsPromoteSales.getId();
            }
            int i2_1 = goodsMapper.addBindingIdToGoodsDiscount(id,addNewGoodsPromoteSales.getId());
            if (i2_1 == 0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //angdf:adding binding goods discount failed
                return DtoUtil.getFalseDto("操作失败abgdf", 90019);
            }
        }
        for (int i = 0; i < fullValues.length; i++) {
            int i3 = goodsMapper.addNewFullReduction(id,fullValues[i],disValues[i],
                    goodsFullReductionPromoteSales.getStartTime(),goodsFullReductionPromoteSales.getEndTime(),
                    goodsFullReductionPromoteSales.getStoreId());
            if (i3 != 1){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //angfrf:adding new goods full reduction failed
                return DtoUtil.getFalseDto("操作失败angfrf",90023);
            }
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public Dto goodsPromoteSalesVerify(GoodsPromoteSalesVerify goodsPromoteSalesVerify, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(goodsPromoteSalesVerify.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(goodsPromoteSalesVerify.getUserId(), goodsPromoteSalesVerify.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (goodsMapper.verifyGoodsExistInSGD(goodsPromoteSalesVerify.getGoodsId(),System.currentTimeMillis()/1000,goodsPromoteSalesVerify.getStoreId()) >= 1){
            return DtoUtil.getFalseDto("存在促销中的商品,请重新选择",90021);
        }
        return DtoUtil.getSuccessDto("请求成功",100000);
    }

    @Override
    public Dto showGoodsPromoteSales(ReceivedStoreId receivedStoreId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedStoreId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(receivedStoreId.getUserId(), receivedStoreId.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        List<String> bindingIds = goodsMapper.getGoodsPromoteSalesBindingIds(receivedStoreId.getStoreId(),System.currentTimeMillis()/1000);
        if (bindingIds.size() == 0){
            return DtoUtil.getSuccessDto("暂无数据",200000);
        }
        List<ShowPromoteSalesInfo> result = new ArrayList<>();
        for (String bindingId : bindingIds){
            List<StoreGoodsDiscount> discounts = goodsMapper.getGoodsPromoteSalesInfo(bindingId);
            if (discounts.size() == 0){
                return DtoUtil.getFalseDto("数据异常",90029);
            }
            StoreGoodsDiscount sample = discounts.get(0);
            ShowPromoteSalesInfo salesInfo = new ShowPromoteSalesInfo();
            salesInfo.setPromoteSalesId(sample.getId());
            if (sample.getDiscountedType() == 2){
                StringBuffer disInfo = new StringBuffer();
                List<StoreGoodsFullReduction> reductions = goodsMapper.getGoodsFullReduction(bindingId);
                for (int i = 0; i < reductions.size(); i++) {
                    StoreGoodsFullReduction reduction = reductions.get(i);
                    if (i != 0){
                        disInfo.append(",");
                    }
                    disInfo.append("满").append(reduction.getFullValue()).append("减").append(reduction.getDisValue());
                }
                salesInfo.setDisInfo(disInfo.toString());
                salesInfo.setSelectedInfo("已选" + discounts.size() + "件商品参加" + "满减" + "活动");
                salesInfo.setType("4");
            }else if (sample.getDiscountedType() == 1){
                salesInfo.setSelectedInfo("已选" + discounts.size() + "件商品" + sample.getValue() * 10 + "折");
                salesInfo.setType("3");
            }
            salesInfo.setStartTime(sample.getStartTime());
            salesInfo.setEndTime(sample.getEndTime());
            salesInfo.setStatus(sample.getStartTime() >= System.currentTimeMillis()/1000 ? "0" : "1");
            salesInfo.setPromoteType("2");
            result.add(salesInfo);
        }
        StoreUtils.sortPromoteSalesInfo(result);
        return DtoUtil.getSuccesWithDataDto("success",result,100000);
    }

    @Override
    public Dto showAllOverduePromoteSales(ReceivedStoreId receivedStoreId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedStoreId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(receivedStoreId.getUserId(), receivedStoreId.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        //商品促销
        List<String> bindingIds = goodsMapper.getGoodsOverduePromoteSalesBindingIds(receivedStoreId.getStoreId(),System.currentTimeMillis()/1000);
        //店铺促销
        List<String> storeTimes = storeMapper.getStoreOverduePromoteSalesTimes(receivedStoreId.getStoreId(),System.currentTimeMillis()/1000);
        if (bindingIds.size() == 0 && storeTimes.size() == 0){
            return DtoUtil.getFalseDto("暂无数据",200000);
        }
        List<ShowPromoteSalesInfo> result = new ArrayList<>();
        for (String bindingId : bindingIds){
            List<StoreGoodsDiscount> discounts = goodsMapper.getGoodsPromoteSalesInfo(bindingId);
            if (discounts.size() == 0){
                return DtoUtil.getFalseDto("数据异常",90029);
            }
            StoreGoodsDiscount sample = discounts.get(0);
            ShowPromoteSalesInfo salesInfo = new ShowPromoteSalesInfo();
            salesInfo.setPromoteSalesId(sample.getId());
            if (sample.getDiscountedType() == 2){
                StringBuffer disInfo = new StringBuffer();
                List<StoreGoodsFullReduction> reductions = goodsMapper.getGoodsFullReduction(bindingId);
                for (int i = 0; i < reductions.size(); i++) {
                    StoreGoodsFullReduction reduction = reductions.get(i);
                    if (i != 0){
                        disInfo.append(",");
                    }
                    disInfo.append("满").append(reduction.getFullValue()).append("减").append(reduction.getDisValue());
                }
                salesInfo.setDisInfo(disInfo.toString());
                salesInfo.setSelectedInfo("已选" + discounts.size() + "件商品参加" + "满减" + "活动");
                salesInfo.setType("4");
            }else if (sample.getDiscountedType() == 1){
                salesInfo.setSelectedInfo("已选" + discounts.size() + "件商品" + sample.getValue() * 10 + "折");
                salesInfo.setType("3");
            }
            salesInfo.setStartTime(sample.getStartTime());
            salesInfo.setEndTime(sample.getEndTime());
            salesInfo.setStatus("2");
            salesInfo.setPromoteType("2");
            result.add(salesInfo);
        }
        for (String time : storeTimes){
            List<StoreFullReduction> reductions = storeMapper.getStorePromoteSalesInfo(receivedStoreId.getStoreId(),time);
            ShowPromoteSalesInfo salesInfo = new ShowPromoteSalesInfo();
            if (reductions.size() == 0){
                return DtoUtil.getFalseDto("数据异常",90029);
            }
            StoreFullReduction sample = reductions.get(0);
            salesInfo.setPromoteSalesId(sample.getId());
            if (sample.getDiscountedType() == 2){
                StringBuffer disInfo = new StringBuffer();
                for (int i = 0; i < reductions.size(); i++) {
                    StoreFullReduction reduction = reductions.get(i);
                    if (i != 0){
                        disInfo.append(",");
                    }
                    disInfo.append("满").append(reduction.getFullValue()).append("减").append(reduction.getDisValue());
                }
                salesInfo.setDisInfo(disInfo.toString());
            }else if (sample.getDiscountedType() == 1){
                salesInfo.setDisInfo("全场商品" + sample.getFullValue() * 10 + "折");
            }
            salesInfo.setStartTime(sample.getStartTime());
            salesInfo.setEndTime(sample.getEndTime());
            salesInfo.setStatus("2");
            salesInfo.setType(sample.getDiscountedType().toString());
            salesInfo.setPromoteType("1");
            result.add(salesInfo);
        }
        StoreUtils.sortAllOverduePromoteSalesInfo(result);
        return DtoUtil.getSuccesWithDataDto("success",result,100000);
    }

    @Override
    public synchronized Dto deleteGoodsPromoteSales(DeleteGoodsPromoteSales deleteGoodsPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteGoodsPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(deleteGoodsPromoteSales.getUserId(), deleteGoodsPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        StoreGoodsDiscount discount = goodsMapper.getStoreGoodsDiscount(deleteGoodsPromoteSales.getPromoteSalesId());
        if (discount.getEndTime() < System.currentTimeMillis()/1000){
            return DtoUtil.getFalseDto("促销已过期,无法删除",90030);
        }
        int i1 = goodsMapper.deleteGoodsPromoteSales(deleteGoodsPromoteSales.getPromoteSalesId());
        if (i1 == 0){
            return DtoUtil.getFalseDto("删除失败",90031);
        }
        if (discount.getDiscountedType() == 2){
            int i2 = goodsMapper.deleteStoreGoodsFullReduction(discount.getId());
            if (i2 == 0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("删除失败",90031);
            }
        }
        return DtoUtil.getSuccessDto("删除成功",100000);
    }

    @Override
    public Dto getUpdateGoodsPromoteSales(GetUpdateGoodsPromoteSales getUpdateGoodsPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getUpdateGoodsPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getUpdateGoodsPromoteSales.getUserId(), getUpdateGoodsPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        Map<String,Object> result = new HashMap<>();
        List<StoreGoodsDiscount> storeGoodsDiscounts = goodsMapper.getGoodsPromoteSalesInfo(getUpdateGoodsPromoteSales.getPromoteSalesId());
        if (storeGoodsDiscounts.size() == 0){
            return DtoUtil.getFalseDto("未检测到数据",200000);
        }
        StoreGoodsDiscount storeGoodsDiscount = storeGoodsDiscounts.get(0);
        result.put("promoteSalesId",getUpdateGoodsPromoteSales.getPromoteSalesId());
        result.put("discountedType",storeGoodsDiscount.getDiscountedType());
        result.put("selectedInfo","已选择"+storeGoodsDiscounts.size() + "件商品");
        if (storeGoodsDiscount.getDiscountedType() == 1){
            result.put("value",storeGoodsDiscount.getValue() * 10);
            result.put("fullValues",new ArrayList<>());
            result.put("disValues",new ArrayList<>());
        }else if (storeGoodsDiscount.getDiscountedType() == 2){
            result.put("value",0);
            List<Double> fullValues = new ArrayList<>();
            List<Double> disValues = new ArrayList<>();
            List<StoreGoodsFullReduction> goodsFullReductions = goodsMapper.getGoodsFullReduction(getUpdateGoodsPromoteSales.getPromoteSalesId());
            for (StoreGoodsFullReduction reduction : goodsFullReductions){
                fullValues.add(reduction.getFullValue());
                disValues.add(reduction.getDisValue());
            }
            result.put("fullValues",fullValues);
            result.put("disValues",disValues);
        }else {
            //discountedType is wrong
            return DtoUtil.getFalseDto("参数有误diw",90033);
        }
        result.put("share",1);
        result.put("startTime",storeGoodsDiscount.getStartTime());
        result.put("endTime",storeGoodsDiscount.getEndTime());
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public Dto updateGoodsPromoteSales(UpdateGoodsPromoteSales updateGoodsPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateGoodsPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(updateGoodsPromoteSales.getUserId(), updateGoodsPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        List<String> totalGoodsIds = new ArrayList<>();
        if (updateGoodsPromoteSales.getGoodsId().length == 0){
            List<StoreGoodsDiscount> discounts = goodsMapper.getGoodsPromoteSalesInfo(updateGoodsPromoteSales.getPromoteSalesId());
            for (StoreGoodsDiscount t : discounts){
                totalGoodsIds.add(t.getGoodsId()+"");
            }
        }else {
            totalGoodsIds.addAll(Arrays.asList(updateGoodsPromoteSales.getGoodsId()));
        }
        if (!verUpdateGoodsPromoteSales(totalGoodsIds.toArray(new String[totalGoodsIds.size()]), updateGoodsPromoteSales.getStartTime(),
                updateGoodsPromoteSales.getEndTime(), updateGoodsPromoteSales.getStoreId(), updateGoodsPromoteSales.getPromoteSalesId())) {
            return DtoUtil.getFalseDto("请合理安排商品及促销时间!", 90027);
        }
        int verValue = goodsMapper.verUpdateGoodsPromoteSalesRepetitive(totalGoodsIds.toArray(new String[totalGoodsIds.size()]), updateGoodsPromoteSales.getStartTime(),
                updateGoodsPromoteSales.getEndTime(), updateGoodsPromoteSales.getStoreId(),
                System.currentTimeMillis() / 1000, updateGoodsPromoteSales.getDiscountedType() == 2 ? 1 : 2, updateGoodsPromoteSales.getPromoteSalesId());
        if (verValue >= 1) {
            return DtoUtil.getFalseDto("同一商品只能参与一种折扣方式,请勿重复添加", 90028);
        }
        int d = goodsMapper.deleteGoodsPromoteSales(updateGoodsPromoteSales.getPromoteSalesId());
        if (d == 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //delete promote sales failed
            return DtoUtil.getFalseDto("操作失败dpsf",90033);
        }
        goodsMapper.deleteStoreGoodsFullReduction(Long.valueOf(updateGoodsPromoteSales.getPromoteSalesId()));
        if (updateGoodsPromoteSales.getDiscountedType() == 1){
            GoodsDiscountPromoteSales discount = new GoodsDiscountPromoteSales();
            discount.setValue(updateGoodsPromoteSales.getValue());
            discount.setEndTime(updateGoodsPromoteSales.getEndTime());
            discount.setGoodsId(totalGoodsIds.toArray(new String[totalGoodsIds.size()]));
            discount.setStartTime(updateGoodsPromoteSales.getStartTime());
            discount.setStoreId(updateGoodsPromoteSales.getStoreId());
            discount.setUserId(updateGoodsPromoteSales.getUserId());
            return goodsDiscountPromoteSales(discount,token);
        }else if (updateGoodsPromoteSales.getDiscountedType() == 2){
            GoodsFullReductionPromoteSales reduction = new GoodsFullReductionPromoteSales();
            reduction.setEndTime(updateGoodsPromoteSales.getEndTime());
            reduction.setGoodsId(totalGoodsIds.toArray(new String[totalGoodsIds.size()]));
            reduction.setStartTime(updateGoodsPromoteSales.getStartTime());
            reduction.setStoreId(updateGoodsPromoteSales.getStoreId());
            reduction.setUserId(updateGoodsPromoteSales.getUserId());
            reduction.setFullValue(updateGoodsPromoteSales.getFullValues());
            reduction.setDisValue(updateGoodsPromoteSales.getDisValues());
            return goodsFullReductionPromoteSales(reduction,token);
        }else {
            //discountedType is wrong
            return DtoUtil.getFalseDto("参数有误diw",90033);
        }
    }

    @Override
    public Dto getUpdatePromoteSalesGoodsList(GetUpdatePromoteSalesGoodsList getUpdatePromoteSalesGoodsList, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getUpdatePromoteSalesGoodsList.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(getUpdatePromoteSalesGoodsList.getUserId(), getUpdatePromoteSalesGoodsList.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        List<String> goodsIds = new ArrayList<>();
        List<StoreGoodsDiscount> discounts = goodsMapper.getGoodsPromoteSalesInfo(getUpdatePromoteSalesGoodsList.getPromoteSalesId());
        for (StoreGoodsDiscount t : discounts){
            goodsIds.add(t.getGoodsId()+"");
        }
        getUpdatePromoteSalesGoodsList.setPageNum(getUpdatePromoteSalesGoodsList.getPageNum() - 1);
        List<ShowGetUpdatePromoteSalesGoodsList> result = goodsMapper.getUpdatePromoteSalesGoodsList(getUpdatePromoteSalesGoodsList.getStoreId(),getUpdatePromoteSalesGoodsList.getGoodsName(),
                getUpdatePromoteSalesGoodsList.getPageNum(),getUpdatePromoteSalesGoodsList.getPageSize());
        if (result.size() == 0){
            return DtoUtil.getFalseDto("未检测到数据",200000);
        }
        for (String goodsId : goodsIds){
            for (ShowGetUpdatePromoteSalesGoodsList list : result){
                if (list.getGoodsId().equals(goodsId)){
                    list.setSelectStatus(true);
                    break;
                }
            }
        }
        StoreUtils.sortGoodsListBySelectStatus(result);
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public Dto getManagePriceNum(ReceivedStoreId receivedStoreId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedStoreId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(receivedStoreId.getUserId(), receivedStoreId.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        Map<String,Long> result = new HashMap<>();
        result.put("priced",goodsMapper.getPricedGoodsNumByStatus(receivedStoreId.getStoreId(), 1));
        result.put("noPricing",goodsMapper.getPricedGoodsNumByStatus(receivedStoreId.getStoreId(), 3));
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 验证选中的商品和选中的促销时间是否与已添加过的冲突
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @param storeId
     * @return false:存在冲突
     */
    private boolean verGoodsPromoteSales(String[] goodsIds, Long startTime, Long endTime, String storeId){
        int i = goodsMapper.verGoodsPromoteSales(goodsIds,startTime,endTime,storeId,System.currentTimeMillis()/1000);
        return i < 1;
    }

    /**
     * 验证选中的商品和选中的促销时间是否与已添加过的冲突(除去要修改的本身)
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @param storeId
     * @return false:存在冲突
     */
    private boolean verUpdateGoodsPromoteSales(String[] goodsIds, Long startTime, Long endTime, String storeId, String bindingId){
        int i = goodsMapper.verUpdateGoodsPromoteSales(goodsIds,startTime,endTime,storeId,System.currentTimeMillis()/1000,bindingId);
        return i < 1;
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
            if (goodsStockInfos.size() == 0) {
                return DtoUtil.getSuccessDto("暂无数据", 200000);
            }
            result.put("goodsList", goodsStockInfos);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else if ("price".equals(getGoodsStockList.getGetType())) {
            List<ShowGoodsPriceInfo> goodsPriceList = goodsMapper.getGoodsPriceList(getGoodsStockList);
            if (goodsPriceList.size() == 0) {
                return DtoUtil.getSuccessDto("暂无数据", 200000);
            }
            result.put("goodsList", goodsPriceList);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else if ("consumable".equals(getGoodsStockList.getGetType())) {
            List<ShowConsumableGoods> consumableGoods = goodsMapper.getConsumableGoods(getGoodsStockList);
            if (consumableGoods.size() == 0) {
                return DtoUtil.getSuccessDto("暂无数据", 200000);
            }
            result.put("goodsList", consumableGoods);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        } else if ("son".equals(getGoodsStockList.getGetType())) {
            List<String> ids = goodsMapper.getCorGoodsId();
            StringBuffer stringBuffer = new StringBuffer("0");
            for (String s : ids) {
                stringBuffer.append("," + s);
            }
            List<ShowConsumableGoods> consumableGoods = goodsMapper.getCorGoods(getGoodsStockList, stringBuffer.toString());
            if (consumableGoods.size() == 0) {
                return DtoUtil.getSuccessDto("暂无数据", 200000);
            }
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
        List<Map<String,Object>> goodsTypeList=goodsMapper.getGoodsTypeList(goodsListVo.getStoreId());
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
                goodsListVo.setGoodsType(goodsTypeList.size()>0?goodsTypeList.get(0).get("id").toString():"");
            }
            goodsList=goodsMapper.getGoodsList(goodsListVo.getStoreId(),goodsListVo.getGoodsName(),goodsListVo.getGoodsType(),pageIndex,pageSize);
            for (Map<String,Object> storeGoods:goodsList) {
                Map<String,Object> goodsMap=new HashMap<>(7);
                goodsMap.put("goodsId",storeGoods.get("id"));
                goodsMap.put("goodsPicture",storeGoods.get("goodsPicture"));
                goodsMap.put("goodsName",storeGoods.get("goodsName"));
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
        List<Map<String,Object>> goodsTypeList=goodsMapper.getGoodsTypeList(goodsListVo.getStoreId());
        for (Map<String,Object> storeGoodsType:goodsTypeList) {
            List<Map<String,Object>> mapperGoodsList=goodsMapper.getGoodsList(goodsListVo.getStoreId(),goodsListVo.getGoodsName(),storeGoodsType.get("id").toString(),-1,-1);
            List<Map<String,Object>> list=new ArrayList<>();
            for (Map<String,Object> map:mapperGoodsList) {
                Map<String,Object> goodsMap=new HashMap<>(7);
                goodsMap.put("goodsId",map.get("id"));
                goodsMap.put("goodsPicture",map.get("goodsPicture"));
                goodsMap.put("goodsName",map.get("goodsName"));
                //周销量
                goodsMap.put("weekSalesVolume",0);
                goodsMap.put("goodsPrice",map.get("goodsPrice")==null ? 0 : map.get("goodsPrice"));
                goodsMap.put("goodsUnit",map.get("goodsUnit"));
                //商品库存
                goodsMap.put("goodsStock",map.get("stockNum"));
                //可转换商品的Id
                StoreGoodsCorrelation storeGoodsCorrelation=goodsMapper.getSonGoodsInfo(map.get("id").toString());
                goodsMap.put("changeGoodsId", ObjectUtils.isEmpty(storeGoodsCorrelation) ? "" : storeGoodsCorrelation.getGoodsSonId().toString());
                //转换商品的比例
                goodsMap.put("conversionRatio",map.get("faUnitNum"));
                //转换商品的单位
                goodsMap.put("changeGoodsUnit",map.get("goodsFUnit"));
                list.add(goodsMap);
            }
            storeGoodsType.put("list",list);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",goodsTypeList,100000);
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
        if (!StringUtils.hasText(barcode)) {
            return DtoUtil.getFalseDto("barcode不存在", 90003);
        }
        String url = "https://www.mxnzp.com/api/barcode/goods/details?barcode=" + barcode;
        RestTemplate template = new RestTemplate();
        ResponseEntity responseEntity = template.getForEntity(url, String.class);
        GetBarcode getBarcode = JSONObject.parseObject(responseEntity.getBody().toString(), GetBarcode.class);
        if ("0".equals(getBarcode.getCode())) {
            return DtoUtil.getFalseDto("获取失败", 80004);
        }
        return DtoUtil.getSuccesWithDataDto("获取成功", getBarcode.getData(), 100000);
    }

    /**
     * 添加或新增商品库存
     * @param targetStoreId
     * @param sourceGoods
     * @return
     */
    private void addGoodsStock(String targetStoreId, List<Map<String, String>> sourceGoods) {
        try {
            for (Map<String,String> map:sourceGoods) {
                int a=0;
                //查询商品编码
                Long goodsId;
                Long num;
                //判断是否转换
                if ("0".equals(map.get("changeGoodsNum"))){
                    goodsId= Long.valueOf(map.get("goodsId"));
                    num= Long.valueOf(map.get("num"));
                }else {
                    goodsId= Long.valueOf(map.get("changeGoodsId"));
                    num= Long.valueOf(map.get("changeGoodsNum"));
                }
                StoreGoods storeGoods=goodsMapper.getGoodsInfo(goodsId.toString());
                //如果有条形码
                if (!StringUtils.isEmpty(storeGoods.getGoodsBarCode())){
                    //判断是新增还是修改库存
                    StoreGoodsStock storeGoodsStock=goodsMapper.getGoodsStockByGoodsBarCode(targetStoreId,storeGoods.getGoodsBarCode());
                    if (ObjectUtils.isEmpty(storeGoodsStock)){
                        //新增
                        storeGoodsStock=new StoreGoodsStock();
                        storeGoodsStock.setGoodsId(goodsId);
                        storeGoodsStock.setStoreId(Long.valueOf(targetStoreId));
                        storeGoodsStock.setStockNum(num);
                        storeGoodsStock.setGoodsBarCode(storeGoods.getGoodsBarCode());
                        storeGoodsStock.setGoodsStatus(3L);
                        a=goodsMapper.insertStoreGoodsStock(storeGoodsStock);
                    }else {
                        //修改
                        storeGoodsStock.setStockNum(storeGoodsStock.getStockNum()+num);
                        storeGoodsStock.setGoodsBarCode(storeGoods.getGoodsBarCode());
                        a=goodsMapper.updateGoodsStockByBarCode(storeGoodsStock);
                    }
                }else {//没有条形码
                    StoreGoodsStock storeGoodsStock=goodsMapper.queryGoodsStock(goodsId.toString(),targetStoreId);
                    //判断是新增还是修改库存
                    if (ObjectUtils.isEmpty(storeGoodsStock)){
                        //新增
                        storeGoodsStock=new StoreGoodsStock();
                        storeGoodsStock.setGoodsId(goodsId);
                        storeGoodsStock.setStoreId(Long.valueOf(targetStoreId));
                        storeGoodsStock.setStockNum(num);
                        storeGoodsStock.setGoodsStatus(3L);
                        a=goodsMapper.insertStoreGoodsStock(storeGoodsStock);
                    }else {
                        //修改
                        storeGoodsStock.setStockNum(storeGoodsStock.getStockNum()+num);
                        a=goodsMapper.updGoodsStock(storeGoodsStock);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 减少库存并存储到临时表中
     * @param tradeNo
     * @return false:消耗库存失败
     */
    public String makeGoodsStockReduce(String tradeNo,String goodsList){
        StoreOfflineOrders offlineOrders = goodsMapper.getOfflineOrder(tradeNo);
        if (ObjectUtils.isEmpty(offlineOrders)){
            return "订单未找到";
        }
        String storeId = offlineOrders.getSourceStoreId().toString();
        Map goods=JSONObject.parseObject(goodsList,Map.class);
        List<Map> result = JSONObject.parseArray(JSON.toJSONString(goods.get("goodsBeanArray")),Map.class);
        List<Map<String,Object>> temStocks = new ArrayList<>();
        for (Map map : result){
            String goodsId = map.get("goodsId").toString();
            Long num = Long.valueOf(map.get("num").toString());
            StoreGoodsStock goodsStock = goodsMapper.getGoodsStock(goodsId,storeId);
            List<StoreGoodsConsumable> consumablesList = goodsMapper.getGoodsAllConsumablesList(goodsId);
            Map<String ,Object> temStock = new HashMap<>();
            temStock.put("storeId",storeId);
            temStock.put("goodsBarCode",goodsStock.getGoodsBarCode());
            temStock.put("num",num);
            temStocks.add(temStock);
            if (consumablesList.size() == 0){
                long resNum =  goodsStock.getStockNum()-num;
                if (resNum < 0){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return "商品\"" + map.get("goodsName").toString() + "\"库存不足,请及时联系店员添加商品";
                }
                goodsMapper.updateGoodsStockNum(goodsId,resNum,storeId);
            }else {
                //此处补全减少绑定消耗品的库存
            }
        }
        if (temStocks.size() != 0){
            goodsMapper.addNewTemStock(JSON.toJSONString(temStocks),tradeNo);
        }
        return "success";
    }

    private Map<String, Object> getStoreFirstGoods(Map<String, Object> storeFirstGoods, List<StorePurchaseRecords> newOrderGoodsList,
                                                   String sourceStoreId, String targetStoreId) {
        List<Map> salesVolumes = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (StorePurchaseRecords records : newOrderGoodsList) {
            Date time = goodsMapper.getGoodsFirstPurchaseTime(sourceStoreId, targetStoreId, records.getGoodsId());
            if (ObjectUtils.isEmpty(time)) {
                continue;
            }
            Map salesVolume = goodsMapper.getSalesVolumeByCreateTime(records.getChangeGoodsId().toString(), time);
            if (ObjectUtils.isEmpty(salesVolume)) {
                continue;
            }
            salesVolume.put("records", records);
            salesVolumes.add(salesVolume);
        }
        if (salesVolumes.size() > 0) {
            Map temp;
            for (int i = 0; i < salesVolumes.size() - 1; i++) {
                for (int j = 0; j < salesVolumes.size() - i - 1; j++) {
                    if (Long.valueOf(salesVolumes.get(j + 1).get("num").toString()) < Long.valueOf(salesVolumes.get(j).get("num").toString())) {
                        temp = salesVolumes.get(j);
                        salesVolumes.set(j, salesVolumes.get(j + 1));
                        salesVolumes.set(j + 1, temp);
                    }
                }
            }
            Map sv = new HashMap();
            if (salesVolumes.size() >= 2) {
                if (salesVolumes.get(0).get("num").toString().equals(salesVolumes.get(1).get("num").toString())) {
                    StorePurchaseRecords r1 = (StorePurchaseRecords) salesVolumes.get(0).get("records");
                    StorePurchaseRecords r2 = (StorePurchaseRecords) salesVolumes.get(1).get("records");
                    Long n1 = ObjectUtils.isEmpty(r1.getChangeGoodsId()) ? r1.getGoodsCount() : goodsMapper.getGoodsInfo(r1.getGoodsId().toString()).getFaUnitNum() * r1.getGoodsCount();
                    Long n2 = ObjectUtils.isEmpty(r2.getChangeGoodsId()) ? r2.getGoodsCount() : goodsMapper.getGoodsInfo(r2.getGoodsId().toString()).getFaUnitNum() * r2.getGoodsCount();
                    if (n1 < n2) {
                        sv = salesVolumes.get(1);
                    }
                }
            } else {
                sv = salesVolumes.get(0);
            }
            StorePurchaseRecords records = (StorePurchaseRecords) sv.get("records");
            StoreGoods goods = goodsMapper.getGoodsInfo(records.getChangeGoodsId().toString());
            storeFirstGoods.put("goodsId", goods.getId());
            storeFirstGoods.put("goodsName", goods.getGoodsName());
            storeFirstGoods.put("createTime", simpleDateFormat.format(records.getCreateDate()));
            if (records.getGoodsId().equals(records.getChangeGoodsId())) {
                storeFirstGoods.put("purchaseUnit", goods.getGoodsUnit());
                storeFirstGoods.put("soldUnit", goods.getGoodsUnit());
            } else {
                StoreGoods g = goodsMapper.getGoodsInfo(records.getGoodsId().toString());
                storeFirstGoods.put("purchaseUnit", g.getGoodsFUnit());
                storeFirstGoods.put("soldUnit", g.getGoodsFUnit());
            }
            storeFirstGoods.put("purchaseNum", records.getGoodsCount());
            storeFirstGoods.put("soldNum", sv.get("num"));
            StoreGoodsStock stock = goodsMapper.getGoodsStock(sv.get("goodsId").toString(), targetStoreId);
            storeFirstGoods.put("stock", ObjectUtils.isEmpty(stock) ? 0 : stock.getStockNum());
            storeFirstGoods.put("targetStoreId", records.getTargetStoreId());
            storeFirstGoods.put("orderNumber", records.getOrderNumber());
        } else {
            if (newOrderGoodsList.size() == 0) {
                return storeFirstGoods;
            }
            StorePurchaseRecords records = newOrderGoodsList.get(0);
            StoreGoods goods = goodsMapper.getGoodsInfo(records.getChangeGoodsId().toString());
            storeFirstGoods.put("goodsId", goods.getId());
            storeFirstGoods.put("createTime", simpleDateFormat.format(records.getCreateDate()));
            storeFirstGoods.put("goodsName", goods.getGoodsName());
            if (records.getGoodsId().equals(records.getChangeGoodsId())) {
                storeFirstGoods.put("soldUnit", goods.getGoodsUnit());
                storeFirstGoods.put("purchaseUnit", goods.getGoodsUnit());
            } else {
                StoreGoods g = goodsMapper.getGoodsInfo(records.getGoodsId().toString());
                storeFirstGoods.put("soldUnit", g.getGoodsFUnit());
                storeFirstGoods.put("purchaseUnit", g.getGoodsFUnit());
            }
            storeFirstGoods.put("purchaseNum", records.getGoodsCount());
            storeFirstGoods.put("soldNum", 0);
            StoreGoodsStock stock = goodsMapper.getGoodsStock(records.getChangeGoodsId().toString(), targetStoreId);
            storeFirstGoods.put("stock", ObjectUtils.isEmpty(stock) ? 0 : stock.getStockNum());
            storeFirstGoods.put("orderNumber", records.getOrderNumber());
            storeFirstGoods.put("targetStoreId", records.getTargetStoreId());
        }
        return storeFirstGoods;
    }
}