package com.letmesee.www.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;

@Component
public class SomeUtils {

    private static final long BEGIN = 1645555555L;

    private static String STR = "1234abcdefghijklmnopqrstuvwxyz657890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static Random random = new Random();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public long getNextId(){

        //生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSec = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSec-BEGIN;

        //生成序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment("icr "+date);

        return timestamp << 32 | count;
    }


    public static String getSummary(String content){
        if(content.length()>100){
            return content.substring(0,60);
        }else{
            return content;
        }
    }


    public static String getUrlFromProFile(String key) throws IOException {
        Properties properties = new Properties();
        Resource resource = new ClassPathResource("some/key.properties");
        properties.load(resource.getInputStream());
        String url= (String) properties.get(key);
        return url;
    }


    public static String getRandomStr(){
        int in1 = random.nextInt(62);
        int in2 = random.nextInt(62);
        int in3 = random.nextInt(62);
        int in4 = random.nextInt(62);
        int randomNum = random.nextInt(90000);
        StringBuilder str = new StringBuilder();
        str.append(STR.charAt(in1)).append(STR.charAt(in2)).append(STR.charAt(in3)).append(STR.charAt(in4));
        str.append(randomNum);
        return str.toString();
    }


    public static boolean isNullorEmpty(Object... objects){
        for(Object obj:objects){
            if(obj instanceof String){
                String temp = (String) obj;
                if(temp==null||temp.length()==0){
                    return true;
                }
            }else{
                if(obj==null){
                    return true;
                }
            }
        }
        return false;
    }
}
