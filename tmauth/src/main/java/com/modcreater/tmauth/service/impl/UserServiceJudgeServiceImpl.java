package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserServiceJudgeService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.ServiceRemainingTime;
import com.modcreater.tmbeans.pojo.UserRealInfo;
import com.modcreater.tmdao.mapper.UserRealInfoMapper;
import com.modcreater.tmdao.mapper.UserServiceMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-31
 * Time: 14:08
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceJudgeServiceImpl implements UserServiceJudgeService {

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private UserRealInfoMapper realInfoMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用户查询功能判断
     * @param userId
     * @return
     */
    @Override
    public Dto searchServiceJudge(String userId) {
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId,"2");
        //用户未开通
        if (ObjectUtils.isEmpty(time)){
            return DtoUtil.getSuccessDto("该用户尚未开通查询功能",20000);
        }
        //开通了,查询次卡是否有剩余
        if (time.getResidueDegree() == 0){
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis()/1000){
                return DtoUtil.getSuccessDto("该用户尚未开通查询功能",20000);
            }
        }else {
            //有剩余,判断此次查询完毕后是否剩余为0次
            time.setResidueDegree(time.getResidueDegree()-1);
            //如果剩余次数为0,判断库存时间是否为0
            if (time.getResidueDegree() == 0 && time.getStorageTime()!= 0){
                //如果有库存时间,将这个时间加入用户有效的剩余时间中
                time.setTimeRemaining(System.currentTimeMillis()/1000 + time.getStorageTime());
            }
        }
        userServiceMapper.updateServiceRemainingTime(time);
        return null;
    }
    /**
     * 好友功能判断
     * @param userId
     * @return
     */
    @Override
    public Dto friendServiceJudge(String userId,String token) {
        if (StringUtils.isEmpty(userId)){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请重新登录",21014);
        }
        ServiceRemainingTime service=userServiceMapper.getServiceRemainingTime(userId,"1");
        if (ObjectUtils.isEmpty(service)){
            return DtoUtil.getFalseDto("好友功能尚未开通",37001);
        }
        return DtoUtil.getSuccessDto("好友功能已开通",37002);
    }

    /**
     * 年报功能判断
     * @param userId
     * @return
     */
    @Override
    public Dto annualReportingServiceJudge(String userId,String token) {
        if (StringUtils.isEmpty(userId)){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请重新登录",21014);
        }
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId,"4");
        //用户未开通
        if (ObjectUtils.isEmpty(time)){
            return DtoUtil.getSuccessDto("该用户尚未开通备份功能",20000);
        }
        //开通了,查询次卡是否有剩余
        if (time.getResidueDegree() == 0){
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis()/1000){
                return DtoUtil.getSuccessDto("该用户尚未开通备份功能",20000);
            }
        }else {
            //有剩余,判断此次查询完毕后是否剩余为0次
            time.setResidueDegree(time.getResidueDegree()-1);
            //如果剩余次数为0,判断库存时间是否为0
            if (time.getResidueDegree() == 0 && time.getStorageTime()!= 0){
                //如果有库存时间,将这个时间加入用户有效的剩余时间中
                time.setTimeRemaining(System.currentTimeMillis()/1000 + time.getStorageTime());
            }
        }
        return DtoUtil.getSuccessDto("年报功能已开通",100000);
    }

    /**
     * 备份功能判断
     * @param userId
     * @return
     */
    @Override
    public Dto backupServiceJudge(String userId,String token) {
        if (StringUtils.isEmpty(userId)){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请重新登录",21014);
        }
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId,"4");
        //用户未开通
        if (ObjectUtils.isEmpty(time)){
            return DtoUtil.getSuccessDto("该用户尚未开通备份功能",20000);
        }
        //开通了,查询次卡是否有剩余
        if (time.getResidueDegree() == 0){
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis()/1000){
                return DtoUtil.getSuccessDto("该用户尚未开通备份功能",20000);
            }
        }else {
            //有剩余,判断此次查询完毕后是否剩余为0次
            time.setResidueDegree(time.getResidueDegree()-1);
            //如果剩余次数为0,判断库存时间是否为0
            if (time.getResidueDegree() == 0 && time.getStorageTime()!= 0){
                //如果有库存时间,将这个时间加入用户有效的剩余时间中
                time.setTimeRemaining(System.currentTimeMillis()/1000 + time.getStorageTime());
            }
        }
        return DtoUtil.getSuccessDto("备份功能已开通",100000);
    }

    /**
     * 实名认证判断
     * @param userId
     * @return
     */
    @Override
    public Dto realInfoJudge(String userId,String token) {
        if (StringUtils.isEmpty(userId)){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请重新登录",21014);
        }
        UserRealInfo userRealInfo=realInfoMapper.queryDetail(userId);
        if (ObjectUtils.isEmpty(userRealInfo)){
            return DtoUtil.getFalseDto("实名认证尚未开通",200000);
        }else if (userRealInfo.getRealStatus() == 0 ){
            return DtoUtil.getFalseDto("实名认证正在认证中，请稍等",36001);
        }else if (userRealInfo.getRealStatus() == 2 ){
            return DtoUtil.getFalseDto("实名认证已驳回，请重新上传认证",36002);
        }
        return DtoUtil.getSuccessDto("实名认证已完成",100000);
    }
}
