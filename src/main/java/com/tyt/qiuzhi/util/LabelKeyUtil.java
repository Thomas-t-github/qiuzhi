package com.tyt.qiuzhi.util;

public class LabelKeyUtil {

    private static String SPLIT = ",";

    public static String getLabel(String label,String university,String industry){
        return label+SPLIT+university+SPLIT+industry;
    }

}
