package learn.shendy.e_bookshop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import learn.shendy.e_bookshop.R;
import learn.shendy.e_bookshop.databinding.ActivitySplashScreenBindingImpl;
import learn.shendy.e_bookshop.util.AsyncUtils;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class SplashScreenActivity extends AppCompatActivity {
    public static final int SPLASH_SCREEN_TIMEOUT = 2500;

    private ActivitySplashScreenBindingImpl mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        setContentView(mBinding.getRoot());

        setupUI();
        startNextActivity();
    }

    // MARK: Setup Methods

    private void setupUI() {
        Animation splashLogoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim);
        Animation splashLogoNameAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_name_anim);
        Animation splashSlangAnim = AnimationUtils.loadAnimation(this, R.anim.splash_slang_anim);

        mBinding.splashLogoIv.setAnimation(splashLogoAnim);
        mBinding.splashLogoNameTv.setAnimation(splashLogoNameAnim);
        mBinding.splashSlangTv.setAnimation(splashSlangAnim);
    }

    // MARK: Business Logic Methods

    private void startNextActivity() {
        AsyncUtils.doDelayedInBackground(() -> {
            startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
