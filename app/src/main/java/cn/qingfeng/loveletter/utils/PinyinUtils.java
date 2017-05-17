package cn.qingfeng.loveletter.utils;


import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * fileName    : cn.qingfeng.aixin.utils.PinyinUtils.java
 * author      : 李青峰
 * date        : 2016-07-23 15:08
 * description :
 * version     : V1.0
 */
public class PinyinUtils {
    public static String getPinyin(String name){
        return PinyinHelper.convertToPinyinString(name,"", PinyinFormat.WITHOUT_TONE);
    }
}
