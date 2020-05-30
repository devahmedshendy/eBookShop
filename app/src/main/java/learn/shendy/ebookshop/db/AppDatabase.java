package learn.shendy.ebookshop.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.books.BookDAO;
import learn.shendy.ebookshop.db.builders.BookBuilder;
import learn.shendy.ebookshop.db.categories.Category;
import learn.shendy.ebookshop.db.categories.CategoryDAO;
import learn.shendy.ebookshop.utils.ResourcesUtils;

@Database(entities = {Category.class, Book.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";

    // MARK: DAOs

    public abstract BookDAO mBookDAO();
    public abstract CategoryDAO mCategoryDAO();

    // MARK: Static Members Defining Singleton of This Class

    private static AppDatabase INSTANCE;

    static AppDatabase getSingletonInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context, AppDatabase.class, "db")
                    .build();
        }

        return INSTANCE;
    }

    // MARK: Room Callbacks - Static Methods

//    private static RoomDatabase.Callback sOnCreateRoomCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//
//            if (CREATE_DEFAULTS_ENTRIES) {
//                loadDefaultData();
//            }
//        }
//    };

    static Completable loadDefaultDataIntoDatabase() {
        return Completable.defer(() -> {
            try {
                clearTables();
                loadActionAndAdventureCategoryAndItsBooks();
                loadScienceFictionCategoryAndItsBooks();
                loadFantasyCategoryAndItsBooks();

                return CompletableObserver::onComplete;
            } catch (Exception e) {
                Log.e(TAG, "loadDefaultData: " + e.getMessage(), e);
                return Completable.error(e);
            }
        });
    }

    private static void clearTables() {
        INSTANCE.mCategoryDAO().deleteAll();
        INSTANCE.mBookDAO().deleteAll(); // No need for it, but keep it for now
    }

    private static void loadActionAndAdventureCategoryAndItsBooks() {
        long categoryId = insertCategory(
                R.string.action_and_adventure_category,
                R.string.default_category_description
        );

        insertBook(
                categoryId,
                R.string.action_and_adventure_book1_name,
                R.string.action_and_adventure_book1_price,
                R.string.action_and_adventure_book1_cover
        );

        insertBook(
                categoryId,
                R.string.action_and_adventure_book2_name,
                R.string.action_and_adventure_book2_price,
                R.string.action_and_adventure_book2_cover
        );
    }

    private static void loadScienceFictionCategoryAndItsBooks() {
        long categoryId = insertCategory(
                R.string.science_fiction_category,
                R.string.default_category_description
        );

        insertBook(
                categoryId,
                R.string.science_fiction_book1_name,
                R.string.science_fiction_book1_price,
                R.string.science_fiction_book1_cover
        );

        insertBook(
                categoryId,
                R.string.science_fiction_book2_name,
                R.string.science_fiction_book2_price,
                R.string.science_fiction_book2_cover
        );
    }

    private static void loadFantasyCategoryAndItsBooks() {
        long categoryId = insertCategory(
                R.string.fantasy_category,
                R.string.default_category_description
        );

        insertBook(
                categoryId,
                R.string.fantasy_book1_name,
                R.string.fantasy_book1_price,
                R.string.fantasy_book1_cover
        );

//        insertBook(
//                categoryId,
//                R.string.fantasy_book2_name,
//                R.string.fantasy_book2_price,
//                R.string.fantasy_book2_cover
//        );
    }

    private static long insertCategory(int nameResId, int descriptionResId) {
        String categoryName = getResourceString(nameResId);
        String categoryDescription = getResourceString(descriptionResId, categoryName);
        Category category = new Category(categoryName, categoryDescription);

        return INSTANCE.mCategoryDAO().insertSync(category);
    }

    private static void insertBook(long categoryId, int nameResId, int priceResId, int coverResId) {
        String bookName = getResourceString(nameResId);
        double bookPrice = getStringToDouble(priceResId);

        Book book = new BookBuilder()
                .setName(bookName)
                .setPrice(bookPrice)
                .setCover(getResourceString(coverResId))
                .setCategoryId(categoryId)
                .build();
        long id = INSTANCE.mBookDAO().insertSync(book);
    }

    private static String getResourceString(int resId) {
        return ResourcesUtils.getString(resId);
    }

    private static String getResourceString(int resId, Object... formatArgs) {
        return ResourcesUtils.getString(resId, formatArgs);
    }

    private static Double getStringToDouble(int resId) {
        return Double.valueOf(ResourcesUtils.getString(resId));
    }
}
