package com.modcreater.tmstore.controller;


import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.ReceivedStoreId;
import com.modcreater.tmbeans.vo.store.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmstore.config.annotation.Safety;
import com.modcreater.tmstore.service.StoreService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
/**
 * @Author: AJun
 */
@RestController
@RequestMapping("/store/")
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping("queryaccountinfo")
    @ApiOperation("查询认证页面信息")
    public Dto queryAccountInfo(@RequestBody ApproveInfoVo approveInfoVo, HttpServletRequest request){
        return storeService.queryAccountInfo(approveInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("uploadapproveinfo")
    @ApiOperation("上传商铺认证信息")
    public Dto uploadApproveInfo(@RequestBody ApproveInfoVo approveInfoVo, HttpServletRequest request){
        return storeService.uploadApproveInfo(approveInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("querystoreinfo")
    @ApiOperation("查询商铺主页信息")
    public Dto queryStoreInfo(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return storeService.queryStoreInfo(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping("discoverinfo")
    @ApiOperation("发现页面信息")
    public Dto discoverInfo(@RequestBody DiscoverInfoVo discoverInfoVo, HttpServletRequest request){
        return storeService.discoverInfo(discoverInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("hotproducts")
    @ApiOperation("热销产品")
    public Dto hotProducts(@RequestBody SearchDiscoverVo searchDiscoverVo, HttpServletRequest request){
        return storeService.hotProducts(searchDiscoverVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("nearbyshop")
    @ApiOperation("附近好店")
    public Dto nearByShop(@RequestBody SearchDiscoverVo searchDiscoverVo, HttpServletRequest request){
        return storeService.nearByShop(searchDiscoverVo,request.getHeader("token"));
    }


    @PostMapping("getsorttype")
    @ApiOperation("排序方式")
    public Dto getSortType(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return storeService.getSortType(receivedId,request.getHeader("token"));
    }


    @PostMapping("getscreentype")
    @ApiOperation("筛选方式")
    public Dto getScreenType(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return storeService.getScreenType(receivedId,request.getHeader("token"));
    }

    @PostMapping("collectstore")
    @ApiOperation("收藏店铺")
    public Dto collectStore(@RequestBody CollectStoreVo collectStoreVo, HttpServletRequest request){
        return storeService.collectStore(collectStoreVo,request.getHeader("token"));
    }

    @PostMapping("removeCollectStore")
    @ApiOperation("店铺移出收藏")
    public Dto removeCollectStore(@RequestBody CollectStoreVo collectStoreVo, HttpServletRequest request){
        return storeService.removeCollectStore(collectStoreVo,request.getHeader("token"));
    }

    @PostMapping("getCollectStoreList")
    @ApiOperation("查询收藏店铺列表")
    public Dto getCollectStoreList(@RequestBody CollectStoreVo collectStoreVo, HttpServletRequest request){
        return storeService.getCollectStoreList(collectStoreVo,request.getHeader("token"));
    }

    @PostMapping("getstoretypelist")
    @ApiOperation("查询商铺分类")
    public Dto getStoreTypeList(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return storeService.getStoreTypeList(receivedId,request.getHeader("token"));
    }

    @PostMapping("getstorestatuslist")
    @ApiOperation("查询商铺营业状态分类")
    public Dto getStoreStatusList(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return storeService.getStoreStatusList(receivedId,request.getHeader("token"));
    }

    @PostMapping("getstorescreenlist")
    @ApiOperation("查询商铺筛选条件")
    public Dto getStoreScreenList(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return storeService.getStoreScreenList(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "storefullreductionpromotesales")
    @ApiOperation("店铺满减促销")
    public Dto storeFullReductionPromoteSales(@RequestBody StoreFullReductionPromoteSales storeFullReductionPromoteSales, HttpServletRequest request){
        return storeService.storeFullReductionPromoteSales(storeFullReductionPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "storediscountpromotesales")
    @ApiOperation("店铺折扣促销")
    public Dto storeDiscountPromoteSales(@RequestBody StoreDiscountPromoteSales storeDiscountPromoteSales,HttpServletRequest request){
        return storeService.storeDiscountPromoteSales(storeDiscountPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "storepromotesalesverify")
    @ApiOperation("店铺促销验证(是否正在做促销)")
    public Dto storePromoteSalesVerify(@RequestBody ReceivedStoreId receivedStoreId,HttpServletRequest request){
        return storeService.storePromoteSalesVerify(receivedStoreId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "showstorepromotesales")
    @ApiOperation("展示商铺促销")
    public Dto showStorePromoteSales(@RequestBody ReceivedStoreId receivedStoreId ,HttpServletRequest request){
        return storeService.showStorePromoteSales(receivedStoreId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "deletepromotesales")
    @ApiOperation("删除店铺促销活动")
    public Dto deletePromoteSales(@RequestBody DeleteStorePromoteSales deleteStorePromoteSales, HttpServletRequest request){
        return storeService.deletePromoteSales(deleteStorePromoteSales,request.getHeader("token"));
    }
}
