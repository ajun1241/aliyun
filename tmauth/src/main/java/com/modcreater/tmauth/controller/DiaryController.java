package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.DiaryService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.CommentReplyVo;
import com.modcreater.tmbeans.vo.CommentVo;
import com.modcreater.tmbeans.vo.DiaryVo;
import com.modcreater.tmbeans.vo.QueryDiaryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/11 17:34
 */
@RequestMapping("diary")
@RestController
public class DiaryController {

    @Resource
    private DiaryService diaryService;

    @Safety
    @PostMapping("addnewdiary")
    @ApiOperation("添加日记")
    public Dto addNewDiary(@RequestBody DiaryVo diaryVo, HttpServletRequest request){
        return diaryService.addNewDiary(diaryVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("updatediary")
    @ApiOperation("修改日记")
    public Dto updateDiary(@RequestBody DiaryVo diaryVo, HttpServletRequest request){
        return diaryService.updateDiary(diaryVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("deletediary")
    @ApiOperation("删除日记")
    public Dto deleteDiary(@RequestBody DiaryVo diaryVo, HttpServletRequest request){
        return diaryService.deleteDiary(diaryVo,request.getHeader("token"));
    }


    @PostMapping("querydiarylist")
    @ApiOperation("查询历史日记")
    public Dto queryDiaryList(@RequestBody QueryDiaryVo queryDiaryVo, HttpServletRequest request){
        return diaryService.queryDiaryList(queryDiaryVo,request.getHeader("token"));
    }


    @PostMapping("querydiarydetail")
    @ApiOperation("查询日记详情")
    public Dto queryDiaryDetail(@RequestBody DiaryVo diaryVo, HttpServletRequest request){
        return diaryService.queryDiaryDetail(diaryVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("queryFriendDiaryList")
    @ApiOperation("查询好友的日记历史日记")
    public Dto queryFriendDiaryList(@RequestBody QueryDiaryVo queryDiaryVo, HttpServletRequest request){
        return diaryService.queryFriendDiaryList(queryDiaryVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("commentdiary")
    @ApiOperation("评论一篇日记")
    public Dto commentDiary(@RequestBody CommentVo commentVo, HttpServletRequest request){
        return diaryService.commentDiary(commentVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("replycomment")
    @ApiOperation("回复评论")
    public Dto replyComment(@RequestBody CommentReplyVo commentReplyVo, HttpServletRequest request){
        return diaryService.replyComment(commentReplyVo,request.getHeader("token"));
    }


}
