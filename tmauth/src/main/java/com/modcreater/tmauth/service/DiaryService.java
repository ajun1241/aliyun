package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.DiaryVo;
import com.modcreater.tmbeans.vo.QueryDiaryVo;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/11 15:28
 */
public interface DiaryService {

    /**
     * 添加一条日记
     * @param diaryVo
     * @param token
     * @return
     */
    Dto addNewDiary(DiaryVo diaryVo, String token);

    /**
     * 修改日记（只能修改当天的）
     * @param diaryVo
     * @param token
     * @return
     */
    Dto updateDiary(DiaryVo diaryVo,String token);

    /**
     * 删除日记（只能删当天的）
     * @param diaryVo
     * @param token
     * @return
     */
    Dto deleteDiary(DiaryVo diaryVo,String token);

    /**
     * 查询历史日记
     * @param queryDiaryVo
     * @param token
     * @return
     */
    Dto queryDiaryList(QueryDiaryVo queryDiaryVo, String token);

    /**
     * 查询日记详情
     * @param diaryVo
     * @param token
     * @return
     */
    Dto queryDiaryDetail(DiaryVo diaryVo, String token);


    /**
     * 查询好友的日记列表
     * @param queryDiaryVo
     * @param token
     * @return
     */
    Dto queryFriendDiaryList(QueryDiaryVo queryDiaryVo, String token);


}
