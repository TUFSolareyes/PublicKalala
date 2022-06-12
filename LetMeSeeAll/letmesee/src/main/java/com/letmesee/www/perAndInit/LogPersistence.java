package com.letmesee.www.perAndInit;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.www.pojo.RelatedSet;
import com.letmesee.www.pojo.SearchPreLog;
import com.letmesee.www.util.LogUtil;
import com.letmesee.www.util.SomeUtils;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 日志持久化
 */
@Component
public class LogPersistence{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Scheduled(cron = "*/20 * * * * ?")
    public void run() {
        LogUtil.logMessage("开始日志持久化", LogUtil.INFO);

        if(!SomeUtils.judgeStringRedisTemplateOk(stringRedisTemplate)){
            return;
        }

        Set<String> keys = stringRedisTemplate.keys("log:".concat("*"));
        if(keys==null){
            return;
        }
        for(String key:keys){
            long count = stringRedisTemplate.opsForList().size(key);
            for(int i=0;i<count;i++){
                String jsonStr = stringRedisTemplate.opsForList().rightPop(key);
                JsonIterator parse = JsonIterator.parse(jsonStr);
                try {
                    Any any = parse.readAny();
                    SearchPreLog sl = any.as(SearchPreLog.class);
                    Criteria criteria = Criteria.where("_id").is(sl.get_id());
                    Query query = new Query(criteria);
                    SearchPreLog temp = mongoTemplate.findOne(query, SearchPreLog.class, "log_1");
                    if(temp!=null){
                        if(sl.getTids().size()>0){
                            for(int p=0;p<sl.getTids().size();p++){
                                mongoTemplate.updateFirst(query,new Update().addToSet("tids",sl.getTids().get(p)),"log_1");
                            }
                        }else{
                            continue;
                        }
                    }
                    //将缓存中的记录中的tids添加到set用于后续求交集
                    Set<String> cur = new HashSet<>();
                    for(int k=0;k<sl.getTids().size();k++){
                        cur.add(sl.getTids().get(k));
                    }

                    //遍历数据库中的日志
                    MongoCursor<Document> run = mongoTemplate.getCollection("log_1").find().noCursorTimeout(true).batchSize(2000).cursor();
                    boolean isOk = false;

                    while(run.hasNext()){
                        double sum = sl.getTids().size(),in = 0.0;

                        //取出日志记录
                        Document document = run.next();
                        String jsonStr2 = document.toJson();
                        JsonIterator parse2 = JsonIterator.parse(jsonStr2);
                        Any any2 = parse2.readAny();
                        SearchPreLog sl2 = any2.as(SearchPreLog.class);

                        if(sl2.get_id().equals(sl.get_id())){
                            continue;
                        }

                        //计算交集
                        ArrayList<String> arr = sl2.getTids();
                        for(int p=0;p< arr.size();p++){
                            if(cur.contains(arr.get(p))){
                                in++;
                            }else{
                                sum++;
                            }
                        }

                        //当相似度大于等0.6时，合并元素到一个集合
                        if(Math.abs(in/sum)>=0.6){
                            if("*".equals(sl2.getCsid())){
                                //说明元素之前没有在任何一个集合中
                                //创建一个集合
                                String csid = SomeUtils.getRandomStr();
                                ArrayList<String> rws = new ArrayList<>();
                                rws.add(sl.get_id());
                                rws.add(sl2.get_id());
                                RelatedSet rs = new RelatedSet(csid,rws);
                                mongoTemplate.insert(rs,"cs_1");
                                Criteria criteria2 = Criteria.where("_id").is(sl2.get_id());
                                Query query2 = new Query(criteria2);
                                mongoTemplate.updateFirst(query2,new Update().set("csid",csid),"log_1");
                                sl.setCsid(csid);
                                mongoTemplate.insert(sl,"log_1");
                            }else{
                                String csid = sl2.getCsid();
                                Criteria criteria3 = Criteria.where("_id").is(csid);
                                Query query3 = new Query(criteria3);
                                mongoTemplate.updateFirst(query3,new Update().addToSet("rws",sl2.get_id()),"cs_1");
                                sl.setCsid(csid);
                                mongoTemplate.insert(sl,"log_1");
                            }
                            isOk = true;
                            break;
                        }

                    }
                    if(!isOk){
                        mongoTemplate.insert(sl,"log_1");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){}
            }
        }
        LogUtil.logMessage("日志持久化完成", LogUtil.INFO);
    }
}