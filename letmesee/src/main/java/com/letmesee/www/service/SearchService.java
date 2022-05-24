package com.letmesee.www.service;

import com.letmesee.www.pojo.ResultVO;

public interface SearchService {

    /**
     * 以文本搜索文本
     * @param text
     * @param pageCount
     * @param limitCount
     * @param fwords
     * @return
     */
    public ResultVO searchTextByText(String text, int pageCount, int limitCount, String[] fwords);


}
