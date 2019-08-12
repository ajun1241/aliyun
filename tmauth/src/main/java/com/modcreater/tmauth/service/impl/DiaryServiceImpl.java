package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.DiaryService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.CommentReply;
import com.modcreater.tmbeans.pojo.Diary;
import com.modcreater.tmbeans.pojo.DiaryComment;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmdao.mapper.DiaryMapper;
import com.modcreater.tmdao.mapper.SensitiveWordsMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmauth.config.filter.SensitiveWordsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/11 16:01
 */
@Service
public class DiaryServiceImpl implements DiaryService {

    private Logger logger = LoggerFactory.getLogger(DiaryServiceImpl.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SensitiveWordsMapper sensitiveWordsMapper;

    @Resource
    private DiaryMapper diaryMapper;

    /**
     * 添加一条日记
     * @param diaryVo
     * @param token
     * @return
     */
    @Override
    public Dto addNewDiary(DiaryVo diaryVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(diaryVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        //判断是否可以添加
        if (!ObjectUtils.isEmpty(diaryMapper.queryDiaryByDate(diaryVo.getUserId(),sdf.format(new Date())))){
            return DtoUtil.getFalseDto("今天已经有日记了，不能添加",70101);
        }else {
            Diary diary=new Diary();
            diary.setUserId(Long.parseLong(diaryVo.getUserId()));
            diary.setContent(diaryVo.getContent());
            diary.setMoodType(StringUtils.isEmpty(diaryVo.getMoodType()) ? 0L : Long.parseLong(diaryVo.getMoodType()));
            diary.setStatus(StringUtils.isEmpty(diaryVo.getStatus()) ? 0L : Long.parseLong(diaryVo.getStatus()));
            diary.setCover(diaryVo.getCover());
            diary.setWeather(diaryVo.getWeather());
            if (diaryMapper.addNewDiary(diary)<=0){
                return DtoUtil.getFalseDto("添加失败",70102);
            }
        }
        return DtoUtil.getSuccessDto("添加成功",100000);
    }

    /**
     * 修改日记（只能修改当天的）
     * @param diaryVo
     * @param token
     * @return
     */
    @Override
    public Dto updateDiary(DiaryVo diaryVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(diaryVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Diary diary=diaryMapper.queryDiaryDetail(diaryVo.getDiaryId());
        if (!ObjectUtils.isEmpty(diary) && sdf.format(new Date()).equals(sdf.format(diary.getCreateDate()))){
            diary=new Diary();
            diary.setId(Long.parseLong(diaryVo.getDiaryId()));
            diary.setContent(diaryVo.getContent());
            diary.setMoodType(Long.parseLong(StringUtils.isEmpty(diaryVo.getMoodType())?"-1":diaryVo.getMoodType()));
            diary.setStatus(Long.parseLong(diaryVo.getStatus()));
            diary.setCover(diaryVo.getCover());
            diary.setWeather(diaryVo.getWeather());
            if (diaryMapper.updateDiary(diary)<=0){
                return DtoUtil.getFalseDto("没有任何修改",70201);
            }
        }else {
            return DtoUtil.getFalseDto("只能修改当天的日记",70202);
        }
        return DtoUtil.getSuccessDto("修改成功",100000);
    }

    /**
     * 删除日记（只能删当天的）
     * @param diaryVo
     * @param token
     * @return
     */
    @Override
    public Dto deleteDiary(DiaryVo diaryVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(diaryVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Diary diary=diaryMapper.queryDiaryDetail(diaryVo.getDiaryId());
        if (!ObjectUtils.isEmpty(diary)) {
            if (sdf.format(new Date()).equals(sdf.format(diary.getCreateDate()))) {
                if (diary.getUserId().toString().equals(diaryVo.getUserId())) {
                    if (diaryMapper.deleteDiary(diaryVo.getDiaryId()) <= 0) {
                        return DtoUtil.getFalseDto("删除失败", 70301);
                    }
                } else {
                    return DtoUtil.getFalseDto("只能删除自己的日记", 70302);
                }
            } else {
                return DtoUtil.getFalseDto("只能删除当天的日记", 70302);
            }
        } else {
            return DtoUtil.getFalseDto("删除的日记未找到", 70303);
        }
        return DtoUtil.getSuccessDto("删除成功",100000);
    }

    /**
     * 查询所有日记
     * @param queryDiaryVo
     * @param token
     * @return
     */
    @Override
    public Dto queryDiaryList(QueryDiaryVo queryDiaryVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(queryDiaryVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(queryDiaryVo.getPageSize());
        if (StringUtils.isEmpty(queryDiaryVo.getPageNumber())){
            queryDiaryVo.setPageNumber("1");
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int pageIndex=(Integer.parseInt(queryDiaryVo.getPageNumber())-1)*pageSize;
        List<Diary> list=diaryMapper.queryDiaryList(queryDiaryVo.getUserId(),pageIndex,pageSize);
        List<DiaryResultVo> result=new ArrayList<>();
        for (Diary diary:list) {
            DiaryResultVo resultVo=new DiaryResultVo();
            resultVo.setDiaryId(diary.getId());
            resultVo.setContent(diary.getContent());
            resultVo.setCreateDate(sdf.format(diary.getCreateDate()));
            resultVo.setMoodType(diary.getMoodType());
            resultVo.setStatus(diary.getStatus());
            resultVo.setUserId(diary.getUserId());
            resultVo.setCover(diary.getCover());
            resultVo.setWeather(diary.getWeather());
            result.add(resultVo);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 查询日记详情
     * @param diaryVo
     * @param token
     * @return
     */
    @Override
    public Dto queryDiaryDetail(DiaryVo diaryVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(diaryVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Diary diary=diaryMapper.queryDiaryDetail(diaryVo.getDiaryId());
        if (ObjectUtils.isEmpty(diary)){
            return DtoUtil.getFalseDto("该日记不存在，可能已被删除",200000);
        }
        DiaryResultVo resultVo=new DiaryResultVo();
        //自己查看自己的日记详情
        if (diary.getUserId().toString().equals(diaryVo.getUserId())){
            //显示日记内容
            //显示评论详情
            //显示评论回复
            //显示转发次数
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resultVo.setDiaryId(diary.getId());
            resultVo.setContent(diary.getContent());
            resultVo.setCreateDate(sdf.format(diary.getCreateDate()));
            resultVo.setMoodType(diary.getMoodType());
            resultVo.setStatus(diary.getStatus());
            resultVo.setUserId(diary.getUserId());
            resultVo.setCover(diary.getCover());
            resultVo.setWeather(diary.getWeather());
        }else {//查看好友的日记详情
            //判断权限
            //显示日记内容
            //显示评论内容隐藏评论者名字
            //显示转发次数
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",resultVo,100000);
    }

    /**
     * 查询好友的日记列表
     * @param queryDiaryVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendDiaryList(QueryDiaryVo queryDiaryVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(queryDiaryVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(queryDiaryVo.getPageSize());
        if (StringUtils.isEmpty(queryDiaryVo.getPageNumber())){
            queryDiaryVo.setPageNumber("1");
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int pageIndex=(Integer.parseInt(queryDiaryVo.getPageNumber())-1)*pageSize;
        List<Diary> list=diaryMapper.queryFriendsDiaryList(queryDiaryVo.getFriendId(),pageIndex,pageSize);
        List<DiaryResultVo> result=new ArrayList<>();
        for (Diary diary:list) {
            DiaryResultVo resultVo=new DiaryResultVo();
            resultVo.setDiaryId(diary.getId());
            resultVo.setContent(diary.getContent());
            resultVo.setCreateDate(sdf.format(diary.getCreateDate()));
            resultVo.setMoodType(diary.getMoodType());
            resultVo.setStatus(diary.getStatus());
            resultVo.setUserId(diary.getUserId());
            resultVo.setCover(diary.getCover());
            resultVo.setWeather(diary.getWeather());
            result.add(resultVo);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 评论一条日记
     * @param commentVo
     * @param token
     * @return
     */
    @Override
    public Dto commentDiary(CommentVo commentVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(commentVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        try {
            SensitiveWordsFilter sensitiveWordsFilter=new SensitiveWordsFilter();
            //初始化敏感词
            sensitiveWordsFilter.initSensitiveWordsMap(sensitiveWordsMapper.sensitiveWords());
            //拦截敏感词
            Set<String> set=sensitiveWordsFilter.getSensitiveWords(commentVo.getCommentContent(),SensitiveWordsFilter.MatchType.MAX_MATCH);
            if (set.size()>0){
                logger.info("评论敏感词："+set.toString());
                return DtoUtil.getFalseDto("内容包含不恰当词汇",99999);
            }
            DiaryComment diaryComment=new DiaryComment();
            diaryComment.setDiaryId(Long.valueOf(commentVo.getDiaryId()));
            diaryComment.setUserId(Long.valueOf(commentVo.getUserId()));
            diaryComment.setCommentContent(commentVo.getCommentContent());
            if (diaryMapper.addComment(diaryComment)<1){
                return DtoUtil.getFalseDto("评论失败",30001);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("评论失败",30001);
        }
        return DtoUtil.getSuccessDto("评论成功",100000);
    }

    /**
     * 回复一条评论
     * @param commentReplyVo
     * @param token
     * @return
     */
    @Override
    public Dto replyComment(CommentReplyVo commentReplyVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(commentReplyVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        try {
            SensitiveWordsFilter sensitiveWordsFilter=new SensitiveWordsFilter();
            //初始化敏感词
            sensitiveWordsFilter.initSensitiveWordsMap(sensitiveWordsMapper.sensitiveWords());
            //拦截敏感词
            Set<String> set=sensitiveWordsFilter.getSensitiveWords(commentReplyVo.getReplyContent(),SensitiveWordsFilter.MatchType.MAX_MATCH);
            if (set.size()>0){
                logger.info("评论敏感词："+set.toString());
                return DtoUtil.getFalseDto("内容包含不恰当词汇",99999);
            }
            CommentReply commentReply=new CommentReply();
            commentReply.setDiaryId(Long.valueOf(commentReplyVo.getDiaryId()));
            commentReply.setUserId(Long.valueOf(commentReplyVo.getUserId()));
            commentReply.setReplyContent(commentReplyVo.getReplyContent());
            commentReply.setCommentId(Long.valueOf(commentReplyVo.getCommentId()));
            if (diaryMapper.addCommentReply(commentReply)<1){
                return DtoUtil.getFalseDto("回复评论失败",30001);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("回复评论失败",30001);
        }
        return DtoUtil.getSuccessDto("回复评论成功",100000);
    }

    /**
     * 转发分享日记
     * @param transmitVo
     * @param token
     * @return
     */
    @Override
    public Dto transmitDiary(TransmitVo transmitVo, String token) {
        return null;
    }
}
