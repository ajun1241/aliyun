package com.modcreater.tmbeans.utils;

import mob.push.api.model.PushWork;
import mob.push.api.utils.AndroidNotifyStyleEnum;
import mob.push.api.utils.MobHelper;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/20 16:52
 */
public class PushWorkExtra extends PushWork {
    public Integer workType;
    public Long taskTime;
    public PushWorkExtra bulidTimeTask(Integer workType,Long taskTime) {
        if (workType != null) {
            this.workType = workType;
        }
        if (taskTime != null) {
            this.taskTime = taskTime;
        }
        return this;
    }
}
