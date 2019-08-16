package com.modcreater.tmbeans.vo.eventvo;

import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-30
 * Time: 16:05
 */
@Data
public class UploadingEventVo implements Serializable {
    /**
     * 用户ID
     */
    private String userId;
    private String singleEvent;
//    private String backlogList;
    private String apptype;
}
