package learn.shendy.e_bookshop.util;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateUtils {
    @TypeConverter
    public static Date toDate(long timestamp) {
        return new Date(timestamp);
    }

    @TypeConverter
    public static long toTime(Date date) {
        return date.getTime();
    }
}
