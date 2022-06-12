package com.letmesee.www.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.letmesee.www.pojo.ResultVO;

public interface IndexService {

    /**
     * 添加文本索引
     * @param title
     * @param content
     * @param dataJsonStr
     * @return
     * @throws JsonProcessingException
     */
    ResultVO addTextIndex(String title, String content, String dataJsonStr) throws JsonProcessingException;

    /**
     * 添加图片索引
     * @param url
     * @param content
     * @return
     */
    ResultVO addPicIndex(String url,String content);

    /**
     * 删除文本
     * @param textId
     * @return
     */
    ResultVO delText(String textId);

    /**
     * 获取倒排索引信息
     * @return
     */
    ResultVO getInvertedIndexInfo();
}
