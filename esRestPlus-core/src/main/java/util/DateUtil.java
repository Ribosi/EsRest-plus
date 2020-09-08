package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {
    public static final String FORMAT_YMD = "yyyy-MM-dd";
    public static final String FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YMDHMSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public DateUtil() {
    }

    public static Date getDateFromString(String dateStr) {
        return getDateFromString(dateStr, (String)null);
    }

    public static Date getDateFromString(String dateStr, String pattern) {
        if (pattern == null || "".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;

        try {
            date = format.parse(dateStr);
        } catch (ParseException var5) {
            var5.printStackTrace();
            date = null;
        }

        return date;
    }

    public static Date getDateFromString(String dateStr, String pattern, Locale locale) {
        if (pattern == null || "".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);

        Date date;
        try {
            date = format.parse(dateStr);
        } catch (ParseException var6) {
            var6.printStackTrace();
            date = null;
        }

        return date;
    }

    public static Date getDateFromDate(Date defaultDate, String pattern) {
        return getDateFromString(getStrFromDate(defaultDate, pattern), pattern);
    }

    public static String getStrFromDate(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        String s = df.format(date);
        return s;
    }

    public static String getLongStrFromDate(Date date) {
        return getStrFromDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date addMonth(Date date, int month) {
        if (date == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(2, month);
            return cal.getTime();
        }
    }

    public static Date optTime(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static long distance(Date first, Date second) {
        return second.getTime() - first.getTime();
    }

    public static int distanceSec(Date first, Date second) {
        Long sec = (second.getTime() - first.getTime()) / 1000L;
        return sec.intValue();
    }

    public static int distanceMin(Date first, Date second) {
        return distanceSec(first, second) / 60;
    }

    public static int distanceHour(Date first, Date second) {
        return distanceMin(first, second) / 60;
    }

    public static int distanceDay(Date first, Date second) {
        return distanceHour(first, second) / 24;
    }

    public static int distanceMonth(Date start, Date end) {
        if (start.after(end)) {
            Date t = start;
            start = end;
            end = t;
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        Calendar temp = Calendar.getInstance();
        temp.setTime(end);
        temp.add(5, 1);
        int year = endCalendar.get(1) - startCalendar.get(1);
        int month = endCalendar.get(2) - startCalendar.get(2);
        if (startCalendar.get(5) == 1 && temp.get(5) == 1) {
            return year * 12 + month + 1;
        } else if (startCalendar.get(5) != 1 && temp.get(5) == 1) {
            return year * 12 + month;
        } else if (startCalendar.get(5) == 1 && temp.get(5) != 1) {
            return year * 12 + month;
        } else {
            return year * 12 + month - 1 < 0 ? 0 : year * 12 + month;
        }
    }

    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    public static Date getPushBackDate(int days, Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(5, days);
        date = calendar.getTime();
        return date;
    }

    public static Date getPushBackSecond(int seconds, Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(13, seconds);
        date = calendar.getTime();
        return date;
    }

    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(11, 0);
        todayStart.set(12, 0);
        todayStart.set(13, 0);
        todayStart.set(14, 0);
        return todayStart.getTime();
    }

    public static Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(11, 23);
        todayEnd.set(12, 59);
        todayEnd.set(13, 59);
        todayEnd.set(14, 999);
        return todayEnd.getTime();
    }

    public static Date getEndTime(Date date) {
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(11, 23);
        end.set(12, 59);
        end.set(13, 59);
        end.set(14, 999);
        return end.getTime();
    }

    public static boolean isInTime(Date date, int startHour, int endHour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(11);
        int minute = cal.get(12);
        int second = cal.get(13);
        int secondOfDay = (hour * 60 + minute) * 60 + second;
        int start = startHour * 60 * 60;
        int end = endHour * 60 * 60;
        return secondOfDay >= start && secondOfDay <= end;
    }

    public static Date getLastMonthFirstDay() {
        Calendar cale = Calendar.getInstance();
        cale.add(2, -1);
        cale.set(5, 1);
        return cale.getTime();
    }

    public static Date getLastMonthEndDay() {
        Calendar cale = Calendar.getInstance();
        cale.set(5, 0);
        return cale.getTime();
    }
}
