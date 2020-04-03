package learn.shendy.e_bookshop.util;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncUtils {
    private static final int NUMBER_OF_DATABASE_THREADS = 4;
    private static final ExecutorService sExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_DATABASE_THREADS);

    public static void doDelayedInBackground(@NonNull Runnable r, long delayMillis) {
        new Handler().postDelayed(r, delayMillis);
    }

    public static void doDatabaseInBackground(@NonNull Runnable r) {
        sExecutorService.execute(r);
    }
}
