package learn.shendy.ebookshop.utils;

import androidx.room.TypeConverter;

import java.util.Date;

public class RoomConverters {
    @TypeConverter
    public static Date toDate(long timestamp) {
        return new Date(timestamp);
    }

    @TypeConverter
    public static long toTime(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static double fromStringToDouble(String value) {
        return Double.parseDouble(value);
    }

    @TypeConverter
    public static String fromDoubleToString(Double value) {
        return value.toString();
    }
}
