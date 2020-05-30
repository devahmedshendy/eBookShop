package learn.shendy.ebookshop.views.fragments;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.FragmentEditBookBinding;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.books.BookDetails;
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
public class EditBookFragment extends BaseFragment {
    public static final String TAG = "EditBookFragment";

    private Scheduler mSingleScheduler = Schedulers.single();

    private boolean mIsFormChanged = false;

    private long mBookId;
    private BookDetails mBookDetailsBeforeEdits = new BookDetails();
    private BookDetails mBookDetailsAfterEdits = new BookDetails();


    private AppViewModel mViewModel;
    private FragmentEditBookBinding mBinding;

    // MARK: Constructor Methods

    public EditBookFragment() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mBookId = getArguments().getLong(BundleUtils.BOOK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentEditBookBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFragment(view, savedInstanceState);
        loadBookDetails();
        setupOnBackPressedCallbacks();
    }

    // MARK: Setup Methods

    private void setupFragment(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mBinding.editBookCoverIv.setOnClickListener(v -> openGallery.launch("image/*"));
        mBinding.editBookCategoryNameInputTv.setOnClickListener(v -> openSelectCategoryNameDialog());

        mBinding.editBookToolbarSaveIv.setOnClickListener(onSaveCategoryButtonClick);
        mBinding.editBookToolbarCloseIv.setOnClickListener(__ -> navigateBack());
    }

    @SuppressLint("CheckResult")
    private void loadBookDetails() {
        mBinding.setPleaseWait(true);

        mViewModel
                .findBookDetails(mBookId)
                .delay(800, TimeUnit.MILLISECONDS)
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onBookDetailsFound,
                        ToastUtils::showUnhandledErrorToast
                );

    }

    private void setupOnBackPressedCallbacks() {
        setupOnBackPressedCallback(onBackPressedFormCallback);
        setupOnBackPressedCallback(onBackPressedDiscardChangesDialogCallback);
        setupOnBackPressedCallback(onBackPressedSelectCategoryNameDialogCallback);
    }

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
                .doOnError(ToastUtils::showUnhandledErrorToast)
                .doOnNext(this::validateBookCoverName)
                .subscribe();

        observeBookName()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .doOnError(ToastUtils::showUnhandledErrorToast)
                .doOnNext(this::validateBookName)
                .subscribe();

        observeBookPrice()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .doOnError(ToastUtils::showUnhandledErrorToast)
                .doOnNext(this::validateBookPrice)
                .subscribe();

        Observable
                .combineLatest(
                        observeBookCoverName(),
                        observeBookName(),
                        observeBookPrice(),
                        observeBookCategoryName(),
                        this::isFormValid
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .doOnError(ToastUtils::showUnhandledErrorToast)
                .subscribe(mBinding::setIsValidToSave);
    }

    private Observable<String> observeBookCoverName() {
        return observeEditText(mBinding.editBookCoverNameInput);
    }

    private Observable<String> observeBookName() {
        return observeEditText(mBinding.editBookNameInput.getEditText());
    }

    private Observable<String> observeBookPrice() {
        return observeEditText(mBinding.editBookPriceInput.getEditText());
    }

    private Observable<String> observeBookCategoryName() {
        return observeEditText(mBinding.editBookCategoryNameInputTv);
    }

    private Observable<String> observeEditText(EditText v) {
        return Observable.defer(() ->
                RxTextView
                        .textChanges(v)
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


                            mBinding.getBookDetails().setCover(coverName);
                            mBinding.editBookCoverIv.setImageURI(uri);
                        } else {
                            ToastUtils.showInfoToast(R.string.unsupported_image_format);
                        }
                    }
                }
            }
    );

    @SuppressLint("CheckResult")
    private View.OnClickListener onSaveCategoryButtonClick = btn -> {
        if (!isFormValid(
                mBinding.editBookCoverNameInput.getText().toString(),
                mBinding.editBookNameInput.getEditText().getText().toString(),
                mBinding.editBookPriceInput.getEditText().getText().toString(),
                mBinding.editBookCategoryNameInput.getEditText().getText().toString()
        )) return;

        hideKeyboard();

        mBinding.setPleaseWait(true);


        Book book = new BookBuilder().from(mBookDetailsAfterEdits);
        Bitmap coverBitmap = ((BitmapDrawable) mBinding.editBookCoverIv.getDrawable()).getBitmap();

        mViewModel
                .updateBook(book, coverBitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onBookUpdated,
                        this::onUpdateBookFailed
                );
    };

    @SuppressLint("CheckResult")
    private void onBookDetailsFound(BookDetails bookDetails) {
        InternalStorageUtils
                .loadBookCoverBitmap(bookDetails.getCover())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sShortTermDisposables::add)
                .subscribe(
                        bitmap -> {
                            mBinding.setPleaseWait(false);

                            mBookDetailsBeforeEdits = bookDetails;
                            mBookDetailsAfterEdits = mBookDetailsBeforeEdits.clone();

                            mBinding.setBookDetails(mBookDetailsAfterEdits);
                            mBinding.editBookCoverIv.setImageBitmap(bitmap);

                            observeFormChanges();
                        },
                        ToastUtils::showUnhandledErrorToast
                );

    }

    private void onDiscardChangesStateChanged(boolean saveChanges) {
        if (saveChanges) {
            closeDiscardChangesDialog();
            mBinding.editBookToolbarSaveIv.performClick();
        } else {
            closeWholeFragment();
        }
    }

    private void onCategoryNameSelected(CategoryNameHolder categoryNameHolder) {
        mBookDetailsAfterEdits.setCategoryId(categoryNameHolder.getId());
        mBookDetailsAfterEdits.setCategoryName(categoryNameHolder.getName());
        closeSelectCategoryNameDialog();
    }

    private void onBookUpdated() {
        closeWholeFragment();
    }

    private void onUpdateBookFailed(Throwable t) {
        mBinding.setPleaseWait(false);

        if (t instanceof SQLiteConstraintException) {
            ToastUtils.showErrorToast(R.string.duplicate_book_name_error, t);
        } else {
            ToastUtils.showErrorToast(R.string.failed_to_add_book_error, t);
        }
    }
    
    // MARK: Misc

    private boolean doesFormHaveChanges() {
        return !mBookDetailsBeforeEdits.equals(mBookDetailsAfterEdits);
    }

    private boolean isFormValid(
            String bookCoverName, String bookName, String bookPrice, String bookCategoryName
    ) {
        return doesFormHaveChanges()
                && validateBookCoverName(bookCoverName)
                && validateBookName(bookName)
                && validateBookPrice(bookPrice);
    }

    private boolean validateBookCoverName(String coverName) {
        boolean isValid = coverName != null && !coverName.trim().isEmpty();

        mBinding.editBookCoverNameError.setVisibility(isValid ? View.GONE : View.VISIBLE);

        return isValid;
    }

    private boolean validateBookName(String bookName) {
        boolean isValid = !bookName.trim().isEmpty();

        if (isValid) {
            mBinding.editBookNameInput.setErrorEnabled(false);
        } else {
            mBinding.editBookNameInput.setError(getString(R.string.book_name_is_required));
        }

        return isValid;
    }

    private boolean validateBookPrice(String bookPrice) {
        boolean isValid = !bookPrice.trim().isEmpty();

        if (isValid) {
            double price = Double.parseDouble(bookPrice);
            if (price == 0f) {
                mBinding.editBookPriceInput.setError(getString(R.string.book_price_should_be_at_least));
            } else {
                mBinding.editBookPriceInput.setErrorEnabled(false);
            }

        } else {
            mBinding.editBookPriceInput.setError(getString(R.string.book_price_is_required));
        }

        return isValid;
    }
    //                        this::onUpdateBookFailed

    private void openDiscardChangesDialog() {
        onBackPressedDiscardChangesDialogCallback.setEnabled(true);
        openDialogFragment(
                R.id.edit_book_dialog_container,
                new DiscardChangesDialog(),
                DiscardChangesDialog.TAG
        );
        observeDiscardDialogState();
    }

    private void openSelectCategoryNameDialog() {
        onBackPressedSelectCategoryNameDialogCallback.setEnabled(true);
        openDialogFragment(
                R.id.edit_book_dialog_container,
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

    public void closeWholeFragment() {
        onBackPressedDiscardChangesDialogCallback.setEnabled(false);
        onBackPressedFormCallback.setEnabled(false);
        navigateBack();
    }
}
