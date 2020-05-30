package learn.shendy.ebookshop.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.ActivitySplashScreenBinding;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class SplashScreenActivity extends BaseActivity {
    public static final int SPLASH_SCREEN_TIMEOUT = 2100;

    private ActivitySplashScreenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        setContentView(mBinding.getRoot());

        setupActivity();
        startNextActivity();
    }

    // MARK: Setup Methods

    private void setupActivity() {
        Animation splashLogoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim);
        Animation splashLogoNameAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_name_anim);
        Animation splashSlangAnim = AnimationUtils.loadAnimation(this, R.anim.splash_slang_anim);

        mBinding.splashAppLogoIv.setAnimation(splashLogoAnim);
        mBinding.splashAppNameTv.setAnimation(splashLogoNameAnim);
        mBinding.splashAppSloganTv.setAnimation(splashSlangAnim);
    }

    // MARK: Business Logic Methods

    private void startNextActivity() {
        final Class<? extends AppCompatActivity> nextActivityClass = isFirstTimeRun()
                ? DefaultSettingsActivity.class
                : MainActivity.class;

        Observable
                .timer(SPLASH_SCREEN_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .doOnComplete(() -> {
                    startActivity(new Intent(SplashScreenActivity.this, nextActivityClass));
                    finish();
                })
                .doOnSubscribe(mLongTermDisposables::add)
                .subscribe();
    }

    private boolean isFirstTimeRun() {
        return readSharedPreferencesKey(FIRST_TIME_RUN_PF, true);
    }
}
