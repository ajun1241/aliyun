package com.modcreater.tmstore.controller;


import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.store.ApproveInfoVo;
import com.modcreater.tmstore.service.StoreService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
/**
 * @Author: AJun
 */
@RestController
@RequestMapping("/store/")
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping("uploadapproveinfo")
    @ApiOperation("上传商铺认证信息")
    public Dto uploadApproveInfo(@RequestBody ApproveInfoVo approveInfoVo, HttpServletRequest request){
        return storeService.uploadApproveInfo(approveInfoVo,request.getHeader("token"));
    }
}
