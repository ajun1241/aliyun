package com.modcreater.tmbeans.vo;

import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper = true)
public class UploadingEventVo extends SingleEvent implements Serializable {
    /**
     * 用户ID
     */
    private String userId;


}
