package com.y54.bdao;

import java.util.Calendar;

public class DateUtils {
    public static String FormatDate(String ss){
        int y,M,d,h,m,s;
        String str =ss.substring(0,4)+"/"+ss.substring(5,7)+"/"+ss.substring(8,10)+" "+ss.substring(13,15)+":"+ss.substring(16,18)+":"+ss.substring(19,21);
        String returnStr="";
        //2022/07/24 16:13:05
        //now
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int dh = cal.get(Calendar.HOUR_OF_DAY);
        int dm = cal.get(Calendar.MINUTE);
        int ds = cal.get(Calendar.SECOND);
        //get
        y = Integer.parseInt(ss.substring(0,4));
        M = Integer.parseInt(ss.substring(5,7));
        d = Integer.parseInt(ss.substring(8,10));
        h = Integer.parseInt(ss.substring(13,15));
        m = Integer.parseInt(ss.substring(16,18));
        s = Integer.parseInt(ss.substring(19,21));
        if(year==y){
            if((month==M) && (day == d)){
                returnStr = str.substring(11);
            }else{
                returnStr = str.substring(5,16);
            }
        }else{
            returnStr = str.substring(2,16);
        }
        return returnStr;
    }
}
