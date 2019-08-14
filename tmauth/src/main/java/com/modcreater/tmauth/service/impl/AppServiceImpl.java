package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AppService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.ActivityTable;
import com.modcreater.tmbeans.pojo.DiscountCoupon;
import com.modcreater.tmbeans.pojo.DiscountUser;
import com.modcreater.tmbeans.vo.app.DiscountUserVo;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdExtra;
import com.modcreater.tmdao.mapper.AppMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 9:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto updateApp(ReceivedAppInfo appInfo, HttpServletRequest request) {
        List<String> appVersions = appMapper.getAppVersion();
        Map<String, String> result = new HashMap<>();
        result.put("type", "0");
        result.put("apkUrl", null);
        int i = 0;
        String s3 = "";
        if (appVersions.size() > 0){
            for (String s : appVersions){
                String resss = s.replace(".","");
                if (Integer.valueOf(resss) > i){
                    i = Integer.valueOf(resss);
                    s3 = s;
                }
            }
        }
        if (i > 0){
            if (Integer.valueOf(appInfo.getAppVersion().replace(".","")) < i){
                String url = appMapper.getAppUrl(s3);
                result.put("type", "1");
                result.put("apkUrl", url);
                return DtoUtil.getSuccesWithDataDto("需要更新", result, 100000);
            }
            return DtoUtil.getSuccesWithDataDto("已是最新版本", result, 100000);
        }else {
            return DtoUtil.getSuccesWithDataDto("已是最新版本", result, 100000);
        }
    }

    /**
     * 获取活动公告
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getActivityInform(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<ActivityTable> tableList=appMapper.queryActivityTable();
        List<Map<String,String>> result=new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        for (ActivityTable activityTable:tableList) {
            Map<String,String> map=new HashMap<>(7);
            map.put("activityId",activityTable.getId().toString());
            map.put("discountId",activityTable.getDiscountId().toString());
            map.put("activityName",activityTable.getActivityName());
            map.put("starTime",sdf.format(DateUtil.stampToDate(activityTable.getStarTime().toString())));
            map.put("endTime",sdf.format(DateUtil.stampToDate(activityTable.getEndTime().toString())));
            map.put("createDate",sdf.format(activityTable.getCreateDate()));
            map.put("isOverdue",activityTable.getIsOverdue().toString());
            map.put("activityC1",activityTable.getActivityC1());
            map.put("activityC2",activityTable.getActivityC2());
            map.put("activityImg",activityTable.getActivityImg());
            map.put("afterMoney",activityTable.getAfterMoney());
            //查询未领取的用户
            if (ObjectUtils.isEmpty(appMapper.queryDiscountUser(receivedId.getUserId(),activityTable.getDiscountId().toString()))){
                result.add(map);
                /*//领取优惠券
                DiscountUser discountUser=new DiscountUser();
                discountUser.setUserId(Long.valueOf(receivedId.getUserId()));
                discountUser.setDiscountId(Long.valueOf(activityTable.getDiscountId()));
                discountUser.setStarTime(activityTable.getStarTime());
                discountUser.setEndTime(activityTable.getEndTime());
                int i=appMapper.getDiscountCoupon(discountUser);*/
            }
        }
        if (result.size()>0){
            return DtoUtil.getSuccesWithDataDto("获取公告成功",result,100000);
        }else {
            return DtoUtil.getFalseDto("暂无公告",200000);
        }
    }

    /**
     * 领取优惠券
     * @param discountUserVo
     * @param token
     * @return
     */
    @Override
    public Dto getDiscountCoupon(DiscountUserVo discountUserVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(discountUserVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        DiscountCoupon discountCoupon=appMapper.queryDiscountCoupon(discountUserVo.getDiscountId());
        if (discountCoupon.getCouponStatus()==1L){
            return DtoUtil.getFalseDto("该活动已过期",50100);
        }
        DiscountUser discountUser=appMapper.queryDiscountUser(discountUserVo.getUserId(),discountUserVo.getDiscountId());
        if (ObjectUtils.isEmpty(discountUser)){
            discountUser=new DiscountUser();
            discountUser.setUserId(Long.valueOf(discountUserVo.getUserId()));
            discountUser.setDiscountId(Long.valueOf(discountUserVo.getDiscountId()));
            discountUser.setStarTime(discountCoupon.getStarTime());
            discountUser.setEndTime(discountCoupon.getEndTime());
            int i=appMapper.getDiscountCoupon(discountUser);
            if (i<=0){
                return DtoUtil.getFalseDto("领取失败",50101);
            }
        }else {
            return DtoUtil.getFalseDto("你已经领取过了",50102);
        }
        return DtoUtil.getSuccessDto("领取成功",100000);
    }

    /**
     * 根据服务类型查询用户优惠券列表
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getUserDiscountList(ReceivedIdExtra receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        List<DiscountUser> discountUsers=appMapper.queryDiscountUserListByType(receivedId.getUserId(),receivedId.getCouponType());
        List<Map<String,String>> result=new ArrayList<>();
        for (DiscountUser discountUser:discountUsers) {
            Map<String,String> map=new HashMap<>(5);
            DiscountCoupon discountCoupon=appMapper.queryDiscountCoupon(discountUser.getDiscountId().toString());
            String money=new BigDecimal(discountCoupon.getCouponMoney()).stripTrailingZeros().toPlainString();
            map.put("discountUserId",discountUser.getId().toString());
            map.put("couponMoney",money+"元");
            map.put("couponName",discountCoupon.getCouponName());
            map.put("starTime",sdf.format(DateUtil.stampToDate(discountUser.getStarTime().toString())));
            map.put("entTime",sdf.format(DateUtil.stampToDate(discountUser.getEndTime().toString())));
            map.put("couponType",discountCoupon.getDescribe());
            result.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 查询用户可用优惠券数量
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto getUserDiscountCount(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<DiscountUser> discountUsers=appMapper.queryDiscountUserList(receivedId.getUserId());
        return DtoUtil.getSuccesWithDataDto("查询成功",discountUsers.size(),100000);
    }
}