package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.EventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 14:54
 */
@RestController
@RequestMapping(value = "/event/")
public class EventController {

    @Resource
    private EventService eventService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);
    /**
     * 添加一条事件
     *
     * @param uploadingEventVo
     * @return
     */
    @RequestMapping(value = "upl", method = RequestMethod.POST)
    public Dto uploadingEvents(@RequestBody UploadingEventVo uploadingEventVo) {
        LOGGER.info("添加事件===>"+uploadingEventVo.toString());
        return eventService.addNewEvents(uploadingEventVo);
    }

    /**
     * 删除一条事件
     *
     * @param deleteEventVo
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public Dto deleteEvent(@RequestBody DeleteEventVo deleteEventVo) {
        LOGGER.info("删除事件===>"+deleteEventVo.toString());
        return eventService.deleteEvents(deleteEventVo);
    }

    /**
     * 修改一条事件
     *
     * @param updateEventVo
     * @return
     */
    @RequestMapping(value = "upd", method = RequestMethod.POST)
    public Dto updateEvents(@RequestBody UpdateEventVo updateEventVo) {
        LOGGER.info("修改事件===>"+updateEventVo.toString());
        return eventService.updateEvents(updateEventVo);
    }

    /**
     * 同步本地数据
     *
     * @param synchronousUpdateVo
     * @return
     */
    /*@RequestMapping(value = "syup", method = RequestMethod.POST)
    public Dto synchronousUpdate(@RequestBody SynchronousUpdateVo synchronousUpdateVo) {
        return eventService.synchronousUpdate(synchronousUpdateVo);
    }*/

    /**
     * 对比时间戳
     * @param contrastTimestampVo
     * @return
     */
    @RequestMapping(value = "ctime",method = RequestMethod.POST)
    public Dto contrastTimestamp(@RequestBody ContrastTimestampVo contrastTimestampVo){
        LOGGER.info("对比时间戳===>"+contrastTimestampVo.toString());
        return eventService.contrastTimestamp(contrastTimestampVo);
    }

    /**
     * 第一次登录事件数据同步
     * @param synchronousUpdateVo
     * @return
     */
    @RequestMapping(value = "firupl",method = RequestMethod.POST)
    public Dto firstUplEvent(@RequestBody SynchronousUpdateVo synchronousUpdateVo){
        LOGGER.info("第一次登陆事件数据同步===>"+synchronousUpdateVo.toString());
        return eventService.firstUplEvent(synchronousUpdateVo);
    }

    /**
     * 草稿上传
     * @param draftVo
     * @return
     */
    @RequestMapping(value = "upldraft",method = RequestMethod.POST)
    public Dto uplDraft(@RequestBody DraftVo draftVo){
        LOGGER.info("草稿上传===>"+draftVo.toString());
        return eventService.uplDraft(draftVo);
    }


    /**
     * 根据"日"查找事件
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "seabyday",method = RequestMethod.POST)
    public Dto searchByDay(@RequestBody SearchEventVo searchEventVo){
        LOGGER.info("根据日查找事件===>"+searchEventVo.toString());
        return eventService.searchByDayEventIds(searchEventVo);
    }

    /**
     * 根据"月"查找事件
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "seabymon",method = RequestMethod.POST)
    public Dto searchByMonth(@RequestBody SearchEventVo searchEventVo){
        LOGGER.info("根据月查找事件===>"+searchEventVo.toString());
        return eventService.searchByDayEventIdsInMonth(searchEventVo);
    }

    /**
     * 根据"周"查找事件
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "seabyweek",method = RequestMethod.POST)
    public Dto searchByWeek(@RequestBody SearchEventVo searchEventVo){
        LOGGER.info("根据周查找事件===>"+searchEventVo.toString());
        return eventService.searchByDayEventIdsInWeek(searchEventVo);
    }
}
