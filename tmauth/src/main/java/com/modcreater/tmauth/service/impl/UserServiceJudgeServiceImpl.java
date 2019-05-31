package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserServiceJudgeService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.ServiceRemainingTime;
import com.modcreater.tmdao.mapper.UserServiceMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
public class UserServiceJudgeServiceImpl implements UserServiceJudgeService {

    @Resource
    private UserServiceMapper userServiceMapper;

    @Override
    public Dto searchServiceJudge(String userId) {
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userId,"2");
        //用户未开通
        if (ObjectUtils.isEmpty(time)){
            return DtoUtil.getFalseDto("该用户尚未开通查询功能",40007);
        }
        //开通了,查询次卡是否有剩余
        if (time.getResidueDegree() == 0){
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis()/1000){
                return DtoUtil.getFalseDto("该用户尚未开通查询功能",40007);
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
        return null;
    }

    @Override
    public Dto friendServiceJudge(String userId) {
        return null;
    }

    @Override
    public Dto annualReportingServiceJudge(String userId) {
        return null;
    }

    @Override
    public Dto backupServiceJudge(String userId) {
        return null;
    }
}
