package com.modcreater.tmauth.config.filter;

import com.modcreater.tmdao.mapper.SensitiveWordsMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description:
 *              敏感词拦截器
 * @Author: AJun
 * @Date: 2019/8/8 10:30
 */

@Component
public class SensitiveWordsFilter {

    private static Map<Object,Object> sensitiveWordsMap;

    private static final String END_FLAG="end";
    /**
     * 初始化敏感词
     * @param sensitiveWords
     */
    public void initSensitiveWordsMap(Set<String> sensitiveWords){
        if(sensitiveWords==null||sensitiveWords.isEmpty()){
            throw new IllegalArgumentException("Senditive words must not be empty!");
        }
        sensitiveWordsMap=new HashMap<>(sensitiveWords.size());
        String currentWord;
        Map<Object,Object> currentMap;
        Map<Object,Object> subMap;
        Iterator<String> iterator = sensitiveWords.iterator();
        while (iterator.hasNext()){
            currentWord=iterator.next();
            //敏感词长度必须大于等于2
            if(currentWord==null||currentWord.trim().length()<2){
                continue;
            }
            currentMap=sensitiveWordsMap;
            for(int i=0;i<currentWord.length();i++){
                char c=currentWord.charAt(i);
                subMap=(Map<Object, Object>) currentMap.get(c);
                if(subMap==null){
                    subMap=new HashMap<>();
                    currentMap.put(c,subMap);
                    currentMap=subMap;
                }else {
                    currentMap= subMap;
                }
                if(i==currentWord.length()-1){
                    //如果是最后一个字符，则put一个结束标志，这里只需要保存key就行了，value为null可以节省空间。
                    //如果不是最后一个字符，则不需要存这个结束标志，同样也是为了节省空间。
                    currentMap.put(END_FLAG,null);
                }
            }
        }
    }

    /**
     * 定义匹配规则
     */
    public enum MatchType {
        //匹配规则
        MIN_MATCH("最小匹配规则"),MAX_MATCH("最大匹配规则");

        String desc;

        MatchType(String desc) {
            this.desc = desc;
        }
    }

    /**
     *获取敏感词
     * @param text
     * @param matchType
     * @return
     */
    public Set<String> getSensitiveWords(String text,MatchType matchType){
        if(text==null||text.trim().length()==0){
            throw new IllegalArgumentException("The input text must not be empty.");
        }
        //去无效字符
        text=trimInvalid(text);
        Set<String> sensitiveWords=new HashSet<>();
        for(int i=0;i<text.length();i++){
            System.out.println("===>"+text.charAt(i));
            int sensitiveWordLength = getSensitiveWordLength(text, i, matchType);
            if(sensitiveWordLength>0){
                String sensitiveWord = text.substring(i, i + sensitiveWordLength);
                sensitiveWords.add(sensitiveWord);
                if(matchType==MatchType.MIN_MATCH){
                    break;
                }
                i=i+sensitiveWordLength-1;
            }
        }
        return sensitiveWords;
    }

    /**
     * 获取敏感词长度
     * @param text
     * @param startIndex
     * @param matchType
     * @return
     */
    private int getSensitiveWordLength(String text,int startIndex,MatchType matchType){
        if(text==null||text.trim().length()==0){
            throw new IllegalArgumentException("The input text must not be empty.");
        }
        char currentChar;
        Map<Object,Object> currentMap=sensitiveWordsMap;
        int wordLength=0;
        boolean endFlag=false;
        for(int i=startIndex;i<text.length();i++){
            currentChar=text.charAt(i);
            Map<Object,Object> subMap=(Map<Object,Object>) currentMap.get(currentChar);
            if(subMap==null){
                break;
            }else {
                wordLength++;
                if(subMap.containsKey(END_FLAG)){
                    endFlag=true;
                    if(matchType==MatchType.MIN_MATCH){
                        break;
                    }else {
                        currentMap=subMap;
                    }
                }else {
                    currentMap=subMap;
                }
            }
        }
        if(!endFlag){
            wordLength=0;
        }
        return wordLength;
    }

    /**
     * 去除无效字符
     * @param text
     * @return
     */
    private String trimInvalid(String text){
        List<Character> invalid=new ArrayList<>();
        invalid.add(' ');
        invalid.add('*');
        invalid.add('#');
        invalid.add('/');
        String newText="";
        for (int i = 0; i < text.length(); i++) {
            char c=text.charAt(i);
            if (!invalid.contains(c)){
                newText+=c;
            }
        }
        return newText;
    }

    /*public static void main(String[] args) {
        SensitiveWordsFilter sensitiveWordsFilter=new SensitiveWordsFilter();
        String a="加油你是最棒的！傻    逼";

        System.out.println(sensitiveWordsFilter.trimInvalid(a));
        *//*Set<String> set=new HashSet<>();
        set.add("fuck");
        set.add("操你妈");
        set.add("傻逼");
        set.add("脑残");
        set.add("狗逼");
        sensitiveWordsFilter.initSensitiveWordsMap(set);
        String text="小明是个脑    残   f  uck哈哈哈";
        Set<String> sensitiveWordsSet=sensitiveWordsFilter.getSensitiveWords(text,MatchType.MAX_MATCH);
        System.out.println(sensitiveWordsSet);*//*
    }*/
}
