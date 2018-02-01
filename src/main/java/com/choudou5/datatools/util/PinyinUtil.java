package com.choudou5.datatools.util;

import cn.hutool.core.util.StrUtil;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * 拼音工具类
 */
public class PinyinUtil {

    public static void main(String[] args) throws Exception {
        String str = "重庆市";
        System.out.println(getPinYin(str));
        System.out.println(getShortPinYin(str));
        System.out.println(getHeadFirstLetter(str));
    }

    /**
     * 获得 拼音 (全拼)
     * @param str
     * @return
     */
    public static String getPinYin(String str){
        String pinyin = null;
        try {
            pinyin = PinyinHelper.convertToPinyinString(str, "", PinyinFormat.WITHOUT_TONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pinyin;
    }


    /**
     * 获得 短拼
     * @param str
     * @return
     */
    public static String getShortPinYin(String str){
        String shortPinYin = null;
        try {
            shortPinYin = PinyinHelper.getShortPinyin(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shortPinYin;
    }

    /**
     * 获得 首字母
     * @param str
     * @return
     */
    public static String getHeadFirstLetter(String str) {
        String shortPinYin = getShortPinYin(str);
        return StrUtil.isNotBlank(shortPinYin)?String.valueOf(shortPinYin.charAt(0)):null;
    }

}
