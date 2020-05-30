package learn.shendy.ebookshop.utils;

import learn.shendy.ebookshop.App;

public class ResourcesUtils {
    private static final String TAG = "ResourcesUtils";

    public static String getString(int resId) {
        return App.INSTANCE.getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return App.INSTANCE.getString(resId, formatArgs);
    }

    public static int getColor(int resId) {
        return App.INSTANCE.getResources().getColor(resId);
    }
}
