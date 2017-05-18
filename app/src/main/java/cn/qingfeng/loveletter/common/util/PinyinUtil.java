package cn.qingfeng.loveletter.common.util;


import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * fileName    : cn.qingfeng.aixin.utils.PinyinUtil.java
 * author      : 李青峰
 * date        : 2016-07-23 15:08
 * description :
 * version     : V1.0
 */
public class PinyinUtil {
    public static String getPinyin(String name){
        return PinyinHelper.convertToPinyinString(name,"", PinyinFormat.WITHOUT_TONE);
    }
}
