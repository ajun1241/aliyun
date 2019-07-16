package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.DiaryService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Diary;
import com.modcreater.tmbeans.vo.DiaryResultVo;
import com.modcreater.tmbeans.vo.DiaryVo;
import com.modcreater.tmbeans.vo.QueryDiaryVo;
import com.modcreater.tmdao.mapper.DiaryMapper;
import com.modcreater.tmutils.DtoUtil;
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
            diary.setCreateDate(sdf.format(new Date()));
            diary.setDiaryImage(diaryVo.getDiaryImage());
            diary.setMoodType(Long.parseLong(diaryVo.getMoodType()));
            diary.setStatus(Long.parseLong(diaryVo.getStatus()));
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
        if (!ObjectUtils.isEmpty(diary) && sdf.format(new Date()).equals(diary.getCreateDate())){
            diary=new Diary();
            diary.setDiaryId(Long.parseLong(diaryVo.getDiaryId()));
            diary.setUserId(Long.parseLong(diaryVo.getUserId()));
            diary.setContent(diaryVo.getContent());
            diary.setDiaryImage(diaryVo.getDiaryImage());
            diary.setMoodType(Long.parseLong(StringUtils.isEmpty(diaryVo.getMoodType())?"-1":diaryVo.getMoodType()));
            diary.setStatus(Long.parseLong(diaryVo.getStatus()));
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
        if (!ObjectUtils.isEmpty(diary) && sdf.format(new Date()).equals(diary.getCreateDate()) && diary.getUserId().toString().equals(diaryVo.getUserId())) {
            if (diaryMapper.deleteDiary(diaryVo.getDiaryId())<=0){
                return DtoUtil.getFalseDto("删除失败",70301);
            }
        }else {
            return DtoUtil.getFalseDto("只能删除当天的日记",70302);
        }
        return DtoUtil.getSuccessDto("删除成功",100000);
    }

    /**
     * 查询历史日记
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
        int pageIndex=(Integer.parseInt(queryDiaryVo.getPageNumber())-1)*pageSize;
        List<Diary> list=diaryMapper.queryDiaryList(queryDiaryVo.getUserId().toString(),pageIndex,pageSize);
        List<DiaryResultVo> result=new ArrayList<>();
        for (Diary diary:list) {
            DiaryResultVo resultVo=new DiaryResultVo();
            resultVo.setDiaryId(diary.getDiaryId());
            resultVo.setContent(diary.getContent());
            resultVo.setCreateDate(diary.getCreateDate());
            resultVo.setMoodType(diary.getMoodType());
            resultVo.setStatus(diary.getStatus());
            resultVo.setUserId(diary.getUserId());
            resultVo.setDiaryImage(diary.getDiaryImage().replace("[","").replace("]","").split(","));
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
        resultVo.setDiaryId(diary.getDiaryId());
        resultVo.setContent(diary.getContent());
        resultVo.setCreateDate(diary.getCreateDate());
        resultVo.setMoodType(diary.getMoodType());
        resultVo.setStatus(diary.getStatus());
        resultVo.setUserId(diary.getUserId());
        resultVo.setDiaryImage(diary.getDiaryImage().replace("[","").replace("]","").split(","));
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
        int pageIndex=(Integer.parseInt(queryDiaryVo.getPageNumber())-1)*pageSize;
        List<Diary> list=diaryMapper.queryFriendsDiaryList(queryDiaryVo.getFriendId(),pageIndex,pageSize);
        List<DiaryResultVo> result=new ArrayList<>();
        for (Diary diary:list) {
            DiaryResultVo resultVo=new DiaryResultVo();
            resultVo.setDiaryId(diary.getDiaryId());
            resultVo.setContent(diary.getContent());
            resultVo.setCreateDate(diary.getCreateDate());
            resultVo.setMoodType(diary.getMoodType());
            resultVo.setStatus(diary.getStatus());
            resultVo.setUserId(diary.getUserId());
            resultVo.setDiaryImage(diary.getDiaryImage().replace("[","").replace("]","").split(","));
            result.add(resultVo);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }
}
