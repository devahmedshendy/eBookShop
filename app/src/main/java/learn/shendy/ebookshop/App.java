package learn.shendy.ebookshop;

import android.app.Application;

public class App extends Application {
    public static String PACKAGE_NAME;
    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
        PACKAGE_NAME = getApplicationContext().getPackageName();
    }
}
