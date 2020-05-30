package learn.shendy.ebookshop.views.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.FragmentAddBookBinding;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.builders.BookBuilder;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.BundleUtils;
import learn.shendy.ebookshop.utils.InternalStorageUtils;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.dialogs.CategoryNamesSelectDialog;
import learn.shendy.ebookshop.views.dialogs.DiscardChangesDialog;
import learn.shendy.ebookshop.views.models.AppViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBookFragment extends BaseFragment {
    private static final String TAG = "AddBookFragment";

    private CategoryNameHolder mCategoryNameHolder;

    private AppViewModel mViewModel;
    private FragmentAddBookBinding mBinding;

    // MARK: Constructor Methods

    public AddBookFragment() {
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
        // Inflate the layout for this fragment
        mBinding = FragmentAddBookBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFragmentViews();
        observeFormChanges();
        setupOnBackPressedCallbacks();
    }

    // MARK: Setup Methods

    private void setupFragmentViews() {
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mBinding.setCategoryName(mCategoryNameHolder.getName());

        mBinding.newBookCoverIv.setOnClickListener(v -> openGallery.launch("image/*"));
        mBinding.newBookCategoryNameInputTv.setOnClickListener(v ->  openSelectCategoryNameDialog());

        mBinding.addBookToolbarSaveIv.setOnClickListener(__ -> saveFormChanges());
        mBinding.addBookToolbarCloseIv.setOnClickListener(__ -> navigateBack());
    }

    void setupOnBackPressedCallbacks() {
        setupOnBackPressedCallback(onBackPressedFormCallback);
        setupOnBackPressedCallback(onBackPressedDiscardChangesDialogCallback);
        setupOnBackPressedCallback(onBackPressedSelectCategoryNameDialogCallback);
//        setupFormOnBackPressed();
//        setupDiscardChangesDialogOnBackPressed();
//        setupCategoryNameSelectDialogOnBackPressed();
    }

//    private void setupOnBackPressedDispatcherCallbacks() {
//        requireActivity()
//                .getOnBackPressedDispatcher()
//                .addCallback(getViewLifecycleOwner(), onFragmentBackPressed);
//
//        requireActivity()
//                .getOnBackPressedDispatcher()
//                .addCallback(getViewLifecycleOwner(), onDiscardChangesDialogBackPressed);
//
//        requireActivity()
//                .getOnBackPressedDispatcher()
//                .addCallback(getViewLifecycleOwner(), onSelectCategoryNameDialogBackPressed);
//    }
//
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
//
    private OnBackPressedCallback onBackPressedDiscardChangesDialogCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            closeDiscardChangesDialog();
        }
    };
//
    private OnBackPressedCallback onBackPressedSelectCategoryNameDialogCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            closeSelectCategoryNameDialog();
        }
    };

    // MARK: Observer Methods

    @SuppressLint("CheckResult")
    private void observeFormChanges() {

        observeBookCoverName()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::validateBookCoverName,
                        ToastUtils::showUnhandledErrorToast
                );

        observeBookName()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::validateBookName,
                        ToastUtils::showUnhandledErrorToast
                );

        observeBookPrice()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::validateBookPrice,
                        ToastUtils::showUnhandledErrorToast
                );

        Observable
                .combineLatest(
                        observeBookCoverName(),
                        observeBookName(),
                        observeBookPrice(),
                        this::isFormValid
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        mBinding::setIsValidToSave,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    private Observable<String> observeBookCoverName() {
        return observeEditText(mBinding.newBookCoverNameInput);
    }

    private Observable<String> observeBookName() {
        return observeEditText(mBinding.newBookNameInput.getEditText());
    }

    private Observable<String> observeBookPrice() {
        return observeEditText(mBinding.newBookPriceInput.getEditText());
    }

    private Observable<String> observeEditText(EditText v) {
        return Observable.defer(() ->
                RxTextView
                    .textChanges(v)
                    .skipInitialValue()
                    .map(CharSequence::toString)
                    .subscribeOn(mSingleScheduler)
        );
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

    @SuppressLint("CheckResult")
    private void observeSelectCategoryNameDialogState() {
        CategoryNamesSelectDialog
                .sResultObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sShortTermDisposables::add)
                .subscribe(
                        this::onCategoryNameSelected,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    // MARK: Listener Methods

    ActivityResultLauncher<String> openGallery = prepareCall(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        if (InternalStorageUtils.isSupportedCoverBookFormat(uri)) {
                            String coverName = String.format(
                                    "%s.%s",
                                    System.currentTimeMillis(),
                                    InternalStorageUtils.getFileExtensionFromUri(uri)
                            );


                            mBinding.setBookCoverName(coverName);
                            mBinding.newBookCoverIv.setImageURI(uri);
                        } else {
                            ToastUtils.showInfoToast(R.string.unsupported_image_format);
                        }
                    }
                }
            }
    );

    @SuppressLint("CheckResult")
    private void saveFormChanges() {
        if (!isFormValid(
                mBinding.newBookCoverNameInput.getText().toString(),
                mBinding.newBookNameInput.getEditText().getText().toString(),
                mBinding.newBookPriceInput.getEditText().getText().toString()
        )) return;

        hideKeyboard();

        mBinding.setPleaseWait(true);

        String bookName = mBinding.getBookName();
        double bookPrice = Double.parseDouble(mBinding.getBookPrice());
        String bookCover = mBinding.getBookCoverName();
        long categoryId = mCategoryNameHolder.getId();

        Book newBook = new BookBuilder()
                .setName(bookName)
                .setPrice(bookPrice)
                .setCover(bookCover)
                .setCategoryId(categoryId)
                .build();

        Bitmap coverBitmap = ((BitmapDrawable) mBinding.newBookCoverIv.getDrawable()).getBitmap();

        mViewModel
                .addBook(newBook, coverBitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onBookAdded,
                        this::onAddBookFailed
                );
    }

    private void onDiscardChangesStateChanged(boolean saveChanges) {
        if (saveChanges) {
            closeDiscardChangesDialog();
            mBinding.addBookToolbarSaveIv.performClick();
        } else {
            closeWholeFragment();
        }
    }

    private void onCategoryNameSelected(CategoryNameHolder categoryNameHolder) {
        closeSelectCategoryNameDialog();
        mCategoryNameHolder = categoryNameHolder;
        mBinding.setCategoryName(mCategoryNameHolder.getName());
    }

    private void onBookAdded() {
        closeWholeFragment();
    }

    private void onAddBookFailed(Throwable t) {
        mBinding.setPleaseWait(false);

        if (t instanceof SQLiteConstraintException) {
            ToastUtils.showErrorToast(R.string.duplicate_book_name_error, t);
        } else {
            ToastUtils.showErrorToast(R.string.failed_to_add_book_error, t);
        }
    }

    // MARK: Misc

    private boolean doesFormHaveChanges() {
        return !TextUtils.isEmpty(mBinding.newBookCoverNameInput.getText())
                || !TextUtils.isEmpty(mBinding.newBookNameInput.getEditText().getText())
                || !TextUtils.isEmpty(mBinding.newBookPriceInput.getEditText().getText());
    }

    private boolean isFormValid(String bookCoverName, String bookName, String bookPrice) {
        return validateBookCoverName(bookCoverName)
                && validateBookName(bookName)
                && validateBookPrice(bookPrice);
    }

    private boolean validateBookCoverName(String coverName) {
        boolean isValid = coverName != null && !coverName.trim().isEmpty();

        mBinding.newBookCoverNameError.setVisibility(isValid ? View.GONE : View.VISIBLE);

        return isValid;
    }

    private boolean validateBookName(String bookName) {
        boolean isValid = !bookName.trim().isEmpty();

        if (isValid) {
            mBinding.newBookNameInput.setErrorEnabled(false);
        } else {
            mBinding.newBookNameInput.setError(getString(R.string.book_name_is_required));
        }

        return isValid;
    }

    private boolean validateBookPrice(String bookPrice) {
        boolean isValid = !bookPrice.trim().isEmpty();

        if (isValid) {
            double price = Double.parseDouble(bookPrice);
            if (price == 0f) {
                mBinding.newBookPriceInput.setError(getString(R.string.book_price_should_be_at_least));
            } else {
                mBinding.newBookPriceInput.setErrorEnabled(false);
            }

        } else {
            mBinding.newBookPriceInput.setError(getString(R.string.book_price_is_required));
        }

        return isValid;
    }

    private void openDiscardChangesDialog() {
        onBackPressedDiscardChangesDialogCallback.setEnabled(true);

        openDialogFragment(
                R.id.add_book_dialog_container,
                new DiscardChangesDialog(),
                DiscardChangesDialog.TAG
        );

        observeDiscardDialogState();
    }

    private void openSelectCategoryNameDialog() {
        onBackPressedSelectCategoryNameDialogCallback.setEnabled(true);

        openDialogFragment(
                R.id.add_book_dialog_container,
                new CategoryNamesSelectDialog(),
                CategoryNamesSelectDialog.TAG
        );

        observeSelectCategoryNameDialogState();
    }

    private void closeDiscardChangesDialog() {
        onBackPressedDiscardChangesDialogCallback.setEnabled(false);
        closeDialogFragmentByTag(DiscardChangesDialog.TAG);
    }

    private void closeSelectCategoryNameDialog() {
        onBackPressedSelectCategoryNameDialogCallback.setEnabled(false);
        closeDialogFragmentByTag(CategoryNamesSelectDialog.TAG);
    }

    private void closeWholeFragment() {
        onBackPressedFormCallback.setEnabled(false);
        onBackPressedDiscardChangesDialogCallback.setEnabled(false);
        onBackPressedSelectCategoryNameDialogCallback.setEnabled(false);
        navigateBack();
    }

    private String getFileNameFromUri(Uri uri) {
        Cursor returnCursor = requireActivity()
                .getContentResolver()
                .query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        return returnCursor.getString(nameIndex);
    }
}
