package com.liujan.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liujan on 5/10/16.
 */
public class DateUtil {
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static String format(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            return sdf.format(date);
        }
        catch (Exception e) {
            logger.error("date format error!", e);
            return null;
        }
    }
    public static Date parse(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.parse(dateStr);
        }
        catch (Exception e) {
            logger.error("date parse error!", e);
            return null;
        }
    }
}
