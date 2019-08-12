package com.modcreater.tmdao.mapper;

import java.util.Set;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/8 14:48
 */

public interface SensitiveWordsMapper {
    /**
     * 查询敏感词
     * @return
     */
    Set<String> sensitiveWords();
}
