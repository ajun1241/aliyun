package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.QueryEventVo;
import com.modcreater.tmbiz.service.EventService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/event/")
public class EventController {
    @Resource
    private EventService eventService;

    @PostMapping("queryevent")
    @ApiOperation("查询事件")
    public Dto queryEvent(@RequestBody QueryEventVo queryEventVo){
        System.out.println(queryEventVo.toString());
        return eventService.queryEvent(queryEventVo);
    }
    @PostMapping("addevent")
    @ApiOperation("查询事件")
    public Dto addEvent(@RequestBody SingleEvent singleEvent){
        System.out.println(singleEvent.toString());
        return eventService.addEvent(singleEvent);
    }
    /*@PostMapping("queryevent")
    @ApiOperation("查询事件")
    public Dto queryEvent(@RequestBody QueryEventVo queryEventVo){
        System.out.println(queryEventVo.toString());
        return eventService.deleteEvent(queryEventVo);
    }*/
    /*@PostMapping("queryevent")
    @ApiOperation("查询事件")
    public Dto queryEvent(@RequestBody QueryEventVo queryEventVo){
        System.out.println(queryEventVo.toString());
        return eventService.queryEvent(queryEventVo);
    }
    @PostMapping("queryevent")
    @ApiOperation("查询事件")
    public Dto queryEvent(@RequestBody QueryEventVo queryEventVo){
        System.out.println(queryEventVo.toString());
        return eventService.queryEvent(queryEventVo);
    }*/
}
