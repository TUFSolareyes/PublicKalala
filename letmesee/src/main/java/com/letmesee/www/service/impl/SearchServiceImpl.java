package com.letmesee.www.service.impl;

import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.*;
import com.letmesee.www.service.SearchService;
import com.letmesee.www.util.JiebaUtil;
import com.letmesee.www.util.SomeUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {


    Map<String, List<Long>> inverted = LetMeSeeApplication.INVERTED;

    Map<Long,Map<String, WordPacking>> forward = LetMeSeeApplication.FORWARD;

    Map<Long, TextInfoPacking> textinfo = LetMeSeeApplication.TEXTINFO;

    @Override
    public ResultVO searchTextByText(String text, int pageCount, int limitCount, String[] fwords) {

        long start = System.currentTimeMillis();

        //分词处理
        Set<String> set = JiebaUtil.jiebaAn2(text);
        Set<String> abled = new HashSet<>();


        //查询倒排索引
        ArrayList<List<Long>> invertedLists = new ArrayList<>();
        for(String word:set){
            if(!inverted.containsKey(word)){
                continue;
            }
            abled.add(word);
            invertedLists.add(inverted.get(word));
        }
        if(invertedLists.size()==0){
            long end = System.currentTimeMillis();
            return new ResultVO(4000,(end-start)+","+0,new ArrayList<>());
        }

        //求交集数组
        Set<Long> invertedSet = getIntersection(invertedLists);
        if(invertedSet.isEmpty()){
            invertedSet = getIncompleteMatchSet(invertedLists);
        }

        if(invertedSet.isEmpty()){
            return new ResultVO(4000,"0,0",new ArrayList<>());
        }

        //如果有过滤词，则需要求一个差集
        if(fwords!=null&&fwords.length>0){
            ArrayList<List<Long>> farr = new ArrayList<>();
            //遍历过滤词数组
            for(int i=0;i<fwords.length;i++){
                String filterWord = fwords[i];

                //通过倒排索引查询过滤词索引链
                if(!inverted.containsKey(filterWord)){
                    continue;
                }
                farr.add(inverted.get(filterWord));
            }

            Set<Long> fset = getUnion(farr);
            invertedSet = getDifference(invertedSet,fset);
        }


        //用交集数组去正排索引中查询分数并排序
        List<TextScorePacking> cur = getScores(invertedSet,abled);
        ArrayList<TextInfoPacking> ans = new ArrayList<>();
        int iEnd = (pageCount-1)*limitCount+limitCount-1;
        for(int i=(pageCount-1)*limitCount;i<=iEnd&&i<cur.size()&&i>=0;i++){
            ans.add(textinfo.get(cur.get(i).getTid()));
        }
        int maxPageCount = cur.size()%limitCount==0? cur.size()/limitCount : cur.size()/limitCount+1;
        long end = System.currentTimeMillis();
        return new ResultVO(4000,(end-start)+","+ cur.size()+","+maxPageCount,ans);
    }




    /**
     * 获取非完全匹配的文档id集合
     * @return
     */
    private Set<Long> getIncompleteMatchSet(List<List<Long>> arr){
        int min = Integer.MAX_VALUE,flag = 0;
        for(int i=0;i< arr.size();i++){
            if(min>arr.get(i).size()){
                min = arr.get(i).size();
                flag = i;
            }
        }
        Set<Long> ans = new HashSet<>();
        for(int i=0;i<arr.get(flag).size();i++){
            ans.add(arr.get(flag).get(i));
        }
        return ans;
    }



    /**
     * 求交集
     * @param listList
     * @return
     */
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


    /**
     * 求并集
     * @param listList
     * @return
     */
    private Set<Long> getUnion(List<List<Long>> listList){
        Set<Long> ans = new HashSet<>();
        for(int i=0;i<listList.size();i++){
            for(int k=0;k<listList.get(i).size();k++){
                ans.add(listList.get(i).get(k));
            }
        }
        return ans;
    }


    /**
     * 求差集
     * @param tar
     * @param cur
     * @return
     */
    private Set<Long> getDifference(Set<Long> tar,Set<Long> cur){
        Set<Long> ans = new HashSet<>();
        for(long temp:tar){
            if(!cur.contains(temp)){
                ans.add(temp);
            }
        }
        return ans;
    }


    /**
     * 打分 TF*IDF
     * @param set
     * @param words
     * @return
     */
    private List<TextScorePacking> getScores(Set<Long> set,Set<String> words){

        long start = System.currentTimeMillis();
        ArrayList<TextScorePacking> ans = new ArrayList<>();
        Iterator<Long> iterator = set.iterator();

        //总文本数量
        int sumCount = forward.size();

        //遍历交集集合
        while(iterator.hasNext()){
            long textId = iterator.next();
            double score = 0;
            for(String word:words){
                double idf = Math.log((double) sumCount/(inverted.get(word).size()+1));
                if(SomeUtils.isNullorEmpty(forward.get(textId),forward.get(textId).get(word))){
                   continue;
                }
                double tf = forward.get(textId).get(word).getTf();
                score += tf*idf;
            }
            TextScorePacking t = new TextScorePacking(textId,score);
            ans.add(t);
        }

        ans.sort(new Comparator<TextScorePacking>() {
            @Override
            public int compare(TextScorePacking o1, TextScorePacking o2) {
                if(o2.getScore()- o1.getScore()>0){
                    return 1;
                }else if(o2.getScore()- o1.getScore()<0){
                    return -1;
                }else{
                    return 0;
                }
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("打分耗时: "+(end-start)+"ms");
        return ans;
    }
}
