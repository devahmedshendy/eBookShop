package learn.shendy.e_bookshop.util;

import android.os.Handler;

import androidx.annotation.NonNull;

public class AsyncUtils {
    public static void doDelayedInBackground(@NonNull Runnable runnable, long delayMillis) {
        new Handler().postDelayed(runnable, delayMillis);
    }
}
