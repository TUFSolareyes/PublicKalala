package com.letmesee;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.www.SpringUtils;
import com.letmesee.www.pojo.*;
import com.letmesee.www.util.SomeUtils;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LetMeSeeApplication {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public final static Map<String, List<Long>> INVERTED = new ConcurrentHashMap<>();

    public final static Map<Long,Map<String, WordPacking>> FORWARD = new ConcurrentHashMap<>();

    public final static Map<Long,TextInfoPacking> TEXTINFO = new ConcurrentHashMap<>();

    public static ThreadPoolExecutor tPool = new ThreadPoolExecutor(10,20,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void main(String[] args) {
        SpringApplication.run(LetMeSeeApplication.class, args);
        System.out.println("程序正在启动中......");

        Thread ininT = new Thread((Runnable) SpringUtils.getBean("init"));
        ininT.start();

        //索引持久化线程
        Thread t = new Thread((Runnable) SpringUtils.getBean("persistence"));
        t.setDaemon(true);
        t.start();

        //日志持久化线程
        Thread t2 = new Thread((Runnable) SpringUtils.getBean("logPersistence"));
        t2.setDaemon(true);
        t2.start();

        System.out.println("=====================================");
        System.out.println("持久化线程启动完成");
        System.out.println("=====================================");
        System.out.println("程序启动完成!!");

    }

}


/**
 * 日志持久化
 */
@Component
class LogPersistence implements Runnable{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000*20);
                System.out.println("开始日志持久化...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Set<String> keys = stringRedisTemplate.keys("log:".concat("*"));
            for(String key:keys){
                long count = stringRedisTemplate.opsForList().size(key);
                for(int i=0;i<count;i++){
                    String jsonStr = stringRedisTemplate.opsForList().rightPop(key);
                    JsonIterator parse = JsonIterator.parse(jsonStr);
                    try {
                        Any any = parse.readAny();
                        SearchLog sl = any.as(SearchLog.class);
                        Criteria criteria = Criteria.where("_id").is(sl.get_id());
                        Query query = new Query(criteria);
                        SearchLog temp = mongoTemplate.findOne(query, SearchLog.class, "log_1");
                        if(temp!=null){
                            continue;
                        }
                        //将缓存中的记录中的tids添加到set用于后续求交集
                        Set<Long> cur = new HashSet<>();
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
                            SearchLog sl2 = any2.as(SearchLog.class);

                            //计算交集
                            ArrayList<Long> arr = sl2.getTids();
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
                                    mongoTemplate.updateFirst(query3,new Update().push("rws",sl.get_id()),"cs_1");
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
                    }catch (Exception e){
                    }
                }
            }
            System.out.println("日志持久化结束...");
        }
    }
}



/**
 * 持久化计划类
 */
@Component
@SuppressWarnings("all")
class Persistence implements Runnable{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000*20);
                System.out.println("开始持久化......");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CountDownLatch countDownLatch = new CountDownLatch(3);

            LetMeSeeApplication.tPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("正在持久化倒排索引...");
                    //从redis中取出前缀为w:的key
                    Set<String> set = stringRedisTemplate.keys("w:".concat("*"));

                    //遍历key
                    for(String word:set){
                        if(word==null||word.length()==0){
                            continue;
                        }

                        //取出key对应的list中当前元素的个数
                        long count = stringRedisTemplate.opsForList().size(word);
                        if(count==0){
                            continue;
                        }

                        //用于存储弹出的元素
                        List<Long> list = Collections.synchronizedList(new LinkedList<>());
                        //弹出list中的元素
                        for(int i=0;i<count;i++){
                            //弹出list中的元素（textId）
                            String idStr = stringRedisTemplate.opsForList().rightPop(word);
                            Long id = Long.parseLong(idStr);
                            list.add(id);
                        }

                        //将信息封装成实体类
                        String keyword = word.substring(word.indexOf(":")+1);
                        InvertedPacking inv = new InvertedPacking(keyword,list);
                        try{
                            mongoTemplate.insert(inv,"inv_1");
                        }catch (Exception e){
                            Criteria criteria = Criteria.where("_id").is(keyword);
                            Query query = new Query(criteria);
                            for(int i=0;i<list.size();i++){
                                mongoTemplate.updateFirst(query,new Update().push("tids",list.get(i)),"inv_1");
                            }
                        }
                    }
                    countDownLatch.countDown();
                }
            });

            LetMeSeeApplication.tPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("正在持久化正排索引...");
                    //取出前缀为tid的key
                    Set<String> set = stringRedisTemplate.keys("tid:".concat("*"));

                    //遍历key
                    for(String idStr:set){
                        JsonIterator parse = JsonIterator.parse(stringRedisTemplate.opsForValue().get(idStr));
                        try {
                            Any any = parse.readAny();
                            Map<String,WordPacking> as = any.as(Map.class);
                            long textId = Long.parseLong(idStr.substring(idStr.indexOf(":")+1));
                            ForwardPacking f = new ForwardPacking(textId,as);
                            mongoTemplate.insert(f,"for_1");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stringRedisTemplate.delete(idStr);
                    }
                    countDownLatch.countDown();
                }
            });

            LetMeSeeApplication.tPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("正在持久化文本简述信息...");
                    Set<String> set = stringRedisTemplate.keys("fo:".concat("*"));

                    //遍历key
                    for(String idStr:set){
                        String jsonStr = stringRedisTemplate.opsForValue().get(idStr);
                        Long textId = Long.parseLong(idStr.substring(idStr.indexOf(":")+1));
                        JsonIterator parse = JsonIterator.parse(jsonStr);
                        try {
                            Any any = parse.readAny();
                            TextInfoPacking textInfoPacking = any.as(TextInfoPacking.class);
                            textInfoPacking.set_id(textId);
                            mongoTemplate.insert(textInfoPacking,"fo_1");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stringRedisTemplate.delete(idStr);
                    }
                    countDownLatch.countDown();
                }
            });

            try {
                countDownLatch.await();
                System.out.println("持久化完成@@@@");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


/**
 * 初始化任务类
 */
@Component
@SuppressWarnings("all")
class Init implements Runnable{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run() {

        System.out.println("索引初始化开始...");

        CountDownLatch countDownLatch = new CountDownLatch(3);

        LetMeSeeApplication.tPool.execute(new Runnable() {
            @Override
            public void run() {
                //载入倒排索引
                MongoCursor<Document> inv_1 = mongoTemplate.getCollection("inv_1").find().noCursorTimeout(true).batchSize(2000).cursor();
                while(inv_1.hasNext()){
                    Document document = inv_1.next();
                    String jsonStr = document.toJson();
                    JsonIterator parse = JsonIterator.parse(jsonStr);
                    try {
                        Any any = parse.readAny();
                        InvertedPacking invertedPacking = any.as(InvertedPacking.class);
                        LetMeSeeApplication.INVERTED.put(invertedPacking.get_id(),invertedPacking.getTids());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }
        });


        LetMeSeeApplication.tPool.execute(new Runnable() {
            @Override
            public void run() {
                //载入正排索引
                MongoCursor<Document> for_1 = mongoTemplate.getCollection("for_1").find().noCursorTimeout(true).batchSize(2000).cursor();
                while(for_1.hasNext()){
                    Document document = for_1.next();
                    String jsonStr = document.toJson();
                    JsonIterator parse = JsonIterator.parse(jsonStr);
                    try {
                        Any any = parse.readAny();
                        ForwardPacking forwardPacking = any.as(ForwardPacking.class);
                        LetMeSeeApplication.FORWARD.put(forwardPacking.get_id(),forwardPacking.getMap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }
        });


        LetMeSeeApplication.tPool.execute(new Runnable() {
            @Override
            public void run() {
                MongoCursor<Document> fo_1 = mongoTemplate.getCollection("fo_1").find().noCursorTimeout(true).batchSize(2000).cursor();
                while(fo_1.hasNext()){
                    Document document = fo_1.next();
                    String jsonStr = document.toJson();
                    JsonIterator parse = JsonIterator.parse(jsonStr);
                    try {
                        Any any = parse.readAny();
                        TextInfoPacking textInfoPacking = any.as(TextInfoPacking.class);
                        LetMeSeeApplication.TEXTINFO.put(textInfoPacking.get_id(),textInfoPacking);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
            System.out.println("索引加载完成...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("当前倒排索引链数量: "+ LetMeSeeApplication.INVERTED.size());
        System.out.println("当前正排索引链数量: "+ LetMeSeeApplication.FORWARD.size());
        System.out.println("当前文本简介信息数量: "+ LetMeSeeApplication.TEXTINFO.size());
    }
}
