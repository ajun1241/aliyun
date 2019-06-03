package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 14:22
 */
@Mapper
public interface UserRealInfoMapper {

    /**
     * 添加一条新的用户真实信息
     * @param receivedUserRealInfo
     * @return
     */
    int addNewRealInfo(ReceivedUserRealInfo receivedUserRealInfo);
}
