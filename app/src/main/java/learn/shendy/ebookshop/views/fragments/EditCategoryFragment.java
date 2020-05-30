package learn.shendy.ebookshop.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.FragmentEditCategoryBinding;
import learn.shendy.ebookshop.db.categories.Category;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.BundleUtils;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.dialogs.DiscardChangesDialog;
import learn.shendy.ebookshop.views.models.AppViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCategoryFragment extends BaseFragment {
    private static final String TAG = "EditCategoryFragment";

    private boolean mFormHasChanges = false;
    private boolean mIgnoreFormChanges = true;

    private CategoryNameHolder mCategoryNameHolder;

    private Category mCategoryBeforeEdits = new Category();
    private Category mCategoryAfterEdits = new Category();

    private NavController mNavController;
    private AppViewModel mViewModel;
    private FragmentEditCategoryBinding mBinding;

    // MARK: Constructor Methods

    public EditCategoryFragment() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCategoryNameHolder = (CategoryNameHolder) getArguments()
                    .getSerializable(BundleUtils.CATEGORY_NAME_HOLDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentEditCategoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setupFragment(view, savedInstanceState);
        setupOnBackPressedCallbacks();
    }

    // MARK: Setup Methods

    @SuppressLint("CheckResult")
    private void setupFragment(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mBinding.setEditCategory(mCategoryAfterEdits);

        mBinding.editCategoryToolbarSaveIv.setOnClickListener(__ -> saveFormChanges());
        mBinding.editCategoryToolbarCloseIv.setOnClickListener(__ -> navigateBack());

        mViewModel
                .findCategory(mCategoryNameHolder.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onCategoryFound,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    void setupOnBackPressedCallbacks() {
        setupOnBackPressedCallback(onBackPressedFormCallback);
        setupOnBackPressedCallback(onBackPressedDiscardChangesDialogCallback);
    }


    @SuppressLint("CheckResult")
    private void observeFormChanges() {
        Observable
                .combineLatest(
                        observeCategoryName(),
                        observeCategoryDescription(),
                        this::isFormValid
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .doOnError(ToastUtils::showUnhandledErrorToast)
                .subscribe(mBinding::setIsFormChanged);
    }

    @SuppressLint("CheckResult")
    private void observeDiscardDialogState() {
        DiscardChangesDialog
                .sResultObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sShortTermDisposables::add)
                .subscribe(
                        this::onDiscardChangesStateChanged,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    private Observable<String> observeCategoryName() {
        return observeEditText(mBinding.editCategoryNameInput.getEditText())
                .startWith(mCategoryAfterEdits.getName());
    }

    private Observable<String> observeCategoryDescription() {
        return observeEditText(mBinding.editCategoryDescriptionInput.getEditText())
                .startWith(mCategoryAfterEdits.getDescription());
    }

    private Observable<String> observeEditText(EditText v) {
        return Observable.defer(() ->
                RxTextView
                    .textChanges(v)
                    .skipInitialValue()
                    .map(CharSequence::toString)
                    .subscribeOn(Schedulers.computation())
        );
    }

    // MARK: Back Pressed Methods

//    private void setupOnBackPressedDispatcherCallbacks() {
//        setupFormOnBackPressed();
//        setupDiscardChangesDialogOnBackPressed();
//        requireActivity()
//                .getOnBackPressedDispatcher()
//                .addCallback(getViewLifecycleOwner(), onFragmentBackPressed);

//        requireActivity()
//                .getOnBackPressedDispatcher()
//                .addCallback(getViewLifecycleOwner(), onDiscardChangesDialogBackPressed);
//    }

    private OnBackPressedCallback onBackPressedFormCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doesFormHaveChanges()) {
                openDiscardChangesDialog();
            } else {
                closeWholeFragment();
            }
        }
    };

    private OnBackPressedCallback onBackPressedDiscardChangesDialogCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            closeDiscardChangesDialog();
        }
    };

//    OnBackPressedCallback onBackPressedFormCallback = new OnBackPressedCallback(true) {
//        @Override
//        public void handleOnBackPressed() {
//            if (mFormHasChanges) {
//                openDiscardChangesDialog();
//            } else {
//                closeWholeFragment();
//            }
//        }
//    };

//    OnBackPressedCallback onBackPressedDiscardChangesDialogCallback = new OnBackPressedCallback(false) {
//        @Override
//        public void handleOnBackPressed() {
//            closeDiscardChangesDialog();
//        }
//    };


    // MARK: Listeners

    @SuppressLint("CheckResult")
    private void saveFormChanges() {
        hideKeyboard();

        mBinding.setPleaseWait(true);

        mCategoryNameHolder.setName(mCategoryAfterEdits.getName());

        mViewModel
                .updateCategory(mCategoryAfterEdits)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onCategoryUpdated,
                        this::onUpdateCategoryFailed
                );
    }

    // MARK: Observer Handler Methods

    private void onDiscardChangesStateChanged(boolean saveChanges) {
        if (saveChanges) {
            closeDiscardChangesDialog();
            mBinding.editCategoryToolbarSaveIv.performClick();
        } else {
            closeWholeFragment();
        }
    }

    private void onCategoryFound(Category category) {
        Log.d(TAG, "onCategoryFound: called");
        mBinding.setPleaseWait(false);

        mCategoryAfterEdits = category;
        mCategoryBeforeEdits = category.clone();

        mBinding.setEditCategory(mCategoryAfterEdits);

        observeFormChanges();
    }

    private void onCategoryUpdated() {
//        navigateToDashboardListFragment();
//        closeCurrentFragment();
        navigateToCategoryBookListFragment();
    }

    private void onUpdateCategoryFailed(Throwable t) {
        mBinding.setPleaseWait(false);
        ToastUtils.showErrorToast(R.string.failed_to_update_category_error, t);
    }

    //

    private boolean doesFormHaveChanges() {
        return !mCategoryBeforeEdits.equals(mCategoryAfterEdits);
    }

    private boolean isFormValid(String categoryName, String categoryDescription) {
//        mFormHasChanges = !mCategoryBeforeEdits.equals(mCategoryAfterEdits);

//        mIgnoreFormChanges = !isChanged;

        return doesFormHaveChanges() && isCategoryNameValid(categoryName);
    }

    private boolean isCategoryNameValid(String categoryName) {
        boolean validCategoryName = !categoryName.trim().isEmpty();

        if (validCategoryName) {
            mBinding.editCategoryNameInput.setErrorEnabled(false);
        } else {
            mBinding.editCategoryNameInput.setError(getString(R.string.category_name_is_required));
        }

        return validCategoryName;
    }

    private void openDiscardChangesDialog() {
        onBackPressedDiscardChangesDialogCallback.setEnabled(true);

        hideKeyboard();

        openDialogFragment(
                R.id.edit_category_dialog_container,
                new DiscardChangesDialog(),
                DiscardChangesDialog.TAG
        );

        observeDiscardDialogState();
    }

    private void closeDiscardChangesDialog() {
        onBackPressedDiscardChangesDialogCallback.setEnabled(false);
        closeDialogFragmentByTag(DiscardChangesDialog.TAG);
    }
    
    public void closeWholeFragment() {
        onBackPressedFormCallback.setEnabled(false);
        onBackPressedDiscardChangesDialogCallback.setEnabled(false);
        navigateBack();
    }

    private void navigateToCategoryBookListFragment() {
        onBackPressedFormCallback.setEnabled(false);
        onBackPressedDiscardChangesDialogCallback.setEnabled(false);

        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleUtils.CATEGORY_NAME_HOLDER, mCategoryNameHolder);

        mNavController.navigate(
                R.id.action_editCategoryFragment_to_categoryBookListFragment,
                bundle
        );
    }
}
