package learn.shendy.ebookshop.views.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.adapters.CategoryNameListAdapter;
import learn.shendy.ebookshop.views.models.AppViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryNamesSelectDialog extends DialogFragment {
    public static final String TAG = "CategoryNameSelectDialo";

    // MARK: Dialog State Observable

    private static PublishSubject<CategoryNameHolder> sResult = PublishSubject.create();

    public static Observable<CategoryNameHolder> sResultObservable() {
        return sResult
                .delay(150, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single());
    }

    // MARK: Properties

    private ProgressBar mLoadingSpinner;
    private RecyclerView mRecyclerView;
    private TextInputLayout mSearchInput;

    private CategoryNameListAdapter mAdapter;
    private AppViewModel mViewModel;

    // MARK: Constructor Methods
    
    public CategoryNamesSelectDialog() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_category_names_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDialogViews(view, savedInstanceState);
        loadCategoryNameList("");
        observeSearchInput();
    }

    private void setupDialogViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        mAdapter = new CategoryNameListAdapter(requireContext(), sResult);

        mLoadingSpinner = view.findViewById(R.id.progress_spinner_v);
        mSearchInput = view.findViewById(R.id.select_category_name_search_input);

        mRecyclerView = view.findViewById(R.id.select_category_name_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @SuppressLint("CheckResult")
    private void observeSearchInput() {
        RxTextView
                .textChanges(mSearchInput.getEditText())
                .skipInitialValue()
                .debounce(250, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .doOnError(ToastUtils::showUnhandledErrorToast)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadCategoryNameList);

    }

    @SuppressLint("CheckResult")
    private void loadCategoryNameList(String searchText) {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingSpinner.setVisibility(View.VISIBLE);

        mViewModel
                .searchCategoryNameList(searchText)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(categoryNames -> Log.d(TAG, "loadCategoryNameList: " + categoryNames.size()))
                .subscribe(
                        this::onCategoryNameListReady,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    // MARK: Observer Handler Methods

    private void onCategoryNameListReady(List<CategoryNameHolder> categoryNameHolderList) {
        mAdapter.update(categoryNameHolderList);

        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingSpinner.setVisibility(View.GONE);

    }
}
