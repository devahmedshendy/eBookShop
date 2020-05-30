package learn.shendy.ebookshop.db;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.books.BookDAO;
import learn.shendy.ebookshop.db.books.BookDetails;
import learn.shendy.ebookshop.db.builders.BookBuilder;
import learn.shendy.ebookshop.db.builders.CategoryNameHolderBuilder;
import learn.shendy.ebookshop.db.categories.Category;
import learn.shendy.ebookshop.db.categories.CategoryBookListHolder;
import learn.shendy.ebookshop.db.categories.CategoryDAO;
import learn.shendy.ebookshop.db.categories.CategoryLeftJoinBook;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.utils.InternalStorageUtils;

public class AppRepository {
    private static final String TAG = "AppRepository";

    private Scheduler mIOScheduler = Schedulers.io();

    private Application mApplication;

    private BookDAO mBookDAO;
    private CategoryDAO mCategoryDAO;

    public AppRepository(Application application) {
        mApplication = application;

        mBookDAO = AppDatabase.getSingletonInstance(application).mBookDAO();
        mCategoryDAO = AppDatabase.getSingletonInstance(application).mCategoryDAO();
    }

    public Single<Book> findBookById(long id) {
        return mBookDAO
                .findOne(id)
                .subscribeOn(mIOScheduler);
    }

    public Single<BookDetails> findBookDetailsById(long id) {
        return mBookDAO
                .findBookDetailsById(id)
                .delay(R.integer.fragment_navigation_medium_anim_time * 2, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Single<Category> findCategoryById(long id) {
        return mCategoryDAO
                .findDistinctOne(id)
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Completable addBook(Book newBook, Bitmap coverBitmap) {
        return InternalStorageUtils
                .storeBookCoverBitmap(coverBitmap, newBook.getCover())
                .andThen(mBookDAO.insertOneAsync(newBook))
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Completable deleteBook(long id) {
        return findBookById(id)
                .flatMapCompletable(this::deleteBookAndCover)
                .subscribeOn(mIOScheduler);

    }

    public Completable updateBook(Book book, Bitmap coverBitmap) {
        return InternalStorageUtils
                .storeBookCoverBitmap(coverBitmap, book.getCover())
                .andThen(mBookDAO.updateOne(book))
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Completable deleteBookAndCover(Book book) {
        return mBookDAO
                .delete(book)
                .andThen(InternalStorageUtils.deleteFile(book.getCover()))
                .subscribeOn(mIOScheduler);
    }

    public Completable addCategory(Category newCategory) {
        return mCategoryDAO
                .insertOneAsync(newCategory)
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Completable updateCategory(Category category) {
        return mCategoryDAO
                .updateOne(category)
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Completable deleteCategory(long id) {
        return Completable
                .fromObservable(
                        mBookDAO
                                .findAllBookCoverByCategoryId(id)
                                .flattenAsObservable(result -> result)
                                .map(InternalStorageUtils::deleteFile)
                )
                .andThen(mCategoryDAO.deleteOne(id))
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

    public Single<List<CategoryBookListHolder>> findDashboardList() {
        return mCategoryDAO
                .leftJoinCategoryAndBook()
                .flattenAsObservable(result -> result)
                .groupBy(CategoryLeftJoinBook::getCategoryId)
                .concatMapSingle(group -> groupCategoryWithBookList(group, true))
//                .concatMapSingle(
//                        group -> group.reduce(
//                            new CategoryBookListHolder(),
//                            (categoryWithBookList, categoryLeftJoinBook) -> {
//                                categoryWithBookList.setCategoryNameHolder(
//                                        CategoryNameHolderBuilder.from(categoryLeftJoinBook)
//                                );
//
//                                if (categoryWithBookList.getBookList().size() == 2) return categoryWithBookList;
//                                if (categoryLeftJoinBook.getBookName() == null) return categoryWithBookList;
//
//                                Book book = new BookBuilder()
//                                        .setId(categoryLeftJoinBook.getBookId())
//                                        .setName(categoryLeftJoinBook.getBookName())
//                                        .setPrice(categoryLeftJoinBook.getBookPrice())
//                                        .setCover(categoryLeftJoinBook.getBookCoverURL())
//                                        .build();
//
//                                categoryWithBookList.addBook(book);
//
//                                return categoryWithBookList;
//                            }
//                        )
//                )
                .toList()
                .subscribeOn(mIOScheduler);
    }

    private Single<CategoryBookListHolder> groupCategoryWithBookList(
            GroupedObservable<Long, CategoryLeftJoinBook> group,
            boolean onlyTwoRecent
    ) {
        return group.reduce(
                new CategoryBookListHolder(),
                (accumulator, next) -> {

                    accumulator.setCategoryNameHolder(
                            CategoryNameHolderBuilder.from(next)
                    );

                    if (next.getBookName() == null) return accumulator;

                    if (onlyTwoRecent) {
                        if (accumulator.getBookList().size() == 2) return accumulator;
                    }

                    Book book = new BookBuilder()
                            .setId(next.getBookId())
                            .setName(next.getBookName())
                            .setPrice(next.getBookPrice())
                            .setCover(next.getBookCoverURL())
                            .build();

                    accumulator.addBook(book);

                    return accumulator;
                }
        );
    }

    public Single<List<CategoryNameHolder>> searchCategoryNameList(String search) {
        return mCategoryDAO
                .findNameListLike("%" + search + "%")
                .subscribeOn(mIOScheduler);
    }

    public Observable<List<Book>> findBookListByCategoryId(long id) {
        return mBookDAO
                .findAllByCategoryId(id)
                .delay(250, TimeUnit.MILLISECONDS)
                .subscribeOn(mIOScheduler);
    }

//    public Observable<List<CategoryLeftJoinBook>> leftJoinCategoryAndBook() {
//        return mCategoryDAO.leftJoinCategoryAndBook()
//                .subscribeOn(mIOScheduler);
//    }
}
