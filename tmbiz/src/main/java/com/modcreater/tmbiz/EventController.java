package com.modcreater.tmbiz;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbiz.service.EventService;
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

    @RequestMapping(value = "upl" ,method = RequestMethod.POST)
    public Dto uploading(@RequestBody SingleEvent singleEvent){
        return eventService.addNewEvents(singleEvent);
    }


}
