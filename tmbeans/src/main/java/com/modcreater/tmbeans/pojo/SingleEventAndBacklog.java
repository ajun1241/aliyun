package com.modcreater.tmbeans.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/28 9:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SingleEventAndBacklog extends SingleEvent{
    private List<BacklogList> backlogList;
    private String isSync;
}
