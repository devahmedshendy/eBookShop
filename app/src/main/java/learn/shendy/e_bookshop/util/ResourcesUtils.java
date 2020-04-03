package learn.shendy.e_bookshop.util;

import learn.shendy.e_bookshop.App;

public class ResourcesUtils {
    public static String getString(int resId) {
        return App.instance.getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return App.instance.getString(resId, formatArgs);
    }
}
