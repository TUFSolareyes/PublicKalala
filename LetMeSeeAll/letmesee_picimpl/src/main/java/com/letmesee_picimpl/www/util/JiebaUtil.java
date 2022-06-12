package com.letmesee_picimpl.www.util;


import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
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


    public static Set<String> jiebaAn(String text){
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
