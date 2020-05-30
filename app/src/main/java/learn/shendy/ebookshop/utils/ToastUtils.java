package learn.shendy.ebookshop.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import learn.shendy.ebookshop.App;
import learn.shendy.ebookshop.R;

public class ToastUtils {
    private static final String TAG = "ToastUtils";

    private static Context getApplicationContext() {
        return App.INSTANCE.getApplicationContext();
    }

    public static void showInfoToast(String message) {
        showLongToast(message);
    }

    public static void showInfoToast(int message) {
        showLongToast(message);
    }

    public static void showErrorToast(int message, Throwable t) {
        Log.e(TAG, t.getMessage(), t);
        showLongToast(message);
    }

    public static void showUnhandledErrorToast(Throwable t) {
        showErrorToast(R.string.unhandled_error, t);
    }

    private static void showLongToast(int message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private static void showLongToast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private static void showShortToast(int message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private static void showShortToast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}
