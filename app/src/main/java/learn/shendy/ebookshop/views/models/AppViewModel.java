package learn.shendy.ebookshop.views.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.db.AppRepository;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.books.BookDetails;
import learn.shendy.ebookshop.db.categories.Category;
import learn.shendy.ebookshop.db.categories.CategoryBookListHolder;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.ToastUtils;

public class AppViewModel extends AndroidViewModel {
    private static final String TAG = "AppViewModel";

//    private BehaviorSubject<Boolean> mDashboardListState = BehaviorSubject.create();
    private BehaviorSubject<List<CategoryBookListHolder>> mDashboardList = BehaviorSubject.create();
    private BehaviorSubject<List<Book>> mCategoryBookList = BehaviorSubject.create();

    private AppRepository mAppRepository;

    // Data used by CategoryFragment and EditCategoryFragment
    private List<Book> mBookList = Collections.emptyList();

    // Data used by DashboardFragment
//    private List<CategoryBookListHolder> mDashboardList = Collections.emptyList();

    public AppViewModel(@NonNull Application application) {
        super(application);

        mAppRepository = new AppRepository(application);

        reloadDashboardList();
    }

    // MARK: Model Observable Methods

    public Observable<List<CategoryBookListHolder>> observeDashboardListState() {
        return mDashboardList
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Book>> observeCategoryBookListState() {
        return mCategoryBookList
                .subscribeOn(Schedulers.io());
    }

    // MARK: Model Getter Methods

    public List<Book> getBookList() {
        return mBookList;
    }

    // MARK: Model Logic Methods

    @SuppressLint("CheckResult")
    public Single<Category> findCategory(long categoryId) {
        return mAppRepository
                .findCategoryById(categoryId);
    }

    @SuppressLint("CheckResult")
    public Observable<List<Book>> findCategoryBookList(long categoryId) {
        return mAppRepository
                .findBookListByCategoryId(categoryId);
    }

    @SuppressLint("CheckResult")
    public void reloadDashboardList() {
        mAppRepository
                .findDashboardList()
                .subscribe(
                        mDashboardList::onNext,
                        this::onFindDashboardListFailed
                );
    }

    public Single<List<CategoryNameHolder>> searchCategoryNameList(String search) {
        return mAppRepository
                .searchCategoryNameList(search);
    }

    public Single<BookDetails>findBookDetails(long id) {
        return mAppRepository
                .findBookDetailsById(id);
    }

    @SuppressLint("CheckResult")
    public Completable addBook(Book newBook, Bitmap coverBitmap) {
        return mAppRepository
                .addBook(newBook, coverBitmap)
                .doOnComplete(this::reloadDashboardList);
    }

    public Completable updateBook(Book book, Bitmap coverBitmap) {
        return mAppRepository
                .updateBook(book, coverBitmap)
                .doOnComplete(this::reloadDashboardList);
    }

    @SuppressLint("CheckResult")
    public Completable deleteBook(long id) {
        return mAppRepository
                .deleteBook(id)
                .doOnComplete(this::reloadDashboardList);
    }

    @SuppressLint("CheckResult")
    public Completable addCategory(Category newCategory) {
        return mAppRepository
                .addCategory(newCategory)
                .doOnComplete(this::reloadDashboardList);
    }

    public Completable updateCategory(Category category) {
        return mAppRepository
                .updateCategory(category);
    }

    @SuppressLint("CheckResult")
    public Completable deleteCategory(long id) {
        return mAppRepository
                .deleteCategory(id)
                .doOnComplete(this::reloadDashboardList);
    }

    // MARK: Observer Listeners

    private void onFindDashboardListFailed(Throwable t) {
        ToastUtils.showErrorToast(R.string.failed_to_load_dahsboard_list, t);
    }

    private void onFindCategoryBookListFailed(Throwable t) {
        ToastUtils.showErrorToast(R.string.failed_to_load_category_book_list, t);
    }
}
