package com.letmesee.www.util;


import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.letmesee.www.pojo.WordPacking;
import java.util.*;

/**
 * Jieba分词器工具类
 */
public class JiebaUtil {

    public final static JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();

    private final static Set<String> cutWords = new HashSet<>();

    static {
        cutWords.add("的");
    }

    public static Map<String,WordPacking> jiebaWebText(String title, String content){

        //分词
        List<SegToken> titleX = jiebaSegmenter.process(title, JiebaSegmenter.SegMode.INDEX);
        List<SegToken> contentX = jiebaSegmenter.process(content, JiebaSegmenter.SegMode.INDEX);

        //总词数
        int sum = titleX.size()+contentX.size();

        //收集哈希表
        HashMap<String, WordPacking> map = new HashMap<>();

        //对标题进行分词
        for(int i=0;i<titleX.size();i++){
            SegToken s = titleX.get(i);
            String keyword = s.word.replaceAll("\\s*","");
            if(keyword.length()==0){
                continue;
            }
            int pos = s.startOffset;
            if(map.containsKey(keyword)){
                WordPacking w = map.get(keyword);
                w.setTt(w.getTt()+1);
                w.getP().add(pos);
                //计算tf
                int tt = w.getTt();
                double tf = (double) 1+Math.log(tt*5)/sum;
                w.setTf(tf);
            }else{
                List<Integer> p = new ArrayList<>();
                p.add(pos);
                WordPacking w = new WordPacking(keyword,1,0,(double) 1+Math.log(5)/sum,p);
                map.put(keyword,w);
            }
        }


        //对内容进行分词
        for(int i=0;i<contentX.size();i++){
            SegToken s = contentX.get(i);
            String keyword = s.word.replaceAll("\\s*","");
            if(keyword.length()==0){
                continue;
            }
            int pos = s.startOffset;
            if(map.containsKey(keyword)){
                WordPacking w = map.get(keyword);
                w.setCt(w.getCt()+1);
                w.getP().add(s.startOffset);
                //计算tf
                int tt = w.getTt();
                int ct = w.getCt();
                double tf = (double) 1+Math.log(tt*5+ct)/sum;
                w.setTf(tf);
            }else{
                List<Integer> p = new ArrayList<>();
                p.add(pos);
                WordPacking w = new WordPacking(keyword,0,1,(double) 1+Math.log(1)/sum,p);
                map.put(keyword,w);
            }
        }

        return map;
    }





    public static Set<String> jiebaAn2(String text){
        List<SegToken> list = jiebaSegmenter.process(text, JiebaSegmenter.SegMode.INDEX);
        Set<String> set = new HashSet<>();
        for(int i=0;i< list.size();i++){
            String word = list.get(i).word.replaceAll("\\s*","");
            if(word.length()==0){
                continue;
            }
            if(list.size()>1&&cutWords.contains(word)){
                continue;
            }
            set.add(word);
        }
        return set;
    }



}
