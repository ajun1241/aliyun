package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedStudentRealInfo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 13:52
 */
public interface ManageService {

    /**
     * 上传用户真实信息
     * @param receivedUserRealInfo
     * @param token
     * @return
     */
    Dto uploadUserRealInfo(ReceivedUserRealInfo receivedUserRealInfo, String token);

    /**
     * 投诉
     * @param complaintVo
     * @param token
     * @return
     */
    Dto complaint(ComplaintVo complaintVo,String token);

    /**
     * 更换实名信息
     * @param receivedId
     * @param token
     * @return
     */
    Dto changeRealInfo(ReceivedId receivedId, String token);

    /**
     * 学生实名认证
     * @param receivedStudentRealInfo
     * @param token
     * @return
     */
    Dto uploadStudentRealInfo(ReceivedStudentRealInfo receivedStudentRealInfo, String token);

    /**
     * 查询实名认证信息
     * @param receivedId
     * @param token
     * @return
     */
    Dto queryRealInfo(ReceivedId receivedId,String token);
}
