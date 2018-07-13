package com.yxr.imtalk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    public static String getNow() {
        return sdf.format(new Date());
    }

}
