package com.modcreater.tmstore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.vo.goods.ReceivedStoreId;
import com.modcreater.tmbeans.vo.store.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.GoodsMapper;
import com.modcreater.tmdao.mapper.StoreMapper;
import com.modcreater.tmdao.mapper.SuperAdminMapper;
import com.modcreater.tmstore.service.StoreService;
import com.modcreater.tmutils.*;
import org.apache.commons.codec.StringEncoderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: AJun
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreServiceImpl implements StoreService {

    private static final String SYSTEMID = "100000";
    private static final String ANDROID = "android";
    private static final String IOS = "ios";
    private static final Integer LIMIT_SCOPE = 300000;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private SuperAdminMapper superAdminMapper;

    RongCloudMethodUtil rongCloudMethodUtil =new RongCloudMethodUtil();

    private Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    /**
     * 查询认证页面信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto queryAccountInfo(ApproveInfoVo approveInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(approveInfoVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Account account=accountMapper.queryAccount(approveInfoVo.getUserId());
        Map<String,String> map=new HashMap<>(3);

        if (!ObjectUtils.isEmpty(account)){
            map.put("userId",account.getId().toString());
            map.put("userName",account.getUserName());
            map.put("userCode",account.getUserCode());
        }else {
            map.put("userId","");
            map.put("userName","");
            map.put("userCode","");
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    /**
     * 上传商铺认证信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto uploadApproveInfo(ApproveInfoVo approveInfoVo,String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(approveInfoVo.getUserId()))){
            return DtoUtil.getFalseDto("请先登录",21014);
        }
        if (!ObjectUtils.isEmpty(storeMapper.getDisposeStatus(approveInfoVo.getUserId()))){
            return DtoUtil.getFalseDto("您的商铺认证已经提交过了，请耐心等待",27456);
        }
        StoreAttestation storeAttestation=new StoreAttestation();
        storeAttestation.setBusinessLicense(approveInfoVo.getBusinessLicense());
        storeAttestation.setExequatur(JSON.toJSONString(approveInfoVo.getExequatur()));
        storeAttestation.setStoreLogo(approveInfoVo.getStoreLogo());
        storeAttestation.setUserId(Long.valueOf(approveInfoVo.getUserId()));
        storeAttestation.setAddress(approveInfoVo.getAddress());
        storeAttestation.setLongitude(Double.valueOf(approveInfoVo.getLongitude()));
        storeAttestation.setLatitude(Double.valueOf(approveInfoVo.getLatitude()));
        storeAttestation.setBusinessScope(Long.valueOf(approveInfoVo.getBusinessScope()));
        storeAttestation.setDetailAddress(approveInfoVo.getDetailAddress());
        storeAttestation.setStoreName(approveInfoVo.getStoreName());
        storeAttestation.setStorefrontPicture(approveInfoVo.getStorefrontPicture());
        storeAttestation.setOpenStoreHours(approveInfoVo.getOpenStoreHours());
        storeAttestation.setCloseStoreHours(approveInfoVo.getCloseStoreHours());
        storeAttestation.setPhoneNumber(approveInfoVo.getPhoneNumber());
        int i=storeMapper.insertStoreAttestation(storeAttestation);
        if (i==0){
            return DtoUtil.getFalseDto("上传商铺认证信息失败",21022);
        }
        //通知管理员
        List<SuperAdministrator> superAdministrators=superAdminMapper.querySuperAdmins();
        List<String> emails=new ArrayList<>();
        String title="【智袖】";
        String content="有新的【商铺认证】等待您的确认！";
        for (SuperAdministrator administrators : superAdministrators) {
            if (!StringUtils.isEmpty(administrators.getEmail())){
                emails.add(administrators.getEmail());
            }
        }
        System.out.println("接收的邮箱"+emails);
        SendMsgUtil.asynSendEmail(title,content,emails);
        return DtoUtil.getSuccessDto("上传商铺认证信息成功，请耐心等待",100000);
    }

    /**
     * 查询商铺信息
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto queryStoreInfo(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Object> resultMap=new HashMap<>(3);
        //查询用户信息
        Account account=accountMapper.queryAccount(receivedId.getUserId());
        Map<String,Object> accountMap=new HashMap<>(2);
        accountMap.put("userName",account.getUserName());
        //查询认证状态  0:未认证；1：认证中；2：已认证；3：未通过
        StoreAttestation storeAttestation=storeMapper.getDisposeStatus(receivedId.getUserId());
        if (ObjectUtils.isEmpty(storeAttestation)){
            resultMap.put("disposeStatus",0);
        }else if (storeAttestation.getDisposeStatus()==0L){
            resultMap.put("disposeStatus",1);
        }else if (storeAttestation.getDisposeStatus()==1L){
            resultMap.put("disposeStatus",2);
        }else if (storeAttestation.getDisposeStatus()==2L){
            resultMap.put("disposeStatus",3);
        }
        //查询商铺信息
        StoreInfo storeInfo=new StoreInfo();
        if (!ObjectUtils.isEmpty(storeAttestation)){
            storeInfo=storeMapper.getStoreInfoByAttestationId(storeAttestation.getId());
        }
        if (ObjectUtils.isEmpty(storeInfo)){
            storeInfo=new StoreInfo();
        }
        Map<String,Object> storeMap=new HashMap<>(3);
        storeMap.put("storeId",storeInfo.getId());
        storeMap.put("storeName",storeInfo.getStoreName());
        storeMap.put("storePicture",storeInfo.getStorePicture());
        storeMap.put("storeAddress",storeInfo.getStoreAddress());
        //查询余额
        accountMap.put("balance", storeInfo.getWallet());
        resultMap.put("account",accountMap);
        resultMap.put("storeInfo",storeMap);
        return DtoUtil.getSuccesWithDataDto("查询成功",resultMap,100000);
    }

    /**
     * 进入发现主页
     * @param discoverInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto discoverInfo(DiscoverInfoVo discoverInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(discoverInfoVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        /**
         * 查询目标30km范围内所有店铺
         * 店名、logo、联系方式、地址、距离
         */
        List<StoreInfo> storeList=storeMapper.getStoreListByCondition(discoverInfoVo.getStoreTypeId(),discoverInfoVo.getStoreStatusId());
        List<Map<String,String>> mapList=new ArrayList<>();
        for (StoreInfo storeInfo:storeList) {
            String distance=DistanceUtil.getDistanceByamap(storeInfo.getLongitude(),storeInfo.getLatitude(),discoverInfoVo.getLongitude(),discoverInfoVo.getLatitude(),"3");
            if (Integer.parseInt(distance)==-1){
                return DtoUtil.getFalseDto("查询失败",28541);
            }else if (Integer.parseInt(distance)<=LIMIT_SCOPE){
                Map<String,String> map=new HashMap<>(10);
                map.put("storeId",storeInfo.getId().toString());
                map.put("storeName",storeInfo.getStoreName());
                map.put("storePicture",storeInfo.getStorePicture());
                map.put("contactWay",storeInfo.getPhoneNumber());
                //查询商铺周销量
                map.put("weekSalesVolume",storeMapper.getStoreWeekSalesVolume(storeInfo.getId().toString(),new SimpleDateFormat("yyyyMMdd").format(new Date()),DateUtil.getDay(-7)).toString());
                map.put("storeAddress",storeInfo.getStoreAddress());
                map.put("longitude",storeInfo.getLongitude().toString());
                map.put("latitude",storeInfo.getLatitude().toString());
                map.put("distance",distance);
                //查询该店铺收藏状态
                map.put("collectStatus", String.valueOf(storeMapper.getStoreCollectStatus(discoverInfoVo.getUserId(),storeInfo.getId())));
                //查询该店铺收藏数量
                map.put("collectNum", String.valueOf(storeMapper.getStoreCollectNum(storeInfo.getId())));
                map.put("status",storeInfo.getStatus().toString());
                map.put("detailAddress",storeInfo.getDetailAddress());
                map.put("businessHouse",storeInfo.getOpenStoreHours()+"-"+storeInfo.getCloseStoreHours());
                map.put("businessScope",storeInfo.getBusinessScope().toString());
                mapList.add(map);
            }
        }
        //排序方式:
        //1、销量最多
        //2、收藏最多
        if (!StringUtils.isEmpty(discoverInfoVo.getScreenTypeId()) && mapList.size()>0){
            Integer type=Integer.parseInt(discoverInfoVo.getScreenTypeId());
            if (type == 1){
                Collections.sort(mapList, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> o1, Map<String, String> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("weekSalesVolume")) ;
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("weekSalesVolume")) ;
                        return distance1.compareTo(distance2);
                    }
                });
            }else if(type == 2) {
                Collections.sort(mapList, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> o1, Map<String, String> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("collectNum"));
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("collectNum"));
                        return distance1.compareTo(distance2);
                    }
                });
            }
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 热销产品
     * @param searchDiscoverVo
     * @param token
     * @return
     */
    @Override
    public Dto hotProducts(SearchDiscoverVo searchDiscoverVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchDiscoverVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        //查询商品列表
        Map<String,Object> resultMap=new HashMap<>(2);
        List<StoreGoods> goodsList=storeMapper.getGoodsAllType(searchDiscoverVo.getGoodsKeyWords(),searchDiscoverVo.getScreenType());
        List<Map<String,Object>> goodsMapList=new ArrayList<>();
        for (StoreGoods goodsInfo:goodsList) {
            Map<String,Object> goodsMap=new HashMap<>();
            StoreGoodsStock goodsStock=goodsMapper.getGoodsStock(goodsInfo.getId().toString(),goodsInfo.getStoreId().toString());
            goodsMap.put("goodsId",goodsInfo.getId().toString());
            goodsMap.put("goodsName",goodsInfo.getGoodsName());
            goodsMap.put("goodsPicture",goodsInfo.getGoodsPicture());
            goodsMap.put("goodsPrice",goodsStock.getGoodsPrice().toString());
            goodsMap.put("goodsUnit",goodsInfo.getGoodsUnit());
            //推荐指数
            goodsMap.put("goodsScore","4.7");
            Long sumWeekSalesVolume=0L;
            Long collectNum=0L;
            Long sumGoodsStock=0L;
            //查询包含此商品指定范围内的店铺
            List<Map<String,String>> storeList=storeMapper.getStoreListByGoods(goodsInfo.getId());
            List<Map<String,Object>> storeMapList=new ArrayList<>();
            for (Map map:storeList) {
                Map<String,Object> storeMap=new HashMap<>();
                storeMap.put("storeId",map.get("id"));
                storeMap.put("storeName",map.get("storeName"));
                storeMap.put("storePicture",map.get("storePicture"));
                storeMap.put("address",map.get("storeAddress"));
                String distance=DistanceUtil.getDistanceByamap(searchDiscoverVo.getLongitude(),searchDiscoverVo.getLatitude(),Double.parseDouble(map.get("longitude").toString()),Double.parseDouble(map.get("latitude").toString()),"3");
                storeMap.put("longitude",map.get("longitude"));
                storeMap.put("latitude",map.get("latitude"));
                //查询该店铺此商品的周销量
                Long weekSalesVolume=goodsMapper.getGoodsSalesVolume(map.get("id").toString(),goodsInfo.getId().toString(),new SimpleDateFormat("yyyyMMdd").format(new Date()),DateUtil.getDay(-7));
                sumWeekSalesVolume=sumWeekSalesVolume+weekSalesVolume;
                storeMap.put("weekSalesVolume",weekSalesVolume.toString());
                //查询该店铺收藏数量
                collectNum=collectNum+storeMapper.getStoreCollectNum(Long.parseLong(map.get("id").toString()));
                goodsMap.put("collectNum", String.valueOf(storeMapper.getStoreCollectNum(Long.parseLong(map.get("id").toString()))));
                //查询该商铺该商品的库存
                Long goodsStockNum=goodsMapper.getGoodsStockNum(goodsInfo.getId().toString(),map.get("id").toString());
                sumGoodsStock=sumGoodsStock+goodsStockNum;
                storeMap.put("goodsStock",goodsStockNum);
                storeMap.put("distance",distance);
                storeMap.put("status",map.get("status"));
                storeMap.put("businessScope",map.get("businessScope"));
                storeMap.put("businessHouse",map.get("openStoreHours")+"-"+map.get("closeStoreHours"));
                storeMap.put("detailAddress",map.get("detailAddress"));
                //营业时间
                storeMapList.add(storeMap);
            }
            //查询总周销量
            goodsMap.put("weekSalesVolume",sumWeekSalesVolume.toString());
            goodsMap.put("collectNum",collectNum);
            goodsMap.put("goodsStock",sumGoodsStock);
            goodsMap.put("goodsAllergen",goodsInfo.getGoodsAllergen());
            goodsMap.put("storeList",storeMapList);
            Collections.sort(storeMapList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    //name1是从你list里面拿出来的一个
                    Integer distance1 = Integer.valueOf(o1.get("distance").toString()) ;
                    //name1是从你list里面拿出来的第二个name
                    Integer distance2 = Integer.valueOf(o2.get("distance").toString()) ;
                    return distance1.compareTo(distance2);
                }
            });
            goodsMapList.add(goodsMap);
        }
        //排序方式:
        //1、库存数量
        //2、距离最近
        //3、综合排序
        //4、销量最多
        //5、收藏最多
        if (!StringUtils.isEmpty(searchDiscoverVo.getSortType())){
            Integer type=Integer.parseInt(searchDiscoverVo.getSortType());
            if (type == 1){
                Collections.sort(goodsMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        List<Map<String,Object>> list1=JSONObject.parseObject(o1.get("storeList").toString(),ArrayList.class);
                        Integer distance1 = Integer.valueOf(list1.get(0).get("distance").toString()) ;
                        //name1是从你list里面拿出来的第二个name
                        List<Map<String,Object>> list2=JSONObject.parseObject(o2.get("storeList").toString(),ArrayList.class);
                        Integer distance2 = Integer.valueOf(list2.get(0).get("distance").toString()) ;
                        return distance1.compareTo(distance2);
                    }
                });
            }else if(type == 2){
                Collections.sort(goodsMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("distance").toString()) ;
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("distance").toString()) ;
                        return distance1.compareTo(distance2);
                    }
                });
            }else if(type == 3){
                Collections.sort(goodsMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Double distance1 = Double.valueOf(o1.get("goodsPrice").toString()) ;
                        //name1是从你list里面拿出来的第二个name
                        Double distance2 = Double.valueOf(o2.get("goodsPrice").toString()) ;
                        return distance1.compareTo(distance2);
                    }
                });
            }else if(type == 4){
                Collections.sort(goodsMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("weekSalesVolume").toString()) ;
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("weekSalesVolume").toString()) ;
                        return distance1.compareTo(distance2);
                    }
                });
            }else if(type == 5){
                Collections.sort(goodsMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("collectNum").toString()) ;
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("collectNum").toString()) ;
                        return distance1.compareTo(distance2);
                    }
                });
            }
        }
        resultMap.put("hotProducts",goodsMapList);
        return DtoUtil.getSuccesWithDataDto("查询成功",resultMap,100000);
    }

    /**
     * 附近好店
     * @param searchDiscoverVo
     * @param token
     * @return
     */
    @Override
    public Dto nearByShop(SearchDiscoverVo searchDiscoverVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchDiscoverVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        //查询店铺列表
        Map<String,Object> resultMap=new HashMap<>(2);
        List<Map<String,Object>> storeMapList=new ArrayList<>();
        List<Map<String,String>> storeList=storeMapper.getStoreListBySearch(searchDiscoverVo.getGoodsKeyWords(),searchDiscoverVo.getScreenType());
        //查询包含此商品指定范围内的店铺
        for (Map map:storeList) {
            Map<String,Object> storeMap=new HashMap<>();
            storeMap.put("storeId",map.get("id"));
            storeMap.put("storeName",map.get("storeName"));
            storeMap.put("storePicture",map.get("storePicture"));
            storeMap.put("address",map.get("storeAddress"));
            String distance=DistanceUtil.getDistanceByamap(searchDiscoverVo.getLongitude(),searchDiscoverVo.getLatitude(),Double.parseDouble(map.get("longitude").toString()),Double.parseDouble(map.get("latitude").toString()),"3");
            storeMap.put("distance",distance);
            storeMap.put("longitude",map.get("longitude"));
            storeMap.put("latitude",map.get("latitude"));
            storeMap.put("storeScore","4.8");
            storeMap.put("collectNum", String.valueOf(storeMapper.getStoreCollectNum(Long.parseLong(map.get("id").toString()))));
            storeMap.put("status",map.get("status"));
            Long weekSalesVolume=0L;
            Long sumGoodsStock=0L;
            String goodsUnit="";
            //查询商品列表
            List<Map<String,String>> goodsMapList=new ArrayList<>();
            List<StoreGoods> goodsList=storeMapper.getGoodsListBySearch(searchDiscoverVo.getGoodsKeyWords(),searchDiscoverVo.getScreenType(),map.get("id").toString());
            for (StoreGoods storeGoods:goodsList) {
                Map<String,String> goodsMap=new HashMap<>();
                goodsMap.put("goodsId",storeGoods.getId().toString());
                goodsMap.put("goodsName",storeGoods.getGoodsName());
                goodsMap.put("goodsPicture",storeGoods.getGoodsPicture());
                StoreGoodsStock goodsStock=goodsMapper.getGoodsStock(storeGoods.getId().toString(),map.get("id").toString());
                goodsMap.put("goodsPrice",goodsStock.getGoodsPrice().toString());
                goodsMap.put("goodsUnit",storeGoods.getGoodsUnit());
                goodsUnit=goodsUnit+storeGoods.getGoodsUnit();
                //查询销量
                weekSalesVolume=goodsMapper.getGoodsSalesVolume(map.get("id").toString(),storeGoods.getId().toString(),new SimpleDateFormat("yyyyMMdd").format(new Date()),DateUtil.getDay(-7));
                //查询该商铺该商品的库存
                Long goodsStockNum=goodsMapper.getGoodsStockNum(storeGoods.getId().toString(),map.get("id").toString());
                sumGoodsStock=sumGoodsStock+goodsStockNum;
                goodsMapList.add(goodsMap);
            }
            storeMap.put("weekSalesVolume",weekSalesVolume);
            storeMap.put("goodsUnit",goodsUnit);
            storeMap.put("goodsStock",sumGoodsStock);
            storeMap.put("goodsList",goodsMapList);
            storeMapList.add(storeMap);
        }

        //排序方式:
        //1、库存数量
        //2、距离最近
        //3、综合排序
        //4、销量最多
        //5、收藏最多
        if (!StringUtils.isEmpty(searchDiscoverVo.getSortType())) {
            Integer type = Integer.parseInt(searchDiscoverVo.getSortType());
            if (type == 1) {
                Collections.sort(storeMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("goodsStock").toString());
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("goodsStock").toString());
                        return distance1.compareTo(distance2);
                    }
                });
            } else if (type == 2) {
                Collections.sort(storeMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("distance").toString());
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("distance").toString());
                        return distance1.compareTo(distance2);
                    }
                });
            } else if (type == 3) {
                Collections.sort(storeMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Double distance1 = Double.valueOf(o1.get("storeScore").toString());
                        //name1是从你list里面拿出来的第二个name
                        Double distance2 = Double.valueOf(o2.get("storeScore").toString());
                        return distance1.compareTo(distance2);
                    }
                });
            } else if (type == 4) {
                Collections.sort(storeMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("weekSalesVolume").toString());
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("weekSalesVolume").toString());
                        return distance1.compareTo(distance2);
                    }
                });
            }else if (type == 5) {
                Collections.sort(storeMapList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        //name1是从你list里面拿出来的一个
                        Integer distance1 = Integer.valueOf(o1.get("collectNum").toString());
                        //name1是从你list里面拿出来的第二个name
                        Integer distance2 = Integer.valueOf(o2.get("collectNum").toString());
                        return distance1.compareTo(distance2);
                    }
                });
            }
        }
        resultMap.put("nearByShop",storeMapList);
        return DtoUtil.getSuccesWithDataDto("查询成功",resultMap,100000);
    }

    /**
     * 排序方式
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getSortType(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<Map<String,String>> mapList=new ArrayList<>();
        Map<String,String> map3=new HashMap<>();
        map3.put("sortTypeId","3");
        map3.put("sortType","综合排序");
        Map<String,String> map4=new HashMap<>();
        map4.put("sortTypeId","4");
        map4.put("sortType","销量最多");
        Map<String,String> map5=new HashMap<>();
        map5.put("sortTypeId","5");
        map5.put("sortType","收藏最多");
        mapList.add(map3);
        mapList.add(map4);
        mapList.add(map5);
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 筛选方式
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getScreenType(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<Map<String,String>> mapList=goodsMapper.getGoodsAllTypeList();
        mapList.remove(0);
        mapList.remove(1);
        mapList.remove(2);
        List<Map> resultMapList=new ArrayList<>();
        for (Map map:mapList) {
            Map<String,String> map1=new HashMap<>();
            map1.put("screenTypeId",map.get("id").toString());
            map1.put("screenType",map.get("type").toString());
            map1.put("select","false");
            resultMapList.add(map1);
        }

        return DtoUtil.getSuccesWithDataDto("查询成功",resultMapList,100000);
    }

    /**
     * 收藏店铺
     * @param collectStoreVo
     * @param token
     * @return
     */
    @Override
    public Dto collectStore(CollectStoreVo collectStoreVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(collectStoreVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        if (storeMapper.getStoreCollectStatus(collectStoreVo.getUserId(), Long.valueOf(collectStoreVo.getStoreId()))>0){
            return DtoUtil.getFalseDto("不能重复收藏",62014);
        }
        int i=storeMapper.collectStore(collectStoreVo.getUserId(),collectStoreVo.getStoreId());
        if (i>0){
            return DtoUtil.getSuccessDto("收藏成功",100000);
        }
        return DtoUtil.getSuccessDto("操作失败",61041);
    }

    /**
     * 移出收藏
     * @param collectStoreVo
     * @param token
     * @return
     */
    @Override
    public Dto removeCollectStore(CollectStoreVo collectStoreVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(collectStoreVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        int i=storeMapper.deleteCollectStore(collectStoreVo.getUserId(),collectStoreVo.getStoreId());
        if (i>0){
            return DtoUtil.getSuccessDto("移除收藏夹成功",100000);
        }
        return DtoUtil.getSuccessDto("操作失败",61041);
    }

    /**
     * 查询收藏店铺列表
     * @param collectStoreVo
     * @param token
     * @return
     */
    @Override
    public Dto getCollectStoreList(CollectStoreVo collectStoreVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(collectStoreVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<StoreInfo> storeList=storeMapper.getCollectStoreList(collectStoreVo.getUserId());
        List<Map<String,String>> mapList=new ArrayList<>();
        for (StoreInfo storeInfo:storeList) {
            String distance=DistanceUtil.getDistanceByamap(storeInfo.getLongitude(),storeInfo.getLatitude(),collectStoreVo.getLongitude(),collectStoreVo.getLatitude(),"3");
            if (Integer.parseInt(distance)==-1){
                return DtoUtil.getFalseDto("查询失败",28541);
            }else if (Integer.parseInt(distance)<=LIMIT_SCOPE){
                Map<String,String> map=new HashMap<>(10);
                map.put("storeId",storeInfo.getId().toString());
                map.put("storeName",storeInfo.getStoreName());
                map.put("goodsPicture",storeInfo.getStorePicture());
                map.put("contactWay","123456789");
                map.put("weekSalesVolume","7520");
                map.put("storeAddress",storeInfo.getStoreAddress());
                map.put("longitude",storeInfo.getLongitude().toString());
                map.put("latitude",storeInfo.getLatitude().toString());
                map.put("distance",distance);
                map.put("status",storeInfo.getStatus().toString());
                mapList.add(map);
            }
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询商铺分类
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getStoreTypeList(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<Map<String,String>> mapList=new ArrayList<>();
        Map<String,String> map1=new HashMap<>();
        map1.put("storeTypeId","1");
        map1.put("storeType","生活超市");
        Map<String,String> map2=new HashMap<>();
        map2.put("storeTypeId","2");
        map2.put("storeType","甜点饮品");
        Map<String,String> map3=new HashMap<>();
        map3.put("storeTypeId","3");
        map3.put("storeType","便利商店");
        Map<String,String> map4=new HashMap<>();
        map4.put("storeTypeId","4");
        map4.put("storeType","美食");
        mapList.add(map1);
        mapList.add(map2);
        mapList.add(map3);
        mapList.add(map4);
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询商铺营业状态分类
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getStoreStatusList(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<Map<String,String>> mapList=new ArrayList<>();
        Map<String,String> map1=new HashMap<>();
        map1.put("storeStatusId","1");
        map1.put("storeStatus","正在营业");
        Map<String,String> map2=new HashMap<>();
        map2.put("storeStatusId","2");
        map2.put("storeStatus","商家打样");
        Map<String,String> map3=new HashMap<>();
        map3.put("storeStatusId","3");
        map3.put("storeStatus","敬请期待");
        mapList.add(map1);
        mapList.add(map2);
        mapList.add(map3);
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询商铺筛选条件
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getStoreScreenList(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<Map<String,String>> mapList=new ArrayList<>();
        Map<String,String> map1=new HashMap<>();
        map1.put("screenTypeId","1");
        map1.put("screenType","销量最多");
        Map<String,String> map2=new HashMap<>();
        map2.put("screenTypeId","2");
        map2.put("screenType","收藏最多");
        mapList.add(map1);
        mapList.add(map2);
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    @Override
    public Dto storeFullReductionPromoteSales(StoreFullReductionPromoteSales storeFullReductionPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(storeFullReductionPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(storeFullReductionPromoteSales.getUserId(), storeFullReductionPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        Double[] fullValues = storeFullReductionPromoteSales.getFullValue();
        Double[] disValues = storeFullReductionPromoteSales.getDisValue();
        if (fullValues.length != disValues.length){
            //plau:params length are unequal
            return DtoUtil.getFalseDto("操作失败plau",90024);
        }
        for (int i = 0; i < fullValues.length; i++) {
            if (disValues[i] > fullValues[i]){
                return DtoUtil.getFalseDto("折扣金额不能大于消费金额",90022);
            }
            int a = storeMapper.addNewStoreFullReduction(storeFullReductionPromoteSales.getStoreId(),storeFullReductionPromoteSales.getFullValue()[i],
                    storeFullReductionPromoteSales.getDisValue()[i],storeFullReductionPromoteSales.getStartTime(),
                    storeFullReductionPromoteSales.getEndTime(),storeFullReductionPromoteSales.getShare());
            if (a != 1){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //ansfrf:adding new store full reduction failed
                return DtoUtil.getFalseDto("操作失败ansfrf",90025);
            }
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public Dto storeDiscountPromoteSales(StoreDiscountPromoteSales storeDiscountPromoteSales, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(storeDiscountPromoteSales.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(storeDiscountPromoteSales.getUserId(), storeDiscountPromoteSales.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        int i = storeMapper.addNewStoreDiscountPromoteSales(storeDiscountPromoteSales.getStoreId(),storeDiscountPromoteSales.getValue(),
                storeDiscountPromoteSales.getStartTime(),storeDiscountPromoteSales.getEndTime());
        if (i != 1){
            return DtoUtil.getFalseDto("操作失败",90026);
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public Dto storePromoteSalesVerify(ReceivedStoreId receivedStoreId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedStoreId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!reg(receivedStoreId.getUserId(), receivedStoreId.getStoreId())) {
            return DtoUtil.getFalseDto("违规操作!", 90001);
        }
        if (storeMapper.verifyStoreExistInSFR(receivedStoreId.getStoreId(),System.currentTimeMillis()/1000) >= 1){
            return DtoUtil.getFalseDto("商店正在进行促销,请勿重复操作",90021);
        }
        return DtoUtil.getSuccessDto("",100000);
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
}
