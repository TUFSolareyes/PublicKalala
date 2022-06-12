package com.letmesee_picimpl.www.service;

import com.letmesee_picimpl.LetmeseePicimplApplication;
import com.letmesee_picimpl.www.pojo.PicPacking;
import com.letmesee_picimpl.www.pojo.ResultVO;
import com.letmesee_picimpl.www.util.Jackson;
import com.letmesee_picimpl.www.util.JiebaUtil;
import com.letmesee_picimpl.www.util.SomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Service
public class IndexServiceImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Jackson jackson;

    private Map<String, List<String>> inv = LetmeseePicimplApplication.INVCACHE;

    private Map<String, PicPacking> forc = LetmeseePicimplApplication.FORCACHE;

    @Autowired
    private SomeUtils someUtils;

    public ResultVO addIndex(String url,String content){

        String fileName = null;
        String savePath = null;
        try {
            savePath = SomeUtils.getValueFromProFile("savePath","application.properties");
            fileName = downloadPicFromUrl(url,savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (SomeUtils.isNullorEmpty(fileName)){
            return new ResultVO(4002,"图片下载失败",null);
        }

        String saveUrl = "img/"+fileName;

        //对描述信息进行分词
        Set<String> set = JiebaUtil.jiebaAn(content);

        //生成唯一id
        String picId = String.valueOf(someUtils.getNextId());

        PicPacking picPacking = new PicPacking(picId,saveUrl,content);

        Iterator<String> iterator = set.iterator();

        //添加倒排索引到内存中
        while(iterator.hasNext()){
            String keyword = iterator.next();
            if(!inv.containsKey(keyword)){
                synchronized (this){
                    if(!inv.containsKey(keyword)){
                        List<String> list = Collections.synchronizedList(new LinkedList<>());
                        list.add(picId);
                        inv.put(keyword,list);
                    }else{
                        inv.get(keyword).add(picId);
                    }
                }
            }else{
                inv.get(keyword).add(picId);
            }
        }

        //将正排索引添加到内存中
        forc.put(picId,picPacking);

        String jsonStr = jackson.getJsonStr(picPacking);
        stringRedisTemplate.opsForList().leftPush("picForc:"+picId,jsonStr);

        Iterator<String> iterator2 = set.iterator();
        while(iterator2.hasNext()){
            String word = iterator2.next();
            stringRedisTemplate.opsForList().leftPush("picInv:"+word,picId);
        }

        return new ResultVO(4000,"ok",null);
    }


    /**
     * 通过url下载图片
     * @param urlStr
     * @param savePath
     * @return
     */
    private String downloadPicFromUrl(String urlStr,String savePath) {
        if(SomeUtils.isNullorEmpty(urlStr,savePath)){
            return null;
        }
        URL url = null;
        URLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            urlConnection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String type = urlConnection.getContentType();
        type = type.substring(0,type.indexOf("/"));
        if(!"image".equalsIgnoreCase(type)){
            return null;
        }

        String fileName = SomeUtils.getRandomStr()+".jpg";
        InputStream is = null;
        FileOutputStream fos = null;
        try{
            is = urlConnection.getInputStream();
            byte[] buff = new byte[1024];

            File file = new File(savePath+File.separator+fileName);
            fos = new FileOutputStream(file);

            int len = -1;
            while((len = is.read(buff))!=-1){
                fos.write(buff,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

         return fileName;
    }

}
