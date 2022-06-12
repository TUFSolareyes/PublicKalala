package com.letmesee;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.www.Interceptor.CurrentLimiting;
import com.letmesee.www.SpringUtils;
import com.letmesee.www.pojo.*;
import com.letmesee.www.util.LogUtil;
import com.letmesee.www.util.SomeUtils;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.*;

@EnableCaching // 启用缓存功能
@EnableScheduling // 开启定时任务功能
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LetMeSeeApplication {

    public final static Map<String, List<String>> INVERTED = new ConcurrentHashMap<String, List<String>>();

    public final static Map<String, Map<String, WordPacking>> FORWARD = new ConcurrentHashMap<String, Map<String, WordPacking>>();

    public final static Map<String, TextInfoPacking> TEXTINFO = new ConcurrentHashMap<String, TextInfoPacking>();

    public final static ThreadPoolExecutor T_POOL = new ThreadPoolExecutor(20,20,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void main(String[] args) {
        SpringApplication.run(LetMeSeeApplication.class, args);
        LogUtil.logMessage("程序正在启动中......",LogUtil.INFO);


        StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) SpringUtils.getBean("stringRedisTemplate");
        MongoTemplate mongoTemplate = (MongoTemplate) SpringUtils.getBean("mongoTemplate");
        if (!SomeUtils.judgeStringRedisTemplateOk(stringRedisTemplate)){
            LogUtil.logMessage("启动时redis连接失败，终止程序",LogUtil.ERROR);
            System.exit(1);
        }

        if(!SomeUtils.judgeMongoTemplateOk(mongoTemplate)){
            LogUtil.logMessage("启动时数据库连接失败，终止程序",LogUtil.ERROR);
            System.exit(1);
        }

        CurrentLimiting currentLimiting = (CurrentLimiting)SpringUtils.getBean("currentLimiting");
        currentLimiting.limitStart(currentLimiting);

        //启动初始化线程加载索引
        Thread initT = new Thread((Runnable) SpringUtils.getBean("init"));
        initT.start();
    }

}









