package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmbiz.dao.EventMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 11:32
 */
@Service
public class EventServiceImpl implements EventService {

    @Resource
    private EventMapper eventMapper;

    @Override
    public Dto addNewEvents(SingleEvent singleEvent) {
        if (!StringUtils.isEmpty(singleEvent)) {
            if (eventMapper.uploadingEvents(singleEvent) > 0) {
                return DtoUtil.getSuccesDto("事件上传成功", "21000");
            }else {
                return DtoUtil.getFalseDto("事件上传失败",21001);
            }
        }
        return DtoUtil.getFalseDto("没有内容", 21002);
    }

    @Override
    public Dto deleteEvents(String userId) {
        return null;
    }

    @Override
    public Dto updateEvents(SingleEvent singleEvent) {
        return null;
    }

    @Override
    public Dto searchEvents(String userId) {
        return null;
    }
}
