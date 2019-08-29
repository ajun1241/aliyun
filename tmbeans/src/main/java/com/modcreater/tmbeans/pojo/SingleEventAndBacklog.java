package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/28 9:36
 */
@Data
public class SingleEventAndBacklog extends SingleEvent{
    private List<BacklogList> backlogList;
}
