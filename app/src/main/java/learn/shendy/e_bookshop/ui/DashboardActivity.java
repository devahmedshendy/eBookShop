package learn.shendy.e_bookshop.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import learn.shendy.e_bookshop.R;
import learn.shendy.e_bookshop.databinding.ActivityDashboardBinding;
import learn.shendy.e_bookshop.db.categories.Category;
import learn.shendy.e_bookshop.ui.adapters.DashboardRecyclerAdapter;
import learn.shendy.e_bookshop.ui.view_models.DashboardViewModel;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    private DashboardViewModel mViewModel;
    private ActivityDashboardBinding mBinding;
    private DashboardRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupActivity();
    }

    private void setupActivity() {
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        mRecyclerAdapter = new DashboardRecyclerAdapter(this);

        mViewModel.findCategoryList().observe(this, (categoryList) -> {
            for (Category category : categoryList) {
                Log.d(TAG, "setupActivity: " + category);
            }
            mRecyclerAdapter.setCategoryList(categoryList);
        });

        mBinding.dashboardRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mBinding.dashboardRecycler.setAdapter(mRecyclerAdapter);
    }
}
