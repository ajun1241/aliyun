package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.CommentReply;
import com.modcreater.tmbeans.pojo.Diary;
import com.modcreater.tmbeans.pojo.DiaryComment;
import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/11 16:07
 */
public interface DiaryMapper {

    /**
     * 添加一条日记
     * @param diary
     * @return
     */
    int addNewDiary(Diary diary);

    /**
     * 修改日记（只能修改当天的）
     * @param diary
     * @return
     */
    int updateDiary(Diary diary);

    /**
     * 删除日记（只能删当天的）
     * @param id
     * @return
     */
    int deleteDiary(String id);

    /**
     * 查询历史日记
     * @param userId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<Diary> queryDiaryList(String userId,int pageIndex,int pageSize);

    /**
     * 查询日记详情
     * @param id
     * @return
     */
    Diary queryDiaryDetail(String id);

    /**
     * 根据日期查日记
     * @param userId
     * @param date
     * @return
     */
    Diary queryDiaryByDate(String userId,String date);

    /**
     * 查询好友的日记列表
     * @param friendId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<Diary> queryFriendsDiaryList(String friendId, int pageIndex, int pageSize);

    /**
     * 评论日记
     * @param diaryComment
     * @return
     */
    int addComment(DiaryComment diaryComment);

    /**
     * 回复评论
     * @param commentReply
     * @return
     */
    int addCommentReply(CommentReply commentReply);
}
