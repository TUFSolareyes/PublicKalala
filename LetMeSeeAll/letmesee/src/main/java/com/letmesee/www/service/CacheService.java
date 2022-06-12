package com.letmesee.www.service;

import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.pojo.TextPacking;

import java.util.List;

public interface CacheService {

    /**
     * 添加搜索结果缓存
     * @param word
     * @param pageCount
     * @param maxPageCount
     * @param maxCount
     * @param data
     * @return
     */
    ResultVO putSearchResultCache(String word, int pageCount, int maxPageCount, int maxCount, List<TextInfoPacking> data);

    /**
     * 添加文本缓存
     * @param textId
     * @param textPacking
     * @return
     */
    ResultVO putTextCache(long textId, TextPacking textPacking);

    /**
     * 获取搜索结果缓存结果
     * @param word
     * @param pageCount
     * @return
     */
    ResultVO selectSearchResultCache(String word,int pageCount);

    /**
     * 获取文本缓存结果
     * @param textId
     * @return
     */
    ResultVO selectTextCache(String textId);
}
