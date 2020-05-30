package learn.shendy.ebookshop.views.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.FragmentCategoryBinding;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.BundleUtils;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.adapters.BookCoverDisplayHolder.BookCoverClickHandler;
import learn.shendy.ebookshop.views.adapters.CategoryBookListAdapter;
import learn.shendy.ebookshop.views.dialogs.ConfirmDeleteDialog;
import learn.shendy.ebookshop.views.dialogs.DeleteBookDialog;
import learn.shendy.ebookshop.views.models.AppViewModel;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment implements OnMenuItemClickListener, BookCoverClickHandler {
    private static final String TAG = "CategoryBookListFragmen";

    private CategoryNameHolder mCategoryNameHolder;

    private NavController mNavController;

    private AppViewModel mViewModel;
    private CategoryBookListAdapter mAdapter;
    private FragmentCategoryBinding mBinding;
    
    // MARK: Constructor Methods

    public CategoryFragment() {
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
        mBinding = FragmentCategoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupFragment(view, savedInstanceState);
        loadCategoryBookList();
        setupOnBackPressedCallbacks();
    }

    // MARK: Setup Methods

    private void setupFragment(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mBinding.setCategoryName(mCategoryNameHolder.getName());

        mAdapter = new CategoryBookListAdapter(requireContext(), this);
        setRecyclerLayoutToGrid();

        mBinding.bookListToolbarBackIv.setOnClickListener(__ -> navigateBack());
        mBinding.bookListToolbarMoreMenu.setOnClickListener(onMoreMenuClick);
        mBinding.bookListToolbarEditIv.setOnClickListener(onEditCategoryClick);
        mBinding.bookListAddBookFab.setOnClickListener(onAddBookButtonClick);
    }

    void setupOnBackPressedCallbacks() {
        setupOnBackPressedCallback(onBackPressedConfirmDeleteDialogCallback);
    }

    OnBackPressedCallback onBackPressedConfirmDeleteDialogCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            closeConfirmDeleteDialog();
        }
    };

    @SuppressLint("CheckResult")
    private void loadCategoryBookList() {
        mViewModel
                .findCategoryBookList(mCategoryNameHolder.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sLongTermDisposables::add)
                .subscribe(
                        this::onCategoryBookListReady,
                        ToastUtils::showUnhandledErrorToast
                );
    }

    @SuppressLint("CheckResult")
    private void observeConfirmDialogState() {
        ConfirmDeleteDialog
                .sStateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(sShortTermDisposables::add)
                .subscribe(
                        this::onConfirmDeleteCategory,
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

    private void updateRecyclerContent(List<Book> bookList) {
        if (bookList.isEmpty() && isGridLayout()) {
            setRecyclerLayoutToGrid();

        } else if (!bookList.isEmpty() && isLinearLayout()) {
            setRecyclerLayoutToLinear();
        }

        mAdapter.update(bookList);
    }

    private boolean isGridLayout() {
        return mBinding.bookListRecycler.getLayoutManager() instanceof GridLayoutManager;
    }

    private boolean isLinearLayout() {
        return mBinding.bookListRecycler.getLayoutManager() instanceof LinearLayoutManager;
    }

    private void setRecyclerLayoutToGrid() {
        mBinding.bookListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.bookListRecycler.setAdapter(mAdapter);
    }

    private void setRecyclerLayoutToLinear() {
        mBinding.bookListRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mBinding.bookListRecycler.setAdapter(mAdapter);
    }

    // MARK: Listener Methods

    private OnClickListener onAddBookButtonClick = btn -> {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleUtils.CATEGORY_NAME_HOLDER, mCategoryNameHolder);

        mNavController.navigate(R.id.action_categoryBookListFragment_to_addBookFragment, bundle);
    };

    private OnClickListener onEditCategoryClick = btn -> {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleUtils.CATEGORY_NAME_HOLDER, mCategoryNameHolder);

        mNavController.navigate(
                R.id.action_categoryBookListFragment_to_editCategoryFragment,
                bundle
        );
    };

    private OnClickListener onMoreMenuClick = btn -> {
        PopupMenu moreMenu = new PopupMenu(getContext(), btn);

        moreMenu.setOnMenuItemClickListener(this);
        moreMenu.inflate(R.menu.delete_menu);
        moreMenu.show();
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            openConfirmDeleteDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onBookCoverClick(long bookId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BundleUtils.BOOK_ID, bookId);

        mNavController
                .navigate(
                        R.id.action_categoryBookListFragment_to_editBookFragment,
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

    // MARK: Observer Methods

    private void onCategoryBookListReady(List<Book> bookList) {
        mBinding.setIsLoading(false);

        updateRecyclerContent(bookList);
    }

    @SuppressLint("CheckResult")
    private void onConfirmDeleteCategory(boolean confirmDelete) {
        closeConfirmDeleteDialog();

        if (confirmDelete) {
            mBinding.deleteCategoryPleaseWait.setVisibility(View.VISIBLE);

            mViewModel
                    .deleteCategory(mCategoryNameHolder.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(sLongTermDisposables::add)
                    .subscribe(
                            this::onCategoryDeleted,
                            this::onDeleteCategoryFailed
                    );
        }
    }

    private void onCategoryDeleted() {
        closeWholeFragment();
    }

    private void onDeleteCategoryFailed(Throwable t) {
        mBinding.deleteCategoryPleaseWait.setVisibility(GONE);
        ToastUtils.showErrorToast(R.string.failed_to_add_category_error, t);
    }

    private void openConfirmDeleteDialog() {
        onBackPressedConfirmDeleteDialogCallback.setEnabled(true);
        openDialogFragmentByTag(ConfirmDeleteDialog.TAG);
        observeConfirmDialogState();
    }

    private void closeConfirmDeleteDialog() {
        onBackPressedConfirmDeleteDialogCallback.setEnabled(false);
        closeDialogFragmentByTag(ConfirmDeleteDialog.TAG);
    }

    private void onDeleteBookDialogStateChanged(Boolean success) {
        closeDialogFragmentByTag(DeleteBookDialog.TAG);
    }

    private void closeWholeFragment() {
        onBackPressedConfirmDeleteDialogCallback.setEnabled(false);
        navigateBack();
    }
}
