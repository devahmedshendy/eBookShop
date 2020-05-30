package learn.shendy.ebookshop.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;

public class BindingAdapterMethods {
    private static final String TAG = "BindingAdapterMethods";

    @SuppressLint("CheckResult")
    @BindingAdapter("setBookCover")
    public static void setBookCover(ImageView iv, String cover) {
        InternalStorageUtils
                .loadBookCoverBitmap(cover)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> onLoadCoverFinish(iv, bitmap),
                        throwable -> onLoadCoverFailed(iv)
                );
    }

    private static void onLoadCoverFinish(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    private static void onLoadCoverFailed(ImageView iv) {
        iv.setImageResource(R.drawable.book_cover_unavailable_placeholder);
    }

}
