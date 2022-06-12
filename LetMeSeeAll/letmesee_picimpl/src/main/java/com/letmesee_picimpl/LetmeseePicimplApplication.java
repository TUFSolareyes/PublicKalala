package com.letmesee_picimpl;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee_picimpl.www.SpringUtils;
import com.letmesee_picimpl.www.pojo.ForPacking;
import com.letmesee_picimpl.www.pojo.InvPacking;
import com.letmesee_picimpl.www.pojo.PicPacking;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@EnableCaching // 启用缓存功能
@EnableScheduling // 开启定时任务功能
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LetmeseePicimplApplication {

    public static final Map<String, List<String>> INVCACHE = new ConcurrentHashMap<String, List<String>>();

    public static final Map<String, PicPacking> FORCACHE = new ConcurrentHashMap<String, PicPacking>();

    public static ThreadPoolExecutor tPool = new ThreadPoolExecutor(3,10,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());


    public static void main(String[] args) {
        SpringApplication.run(LetmeseePicimplApplication.class, args);
        System.out.println("开始初始化...");
        Thread t = new Thread((Runnable) SpringUtils.getBean("init"));
        t.start();
    }


}


@Component
class Init implements Runnable{

    @Autowired
    private MongoTemplate mongoTemplate;

    private Map<String, List<String>> INV = LetmeseePicimplApplication.INVCACHE;

    private Map<String, PicPacking> FOR = LetmeseePicimplApplication.FORCACHE;

    @Override
    public void run() {

        System.out.println("开始加载索引信息...");
        CountDownLatch countDownLatch = new CountDownLatch(2);

        //初始化倒排索引
        LetmeseePicimplApplication.tPool.execute(() -> {
            MongoCursor<Document> run = mongoTemplate.getCollection("picInv_1").find().noCursorTimeout(true).batchSize(2000).cursor();
            while(run.hasNext()){
                Document document = run.next();
                String jsonStr = document.toJson();
                JsonIterator parse = JsonIterator.parse(jsonStr);
                try {
                    Any any = parse.readAny();
                    InvPacking as = any.as(InvPacking.class);
                    INV.put(as.get_id(),as.getTids());
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){

                }
            }
            countDownLatch.countDown();
        });


        //初始化正排索引
        LetmeseePicimplApplication.tPool.execute(() -> {
            MongoCursor<Document> run = mongoTemplate.getCollection("pic_1").find().noCursorTimeout(true).batchSize(2000).cursor();
            while(run.hasNext()){
                Document document = run.next();
                String jsonStr = document.toJson();
                JsonIterator parse = JsonIterator.parse(jsonStr);
                try {
                    Any any = parse.readAny();
                    ForPacking as = any.as(ForPacking.class);
                    PicPacking p = new PicPacking(as.get_id(),as.getUrl(),as.getContent());
                    FOR.put(as.get_id(),p);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){

                }
            }
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
            System.out.println("索引加载完成...");
            System.out.println("当前倒排索引链数量:"+INV.size());
            System.out.println("当前正排索引数量:"+FOR.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


@Component
class Persistence{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Scheduled(cron = "*/10 * * * * ?")
    public void run() {

        System.out.println("开始持久化");

        CountDownLatch countDownLatch = new CountDownLatch(2);

        LetmeseePicimplApplication.tPool.execute(() -> {
            //获取倒排索引key
            Set<String> set = stringRedisTemplate.keys("picInv:".concat("*"));

            for(String key:set){
                Long size = stringRedisTemplate.opsForList().size(key);
                if(size==0){
                    continue;
                }

                List<String> arr = new ArrayList<>();
                for(int i=0;i<size;i++){
                    String value = stringRedisTemplate.opsForList().rightPop(key);
                    arr.add(value);
                }

                String invId = key.substring(key.indexOf(":")+1);
                InvPacking invPacking = new InvPacking(invId,arr);
                try{
                    mongoTemplate.insert(invPacking,"picInv_1");
                }catch (Exception e){
                    Criteria criteria = Criteria.where("_id").is(invId);
                    Query query = new Query(criteria);
                    for(int k=0;k<arr.size();k++){
                        mongoTemplate.updateFirst(query,new Update().push("tids",arr.get(k)),"picInv_1");
                    }
                }
            }
            countDownLatch.countDown();
        });

        LetmeseePicimplApplication.tPool.execute(() -> {
            Set<String> set = stringRedisTemplate.keys("picForc".concat("*"));

            for (String key:set){
                Long size = stringRedisTemplate.opsForList().size(key);
                if(size==0){
                    continue;
                }

                List<PicPacking> arr = new ArrayList<>();

                for(int i=0;i<size;i++){
                    String jsonStr = stringRedisTemplate.opsForList().rightPop(key);
                    JsonIterator jsonIterator = JsonIterator.parse(jsonStr);
                    try {
                        Any any = jsonIterator.readAny();
                        PicPacking as = any.as(PicPacking.class);
                        arr.add(as);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for(int k=0;k<arr.size();k++){
                    try{
                        mongoTemplate.insert(arr.get(k),"pic_1");
                    }catch (Exception e){
                    }
                }
            }
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("持久化结束");
    }
}
