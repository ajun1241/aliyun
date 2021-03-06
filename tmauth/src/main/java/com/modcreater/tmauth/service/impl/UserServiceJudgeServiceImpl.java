package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserServiceJudgeService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.ServiceFunction;
import com.modcreater.tmbeans.pojo.ServiceRemainingTime;
import com.modcreater.tmbeans.pojo.UserRealInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.UserRealInfoMapper;
import com.modcreater.tmdao.mapper.UserServiceMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *
     * @param userId
     * @return
     */
    @Override
    public Dto searchServiceJudge(String userId) {
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId, "2");
        //用户未开通
        if (ObjectUtils.isEmpty(time)) {
            return DtoUtil.getSuccessDto("该用户尚未开通查询功能", 200000);
        }
        //开通过,查询次卡是否有剩余
        if (time.getResidueDegree() == 0) {
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis() / 1000) {
                return DtoUtil.getSuccessDto("该用户尚未开通查询功能", 200000);
            }
        }
        return DtoUtil.getSuccessDto("查询服务已开通", 100000);
    }

    @Override
    public Dto searchServiceJudge(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return searchServiceJudge(userId);
    }

    /**
     * 好友功能判断
     *
     * @param userId
     * @return
     */
    @Override
    public Dto friendServiceJudge(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ServiceRemainingTime service = userServiceMapper.getServiceRemainingTime(userId, "1");
        if (ObjectUtils.isEmpty(service)) {
            return DtoUtil.getFalseDto("好友功能尚未开通", 200000);
        }
        return DtoUtil.getSuccesWithDataDto("好友功能已开通", "1", 100000);
    }

    /**
     * 报表功能判断
     *
     * @param userId
     * @return
     */
    @Override
    public Dto annualReportingServiceJudge(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId, "3");
        //用户未开通
        if (ObjectUtils.isEmpty(time)) {
            return DtoUtil.getSuccessDto("该用户尚未开通报表功能", 20000);
        }
        //无剩余,判断剩余年/月卡时间
        Long timeRemaining = time.getTimeRemaining();
        if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis() / 1000) {
            return DtoUtil.getSuccessDto("该用户尚未开通报表功能", 20000);
        }
        return DtoUtil.getSuccessDto("报表服务功能已开通", 100000);
    }

    /**
     * 备份功能判断
     *
     * @param userId
     * @return
     */
    @Override
    public Dto backupServiceJudge(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId, "4");
        //用户未开通
        if (ObjectUtils.isEmpty(time)) {
            return DtoUtil.getSuccessDto("该用户尚未开通备份功能", 20000);
        }
        //开通了,查询次卡是否有剩余
        if (time.getResidueDegree() == 0) {
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis() / 1000) {
                time.setResidueDegree(-1L);
                userServiceMapper.updateServiceRemainingTime(time);
                return DtoUtil.getSuccesWithDataDto("该用户尚未开通备份功能","2", 20000);
            }
        }else if (time.getResidueDegree() == -1){
            return DtoUtil.getSuccesWithDataDto("该用户尚未开通备份功能","2", 20000);
        }
        return DtoUtil.getSuccessDto("备份功能已开通", 100000);
    }

    /**
     * 实名认证判断
     *
     * @param userId
     * @return
     */
    @Override
    public Dto realInfoJudge(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        UserRealInfo userRealInfo = realInfoMapper.queryDetail(userId);
        Map<String, String> map = new HashMap<>();
        if (ObjectUtils.isEmpty(userRealInfo)) {
            map.put("status", "3");
            return DtoUtil.getSuccesWithDataDto("实名认证尚未开通", map, 100000);
        } else if (userRealInfo.getRealStatus() == 0) {
            map.put("status", userRealInfo.getRealStatus().toString());
            return DtoUtil.getSuccesWithDataDto("实名认证正在认证中，请稍等", map, 100000);
        } else if (userRealInfo.getRealStatus() == 2) {
            map.put("status", userRealInfo.getRealStatus().toString());
            return DtoUtil.getSuccesWithDataDto("实名认证已驳回，请重新上传认证", map, 100000);
        } else {
            map.put("status", userRealInfo.getRealStatus().toString());
            return DtoUtil.getSuccesWithDataDto("实名认证已完成", map, 100000);
        }
    }

    @Override
    public Dto queryUserAllServiceFunction(ReceivedId receivedId, String token) {
        if (StringUtils.isEmpty(receivedId.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<ServiceRemainingTime> serviceRemainingTimes = userServiceMapper.getAllServiceRemainingTime(receivedId.getUserId());
        Map<String, String> result = new HashMap<>(4);
        result.put("friendService", "0");
        result.put("searchService", "0");
        result.put("annualReportingService", "0");
        result.put("backupService", "0");
        for (ServiceRemainingTime serviceRemainingTime : serviceRemainingTimes) {
            if (serviceRemainingTime.getServiceId().equals("1")) {
                if (friendServiceJudge(receivedId.getUserId(), token).getResCode() == 100000) {
                    result.put("friendService", "1");
                }
            } else if (serviceRemainingTime.getServiceId().equals("2")) {
                if (searchServiceJudge(receivedId.getUserId()).getResCode() == 100000) {
                    result.put("searchService", "1");
                }
            } else if (serviceRemainingTime.getServiceId().equals("3")) {
                if (annualReportingServiceJudge(receivedId.getUserId(), token).getResCode() == 100000) {
                    result.put("annualReportingService", "1");
                }
            } else if (serviceRemainingTime.getServiceId().equals("4")) {
                Dto dto = backupServiceJudge(receivedId.getUserId(), token);
                if (dto.getResCode() == 100000) {
                    result.put("backupService", "1");
                }else if (dto.getResCode() == 20000 && !ObjectUtils.isEmpty(dto.getData()) && dto.getData().equals("2")){
                    result.put("backupService", "2");
                }
            }
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }
}
