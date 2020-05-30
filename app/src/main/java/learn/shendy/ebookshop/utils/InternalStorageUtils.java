package learn.shendy.ebookshop.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import learn.shendy.ebookshop.App;
import learn.shendy.ebookshop.R;

public class InternalStorageUtils {
    private static final String TAG = "InternalStorageUtils";

    public static Completable storeBookCoverBitmap(Bitmap bitmap, String cover) {
        return Completable
                .defer(() -> {
                    try (FileOutputStream fos = App.INSTANCE.openFileOutput(cover, Context.MODE_PRIVATE)) {
                        Bitmap.CompressFormat imageCompressFormat = cover.endsWith(".png")
                                ? Bitmap.CompressFormat.PNG
                                : Bitmap.CompressFormat.JPEG;

                        bitmap.compress(imageCompressFormat, 90, fos);

                    } catch (IOException e) {
                        return Completable.error(e);
                    }

                    return CompletableObserver::onComplete;
                });
    }

    public static Single<Bitmap> loadBookCoverBitmap(final String cover) {
        if (cover == null) return Single.error(new FileNotFoundException());

        return Single
                .defer(() -> {
                    File imageFile = new File(App.INSTANCE.getFilesDir(), cover);

                    try (FileInputStream fis = new FileInputStream(imageFile)){
                        return Single.just(BitmapFactory.decodeStream(fis));

                    } catch (FileNotFoundException e) {
                        return Single.error(e);
                    }
                });
    }

    public static Completable deleteFile(final String cover) {
        File file = new File(App.INSTANCE.getFilesDir(), cover);

        if (!file.exists()) {
            return Completable.error(new FileNotFoundException());
        }

        if (file.delete()) {
            return Completable.complete();
        } else {
            String message = ResourcesUtils.getString(R.string.unable_to_delete_book_cover);
            return Completable.error(new IOException(message));
        }
    }

    public static Completable emptyFilesDir() {
        File[] files = App.INSTANCE.getFilesDir().listFiles();

        return Observable.range(0, files.length)
                .map(i -> files[i])
                .flatMapCompletable(file -> deleteFile(file.getName()));
    }

    public static boolean isSupportedCoverBookFormat(Uri uri) {
        String mimeType = getUriMimeType(uri);

        return mimeType != null
                && (mimeType.equalsIgnoreCase("image/jpeg")
                || mimeType.equalsIgnoreCase("image/png"));
    }

    public static String getFileNameFromUri(Uri uri) {
        Cursor returnCursor = App.INSTANCE
                .getContentResolver()
                .query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        String filename = returnCursor.getString(nameIndex);

        returnCursor.close();

        return filename;
    }

    public static String getFileExtensionFromUri(Uri uri) {
        String filename = getFileNameFromUri(uri);

        return filename.substring(filename.length() - 3);
    }

    public static String getUriMimeType(Uri uri) {
        return App.INSTANCE.getContentResolver().getType(uri);
    }
}
