package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-31
 * Time: 14:08
 */
public interface UserServiceJudgeService {

    /**
     * 用户查询功能判断
     * @param userId
     * @return
     */
    Dto searchServiceJudge(String userId);

    /**
     * 好友功能判断
     * @param userId
     * @return
     */
    Dto friendServiceJudge(String userId,String token);

    /**
     * 年报功能判断
     * @param userId
     * @return
     */
    Dto annualReportingServiceJudge(String userId,String token);

    /**
     * 备份功能判断
     * @param userId
     * @return
     */
    Dto backupServiceJudge(String userId,String token);

    /**
     * 实名认证判断
     * @param userId
     * @return
     */
    Dto realInfoJudge(String userId,String token);

}
