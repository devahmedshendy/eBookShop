package learn.shendy.ebookshop.db;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.App;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.utils.InternalStorageUtils;
import learn.shendy.ebookshop.utils.ResourcesUtils;

public class DefaultSettingsRepository {
    private static final String TAG = "DefaultSettingsReposito";

    private Map<Integer, String> sBookCoverDrawableList = new HashMap<>();

    private Scheduler mIOScheduler = Schedulers.io();

    public DefaultSettingsRepository(Application application) {
        AppDatabase.getSingletonInstance(application);
        initializeBookCoverDrawableList();
    }

    @SuppressLint("CheckResult")
    public Completable setupDefaultSettings() {
        return installDefaultData()
                .andThen(InternalStorageUtils.emptyFilesDir())
                .andThen(storeDefaultBookCovers())
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    private Completable installDefaultData() {
        return AppDatabase.loadDefaultDataIntoDatabase();
    }

    private Completable storeDefaultBookCovers() {
        return Completable
                .defer(() -> {
                    try {
                        for (Map.Entry<Integer, String> bookCover : sBookCoverDrawableList.entrySet()) {
                            int coverDrawableId = bookCover.getKey();
                            String coverFilename = bookCover.getValue();

                            Bitmap coverBitmap = BitmapFactory
                                    .decodeResource(App.INSTANCE.getResources(), coverDrawableId);

                            InternalStorageUtils
                                    .storeBookCoverBitmap(coverBitmap, coverFilename)
                                    .subscribe();
                        }

                        return Completable.complete();

                    } catch (Exception e) {
                        return Completable.error(e);
                    }
                });
    }

    private void initializeBookCoverDrawableList() {
        sBookCoverDrawableList.put(
                R.drawable.action_and_adventure_book1,
                ResourcesUtils.getString(R.string.action_and_adventure_book1_cover)
        );

        sBookCoverDrawableList.put(
                R.drawable.action_and_adventure_book2,
                ResourcesUtils.getString(R.string.action_and_adventure_book2_cover)
        );

        sBookCoverDrawableList.put(
                R.drawable.fantasy_book1,
                ResourcesUtils.getString(R.string.fantasy_book1_cover)
        );

        sBookCoverDrawableList.put(
                R.drawable.fantasy_book2,
                ResourcesUtils.getString(R.string.fantasy_book2_cover)
        );

        sBookCoverDrawableList.put(
                R.drawable.science_fiction_book1,
                ResourcesUtils.getString(R.string.science_fiction_book1_cover)
        );

        sBookCoverDrawableList.put(
                R.drawable.science_fiction_book2,
                ResourcesUtils.getString(R.string.science_fiction_book2_cover)
        );
    }
}
