package learn.shendy.ebookshop.views.models;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.reactivex.Completable;
import learn.shendy.ebookshop.db.DefaultSettingsRepository;

public class DefaultSettingsViewModel extends AndroidViewModel {
    private static final String TAG = "SetupDefaultSettingsVie";

    private DefaultSettingsRepository mRepository;

    public DefaultSettingsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DefaultSettingsRepository(application);
    }

    @SuppressLint("CheckResult")
    public Completable setupDefaultSettings() {
        return mRepository.setupDefaultSettings();
    }
}
