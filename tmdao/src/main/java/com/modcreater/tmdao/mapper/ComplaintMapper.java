package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.vo.ComplaintVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/4 15:29
 */
@Mapper
public interface ComplaintMapper {
    /**
     * 上传投诉
     * @param complaintVo
     * @return
     */
    int addComplaint(ComplaintVo complaintVo);


}
