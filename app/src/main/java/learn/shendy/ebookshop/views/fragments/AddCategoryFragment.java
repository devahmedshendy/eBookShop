package learn.shendy.ebookshop.views.fragments;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.FragmentAddCategoryBinding;
import learn.shendy.ebookshop.db.categories.Category;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.dialogs.DiscardChangesDialog;
import learn.shendy.ebookshop.views.models.AppViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("CheckResult")
public class AddCategoryFragment extends BaseFragment {
    private static final String TAG = "AddCategoryFragment";

    boolean mFormHasChanges = false;
    private boolean mIgnoreFormChanges = true;

    private Category mNewCategory = new Category();

    private AppViewModel mViewModel;
    private FragmentAddCategoryBinding mBinding;

    // MARK: Constructor Methods

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentAddCategoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFragment(view, savedInstanceState);
        observeFormChanges();
        setupOnBackPressedCallbacks();
    }

//    @Nullable
//    @Override
//    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        return enter
//                ? AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom)
//                : AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_bottom);
//    }

    // MARK: Setup Methods

    private void setupFragment(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mBinding.setNewCategory(mNewCategory);

        mBinding.addCategoryToolbarSaveIv.setOnClickListener(__ -> saveFormChanges());
        mBinding.addCategoryToolbarCloseIv.setOnClickListener(__ -> navigateBack());
    }

    private void setupObservers() {
        observeFormChanges();

    }

    private void setupOnBackPressedCallbacks() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onFragmentBackPressed);

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onDiscardChangesDialogBackPressed);
    }

    private OnBackPressedCallback onFragmentBackPressed = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doesFormHaveChanges()) {
                openDiscardChangesDialog();
            } else {
                closeWholeFragment();
            }
        }
    };

    private OnBackPressedCallback onDiscardChangesDialogBackPressed = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            closeDiscardChangesDialog();
        }
    };

    // MARK: Observer Methods

    private void observeFormChanges() {
        RxTextView
                .textChanges(mBinding.newCategoryNameInput.getEditText())
                .skipInitialValue()
                .subscribeOn(Schedulers.computation())
                .map(CharSequence::toString)
                .doOnNext(s -> Log.d(TAG, "observeFormChanges: " + s))
                .map(this::isFormValid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        mBinding::setIsValidToSave,
                        ToastUtils::showUnhandledErrorToast
                );
    }

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

    // MARK: Listener Methods

    private void saveFormChanges() {
        if (!isFormValid(mBinding.getNewCategory().getName())) return;

        hideKeyboard();

        mBinding.setPleaseWait(true);

        mViewModel
                .addCategory(mNewCategory)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onCategoryAdded,
                        this::onAddCategoryFailed
                );
    }

    // MARK: Error/Success Observer Methods

    private void onDiscardChangesStateChanged(boolean saveChanges) {
        if (saveChanges) {
            closeDiscardChangesDialog();
            mBinding.addCategoryToolbarSaveIv.performClick();
        } else {
            closeWholeFragment();
        }
    }

    private void onCategoryAdded() {
        closeWholeFragment();
    }

    private void onAddCategoryFailed(Throwable t) {
        mBinding.setPleaseWait(false);

        if (t instanceof SQLiteConstraintException) {
            ToastUtils.showErrorToast(R.string.duplicate_category_name_error, t);
        } else {
            ToastUtils.showErrorToast(R.string.failed_to_add_category_error, t);
        }
    }

    // MARK: Misc

    private boolean doesFormHaveChanges() {
        return !TextUtils.isEmpty(mBinding.getNewCategory().getName())
                || !TextUtils.isEmpty(mBinding.getNewCategory().getDescription());
    }

    private boolean isFormValid(String categoryName) {
        return isCategoryNameValid(categoryName);
    }

    private boolean isCategoryNameValid(String categoryName) {
        boolean validCategoryName = !categoryName.trim().isEmpty();

        if (validCategoryName) {
            mBinding.newCategoryNameInput.setErrorEnabled(false);
        } else {
            mBinding.newCategoryNameInput.setError(getString(R.string.category_name_is_required));
        }

        return validCategoryName;
    }

    private void openDiscardChangesDialog() {
        onDiscardChangesDialogBackPressed.setEnabled(true);
        hideKeyboard();
        openDialogFragment(
                R.id.add_category_dialog_container,
                new DiscardChangesDialog(),
                DiscardChangesDialog.TAG
        );

        observeDiscardDialogState();
    }

    private void closeDiscardChangesDialog() {
        onDiscardChangesDialogBackPressed.setEnabled(false);
        hideKeyboard();
        closeDialogFragmentByTag(DiscardChangesDialog.TAG);
    }

    public void closeWholeFragment() {
        onFragmentBackPressed.setEnabled(false);
        onDiscardChangesDialogBackPressed.setEnabled(false);
        navigateBack();
    }
}
