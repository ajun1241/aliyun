package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;

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
}
