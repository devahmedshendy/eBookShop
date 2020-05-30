package learn.shendy.ebookshop.views;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES_NAME = "base";
    public static final String FIRST_TIME_RUN_PF = "firstTimeRun";

    private SharedPreferences mSharedPreferences;

    final CompositeDisposable mLongTermDisposables = new CompositeDisposable();

    // MARK: Lifecycle Methods

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActivity();
    }

    @Override
    protected void onDestroy() {
        mLongTermDisposables.clear();
        super.onDestroy();
    }

    // MARK: Setup Methods

    private void setupActivity() {
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    // MARK: Shared Preferences Methods

    boolean readSharedPreferencesKey(@NonNull String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    void updateSharedPreferencesKey(@NonNull String key, boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(key, value)
                .apply();
    }
}
