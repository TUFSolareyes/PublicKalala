package com.letmesee.www.service;

import com.letmesee.www.pojo.ResultVO;

public interface ProcessingRecordService {

    /**
     * 相关搜索
     * @param text
     * @return
     */
    ResultVO getRelatedSearch(String text);
}
