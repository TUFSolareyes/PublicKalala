package com.letmesee_picimpl.www.service;


import com.letmesee_picimpl.LetmeseePicimplApplication;
import com.letmesee_picimpl.www.pojo.PicPacking;
import com.letmesee_picimpl.www.pojo.ResultVO;
import com.letmesee_picimpl.www.util.JiebaUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl {

    private Map<String, List<Long>> INVCACHE = LetmeseePicimplApplication.INVCACHE;

    private Map<Long, PicPacking> FORCACHE = LetmeseePicimplApplication.FORCACHE;

    public ResultVO searchPic(String text,int pageCount,int limitCount){

        long start = System.currentTimeMillis();

        //分词
        Set<String> words = JiebaUtil.jiebaAn(text);

        //去除无用关键词
        Set<String> abled = new HashSet<>();
        for(String word:words){
            if(INVCACHE.containsKey(word)){
                abled.add(word);
            }
        }
        words = null;

        //取出关键词对应的倒排索引链
        ArrayList<List<Long>> cur = new ArrayList<>();
        for(String word:abled){
            List<Long> list = INVCACHE.get(word);
            cur.add(list);
        }


        //求交集
        Set<Long> set = getIntersection(cur);
        if(set.isEmpty()){
            //没有交集，就求并集
            set = getUnion(cur);
        }

        ArrayList<Long> temp = new ArrayList<>();
        ArrayList<PicPacking> ans = new ArrayList<>();
        for(Long pid:set){
            temp.add(pid);
        }

        int enda = (pageCount-1)*limitCount+limitCount;
        for(int i=(pageCount-1)*limitCount;i<enda&&i<temp.size();i++){
            ans.add(FORCACHE.get(temp.get(i)));
        }

        long end = System.currentTimeMillis();
        int maxPageCount = temp.size()%limitCount==0? temp.size()/limitCount : temp.size()/limitCount+1;
        return new ResultVO(4000,""+(end-start)+","+temp.size()+","+maxPageCount,ans);
    }


    private Set<Long> getIntersection(List<List<Long>> listList){
        long start = System.currentTimeMillis();
        Set<Long> ans = new HashSet<>();
        if(listList.size()<=0){
            return ans;
        }
        Set<Long> set = new HashSet<>();
        for(int i=0;i<listList.get(0).size();i++){
            set.add(listList.get(0).get(i));
        }
        if(listList.size()==1){
            long end = System.currentTimeMillis();
            System.out.println("哈希求交集耗时: "+(end-start)+"ms");
            return set;
        }

        for(int i=1;i<listList.size();i++){
            for(int k=0;k<listList.get(i).size();k++){
                if(set.contains(listList.get(i).get(k))){
                    ans.add(listList.get(i).get(k));
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("哈希求交集耗时: "+(end-start)+"ms");
        return ans;

    }


    private Set<Long> getUnion(List<List<Long>> listList){
        Set<Long> ans = new HashSet<>();
        for(int i=0;i<listList.size();i++){
            for(int k=0;k<listList.get(i).size();k++){
                ans.add(listList.get(i).get(k));
            }
        }
        return ans;
    }



}
