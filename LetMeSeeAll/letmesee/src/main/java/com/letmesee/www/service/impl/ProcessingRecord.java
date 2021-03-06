package com.letmesee.www.service.impl;

import com.letmesee.www.pojo.RelatedSet;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.SearchPreLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProcessingRecord {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 获取相关搜索
     * @param text
     * @return
     */
    public ResultVO getRelatedSearch(String text){
        Criteria criteria = Criteria.where("_id").is(text);
        Query query = new Query(criteria);
        SearchPreLog one = mongoTemplate.findOne(query, SearchPreLog.class, "log_1");
        if(one==null){
            return new ResultVO(ResultVO.EXCEPTION,"empty",new ArrayList<>());
        }
        Criteria criteria2 = Criteria.where("_id").is(one.getCsid());
        Query query2 = new Query(criteria2);
        RelatedSet rs = mongoTemplate.findOne(query2,RelatedSet.class,"cs_1");
        if(rs==null){
            return new ResultVO(ResultVO.EXCEPTION,"empty",new ArrayList<>());
        }
        rs.getRws().remove(text);
        if(rs.getRws().size()>6){
            return new ResultVO(ResultVO.OK,"ok",rs.getRws().subList(0,7));
        }
        return new ResultVO(ResultVO.OK,"ok",rs.getRws());
    }
}
