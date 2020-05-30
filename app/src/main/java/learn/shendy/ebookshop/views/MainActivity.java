package learn.shendy.ebookshop.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import learn.shendy.ebookshop.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // MARK: Lifecycle Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
