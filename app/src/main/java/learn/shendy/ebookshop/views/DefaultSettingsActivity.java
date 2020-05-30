package learn.shendy.ebookshop.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import io.reactivex.android.schedulers.AndroidSchedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.models.DefaultSettingsViewModel;

public class DefaultSettingsActivity extends BaseActivity {
    private static final String TAG = "DefaultSettingsActivity";

    private DefaultSettingsViewModel mViewModel;

    // MARK: Lifecycle Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_settings);

        setupActivity();
        observeDefaultSettingsState();
    }

    // MARK: Setup Methods

    private void setupActivity() {
        mViewModel = new ViewModelProvider(this).get(DefaultSettingsViewModel.class);
    }

    @SuppressLint("CheckResult")
    private void observeDefaultSettingsState() {
        mViewModel
                .setupDefaultSettings()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mLongTermDisposables::add)
                .subscribe(
                        this::onDefaultSettingsFinished,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    private void onDefaultSettingsFinished() {
        updateSharedPreferencesKey(FIRST_TIME_RUN_PF, false);
        ToastUtils.showInfoToast(R.string.default_settings_loaded_successfully);
        startNextActivity();
    }

    private void startNextActivity() {
        startActivity(new Intent(DefaultSettingsActivity.this, MainActivity.class));
        finish();
    }
}
