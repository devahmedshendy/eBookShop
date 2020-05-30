package learn.shendy.ebookshop.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.FragmentDashboardBinding;
import learn.shendy.ebookshop.db.categories.CategoryBookListHolder;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.BundleUtils;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.DefaultSettingsActivity;
import learn.shendy.ebookshop.views.adapters.BookCoverDisplayHolder.BookCoverClickHandler;
import learn.shendy.ebookshop.views.adapters.DashboardListAdapter;
import learn.shendy.ebookshop.views.adapters.DashboardListAdapter.DashboardCategoryHolder.CategoryNameClickHandler;
import learn.shendy.ebookshop.views.dialogs.DeleteBookDialog;
import learn.shendy.ebookshop.views.models.AppViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends BaseFragment implements
        BookCoverClickHandler,
        CategoryNameClickHandler,
        OnNavigationItemSelectedListener
{
    private static final String TAG = "DashboardFragment";

    private AppViewModel mViewModel;
    private DashboardListAdapter mAdapter;
    private FragmentDashboardBinding mBinding;

    private NavController mNavController;

    // MARK: Constructor Methods

    public DashboardFragment() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentDashboardBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFragment(view, savedInstanceState);
        setupObservers();
    }

    // MARK: Setup Methods

    private void setupFragment(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mBinding.dashboardNavigationView.bringToFront();
        mBinding.dashboardNavigationView.setNavigationItemSelectedListener(this);
        mBinding.dashboardToolbarMenuIv.setOnClickListener(v -> mBinding.dashboardDrawerLayout.openDrawer(GravityCompat.START));

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                requireActivity(),
//                mBinding.dashboardDrawerLayout,
//                mBinding.dashboardToolbar,
//                R.string.dashboard_drawer_open,
//                R.string.dashboard_drawer_close
//        );
//        mBinding.dashboardDrawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

        mAdapter = new DashboardListAdapter(requireContext(), this);
        mBinding.dashboardListRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.dashboardListRecycler.setAdapter(mAdapter);

        mBinding.addCategoryFab.setOnClickListener(onAddCategoryClick);
    }

    private void setupObservers() {
        observeDashboardListState();
    }

    @SuppressLint("CheckResult")
    private void observeDashboardListState() {
        mViewModel
                .observeDashboardListState()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onDashboardListReady,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    @SuppressLint("CheckResult")
    private void observeDeleteBookDialog() {
        DeleteBookDialog
                .sResultObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sShortTermDisposables::add)
                .subscribe(
                        this::onDeleteBookDialogStateChanged,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    // MARK: Listeners

    private View.OnClickListener onAddCategoryClick = btn ->
            mNavController.navigate(R.id.action_dashboardFragment_to_addCategoryFragment);

    private void onDashboardListReady(List<CategoryBookListHolder> list) {
        mBinding.loadingSpinnerInclude.setVisibility(View.GONE);
        mBinding.dashboardListRecycler.setVisibility(View.VISIBLE);

        mAdapter.update(list);
    }

    // MARK: Book Cover Click Handler

    @Override
    public void onCategoryNameClick(CategoryNameHolder categoryNameHolder) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleUtils.CATEGORY_NAME_HOLDER, categoryNameHolder);

        mNavController
                .navigate(
                        R.id.action_dashboardFragment_to_categoryBookListFragment,
                        bundle
                );
    }

    @Override
    public void onBookCoverClick(long bookId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BundleUtils.BOOK_ID, bookId);

        mNavController
                .navigate(
                        R.id.action_dashboardFragment_to_editBookFragment,
                        bundle
                );
    }

    @Override
    public void onBookCoverLongClick(int containerId) {
        closeDialogFragmentByTag(DeleteBookDialog.TAG);

        openDialogFragment(
                containerId,
                new DeleteBookDialog(),
                DeleteBookDialog.TAG
        );

        observeDeleteBookDialog();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dashboard_drawer_reset_item) {
            Intent intent = new Intent(requireContext(), DefaultSettingsActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }

        return false;
    }

    private void onDeleteBookDialogStateChanged(Boolean success) {
        if (success) {
            mViewModel.reloadDashboardList();
        }

        closeDialogFragmentByTag(DeleteBookDialog.TAG);
    }

    private void updateRecyclerContent(List<CategoryBookListHolder> list) {
        mAdapter.update(list);
    }
}
