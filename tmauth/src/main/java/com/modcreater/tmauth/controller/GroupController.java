package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.group.ReceivedGroupId;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-06
 * Time: 9:45
 */
@RestController
@RequestMapping(value = "/group/")
public class GroupController {

    @Resource
    private GroupService groupService;

    @PostMapping(value = "creategroup")
    @ApiOperation("创建团队")
    public Dto createGroup(@RequestBody GroupInfoVo groupInfoVo,HttpServletRequest request){
        return groupService.createGroup(groupInfoVo,request.getHeader("token"));
    }

    @PostMapping(value = "getmygroup")
    @ApiOperation("获取我的团队")
    public Dto getMyGroup(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return groupService.queryGroupList(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "isneedvalueadded")
    @ApiOperation("获取我创建的团队的数量")
    public Dto isNeedValueAdded(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return groupService.isNeedValueAdded(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "getmygroupinfo")
    @ApiOperation("获取团队详细信息")
    public Dto getMyGroupInfo(@RequestBody ReceivedGroupId receivedGroupId, HttpServletRequest request){
        return groupService.getMyGroupInfo(receivedGroupId,request.getHeader("token"));
    }

}
