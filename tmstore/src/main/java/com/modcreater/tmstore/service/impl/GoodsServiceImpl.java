package com.modcreater.tmstore.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.goods.*;
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
        if (StringUtils.hasText(registerGoods.getGoodsBarCode()) && goodsMapper.isBarCodeExists(registerGoods.getStoreId(),registerGoods.getGoodsBarCode()) >= 1){
            return DtoUtil.getFalseDto("请勿重复录入相同的条形码",90007);
        }
        if (goodsMapper.getCorRelation(registerGoods.getCorGoodsId()) >= 1){
            return DtoUtil.getFalseDto("当前选中的转换商品已被其他产品绑定",80006);
        }
        goodsMapper.addNewGoods(registerGoods);
        goodsMapper.addNewGoodsStock(registerGoods.getId(),registerGoods.getStoreId(), registerGoods.getGoodsNum(),"1",registerGoods.getGoodsBarCode());
        goodsMapper.bindingGoods(registerGoods.getId(),registerGoods.getCorGoodsId());
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
                consumable.setConsumptionRate(consumable.getRegisteredRatioIn()/consumable.getRegisteredRatioOut());
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
        if (!reg(updateGoods.getUserId(),updateGoods.getStoreId())){
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        try {
            StoreGoods storeGoods = goodsMapper.getGoodsInfo(updateGoods.getGoodsId());
            if (ObjectUtils.isEmpty(storeGoods)){
                return DtoUtil.getFalseDto("商品未找到",90009);
            }
            if (StringUtils.hasText(updateGoods.getCorGoodsId())){
                if (goodsMapper.updateCorRelation(storeGoods.getId().toString(),updateGoods.getCorGoodsId()) != 1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("修改失败",90011);
                }
            }else if (!StringUtils.hasText(updateGoods.getGoodsFUnit())){
                if (goodsMapper.deleteCorRelation(storeGoods.getId().toString()) != 1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("修改失败",90011);
                }
            }
            int updateGoodsResult = goodsMapper.updateGoods(updateGoods);
            int updateGoodsStockResult = goodsMapper.updateGoodsStock(updateGoods.getGoodsId(),updateGoods.getGoodsNum(),updateGoods.getGoodsBarCode());
            if (updateGoodsResult == 0 || updateGoodsStockResult == 0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("修改失败",90011);
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改失败",90011);
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
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
        result.put("goodsFUnit",goods.getGoodsFUnit() == null ? "" : goods.getFaUnitNum());
        result.put("faUnitNum",goods.getFaUnitNum() == null ? "" : goods.getFaUnitNum());
        return DtoUtil.getSuccesWithDataDto("操作成功",result,100000);
    }

    @Override
    public Dto updateGoodsPrice(UpdateGoodsPrice updateGoodsPrice, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateGoodsPrice.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoodsStock storeGoodsStock = goodsMapper.getGoodsStock(updateGoodsPrice.getGoodsId());
        if (!reg(updateGoodsPrice.getUserId(),storeGoodsStock.getStoreId().toString())){
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        /*StoreGoods goods = goodsMapper.getGoodsInfo(updateGoodsPrice.getGoodsId());
        StoreGoodsCorrelation sonRelation = goodsMapper.getSonGoodsInfo(updateGoodsPrice.getGoodsId());
        StoreGoodsCorrelation parRelation = goodsMapper.getParentGoodsInfo(updateGoodsPrice.getGoodsId());
        //如果查到正在改价的商品有父商品,则按父商品绑定时的单位运算自动修改父商品的价格
        if (!ObjectUtils.isEmpty(parRelation)){
            StoreGoods parGoods = goodsMapper.getGoodsInfo(parRelation.getGoodsParentId().toString());
            goodsMapper.updateGoodsUnitPrice(parGoods.getId().toString(),parGoods.getFaUnitNum() * updateGoodsPrice.getUnitPrice());
        }
        //如果查到正在改价的商品有子商品,则按父商品绑定时的单位运算自动修改子商品的价格
        if (!ObjectUtils.isEmpty(sonRelation)){
            StoreGoods sonGoods = goodsMapper.getGoodsInfo(parRelation.getGoodsParentId().toString());
            goodsMapper.updateGoodsUnitPrice(sonGoods.getId().toString(),updateGoodsPrice.getUnitPrice() / goods.getFaUnitNum());
        }*/
        if (goodsMapper.updateGoodsUnitPrice(updateGoodsPrice.getGoodsId(), updateGoodsPrice.getUnitPrice()) != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改价格失败", 80005);
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
        //判断商品库存
        for (Map<String,String> map:claimGoodsVo.getSourceGoods()) {
            long goodsStock=goodsMapper.queryGoodsStock(map.get("goodsId"),claimGoodsVo.getSourceStoreId());
            if (goodsStock < Long.parseLong(map.get("goodsNum"))){
                StoreGoods storeGoods=goodsMapper.getGoodsInfo(map.get("goodsId"));
                return DtoUtil.getFalseDto("商品"+storeGoods.getGoodsName()+"库存不足,交易未完成",95001);
            }
        }
        //减去出货商家的库存
        int a=goodsMapper.deductionStock(claimGoodsVo.getSourceStoreId(),claimGoodsVo.getSourceGoods());
        //收货商户添加一批货物
        int flag=addGoodsStock(claimGoodsVo.getTargetStoreId(),claimGoodsVo.getSourceGoods());
        //保存交易记录
        String orderNumber=(System.currentTimeMillis()/1000+claimGoodsVo.getSourceStoreId()+claimGoodsVo.getTargetStoreId());
        int b=storeMapper.saveTradingRecord(claimGoodsVo.getSourceGoods(),claimGoodsVo.getSourceStoreId(),claimGoodsVo.getTargetStoreId(),claimGoodsVo.getTransactionPrice(),orderNumber,1);
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
        int i = 0;
        for (String goodsId : goodsDownShelf.getGoodsId()){
            i += goodsMapper.updateGoodsStatus(goodsId,0);
        }
        if (i == goodsDownShelf.getGoodsId().length){
            return DtoUtil.getSuccessDto("操作成功",100000);
        }else {
            return DtoUtil.getFalseDto("操作失败",90008);
        }
    }

    @Override
    public Dto getUpdateGoodsInfo(ReceivedGoodsId receivedGoodsId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGoodsId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        StoreGoods storeGoods = goodsMapper.getGoodsInfo(receivedGoodsId.getGoodsId());
        if (!reg(receivedGoodsId.getUserId(), storeGoods.getStoreId().toString())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        GoodsInfoToUpdate goodsInfoToUpdate = goodsMapper.getGoodsInfoToUpdate(receivedGoodsId.getGoodsId());
        List<ShowConsumable> showConsumables = goodsMapper.getGoodsConsumablesList(receivedGoodsId.getGoodsId());
        goodsInfoToUpdate.setShowConsumables(showConsumables);
        goodsInfoToUpdate.setCorGoodsId(goodsMapper.getSonGoodsInfo(receivedGoodsId.getGoodsId()).getGoodsSonId().toString());
        return DtoUtil.getSuccesWithDataDto("查询成功",goodsInfoToUpdate,100000);
    }

    @Override
    public Dto getGoodsConsumable(ReceivedGoodsId receivedGoodsId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGoodsId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return DtoUtil.getSuccesWithDataDto("获取成功",goodsMapper.getGoodsConsumablesList(receivedGoodsId.getGoodsId()),100000);
    }

    @Override
    public Dto deleteGoodsConsumables(DeleteGoodsConsumables deleteGoodsConsumables, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteGoodsConsumables.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int i = 0;
        for (String consumableId : deleteGoodsConsumables.getConsumableIds()){
            i += goodsMapper.deleteGoodsConsumable(consumableId);
        }
        if (i == deleteGoodsConsumables.getConsumableIds().length){
            return DtoUtil.getSuccessDto("删除成功",100000);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getFalseDto("删除失败",90008);
    }

    @Override
    public Dto getUpdateConsumableInfo(ReceivedConsumableId receivedConsumableId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedConsumableId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowUpdateConsumableInfo consumable = goodsMapper.getUpdateConsumableInfo(receivedConsumableId.getConsumableId());
        if (ObjectUtils.isEmpty(consumable)){
            return DtoUtil.getSuccessDto("暂无数据",200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",consumable,100000);
    }

    @Override
    public Dto updateConsumable(UpdateConsumable updateConsumable, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateConsumable.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        updateConsumable.setConsumptionRate((double)updateConsumable.getRegisteredRatioIn()/updateConsumable.getRegisteredRatioOut());
        if (goodsMapper.updateConsumable(updateConsumable) == 1){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",90008);
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
            List<String> ids = goodsMapper.getCorGoodsId();
            StringBuffer stringBuffer = new StringBuffer("0");
            for (String s : ids){
                stringBuffer.append(","+s);
            }
            List<ShowConsumableGoods> consumableGoods = goodsMapper.getCorGoods(getGoodsStockList,stringBuffer.toString());
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

    /**
     * 添加或新增商品库存
     * @param targetStoreId
     * @param sourceGoods
     * @return
     */
    private int addGoodsStock(String targetStoreId, List<Map<String, String>> sourceGoods) {
        //查询商品编码
        return 0;
    }
}