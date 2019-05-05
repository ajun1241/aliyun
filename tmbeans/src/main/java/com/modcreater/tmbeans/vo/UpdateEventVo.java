package com.modcreater.tmbeans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-30
 * Time: 16:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventVo extends UploadingEventVo implements Serializable {
}
