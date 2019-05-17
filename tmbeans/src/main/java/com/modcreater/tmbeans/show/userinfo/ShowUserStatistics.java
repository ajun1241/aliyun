package com.modcreater.tmbeans.show.userinfo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 14:56
 */
@Data
public class ShowUserStatistics {
    /**
     * 已完成事件数量
     */
    private Long completed;
    /**
     * 未完成事件数量
     */
    private Long unfinished;
    /**
     * 草稿箱
     */
    private Long drafts;

}
