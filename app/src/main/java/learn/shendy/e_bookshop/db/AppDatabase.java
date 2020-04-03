package learn.shendy.e_bookshop.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import learn.shendy.e_bookshop.R;
import learn.shendy.e_bookshop.db.categories.Category;
import learn.shendy.e_bookshop.db.categories.CategoryDAO;
import learn.shendy.e_bookshop.util.AsyncUtils;
import learn.shendy.e_bookshop.util.ResourcesUtils;

@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // MARK: DAOs

    public abstract CategoryDAO mCategoryDAO();

    // MARK: Static Members Defining Singleton of This Class

    private static AppDatabase INSTANCE;
    private static boolean CREATE_DEFAULTS_ENTRIES = true;

    public static AppDatabase getSingletonInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "db")
                    .addCallback(sOnCreateRoomCallback)
                    .build();
        }

        return INSTANCE;
    }

    // MARK: Room Callbacks - Static Methods

    private static RoomDatabase.Callback sOnCreateRoomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            if (!CREATE_DEFAULTS_ENTRIES) return;

            createDefaultCategories();
        }
    };

    private static void createDefaultCategories() {
        AsyncUtils.doDatabaseInBackground(() -> {
            CategoryDAO categoryDAO = INSTANCE.mCategoryDAO();

            categoryDAO.deleteAll();

            String categoryName = ResourcesUtils.getString(R.string.action_and_adventure);
            String categoryDescription = ResourcesUtils.getString(R.string.default_category_description, categoryName);
            Category category = new Category(categoryName, categoryDescription);
            categoryDAO.insert(category);

            categoryName = ResourcesUtils.getString(R.string.science_fiction);
            categoryDescription = ResourcesUtils.getString(R.string.default_category_description, categoryName);
            category = new Category(categoryName, categoryDescription);
            categoryDAO.insert(category);

            categoryName = ResourcesUtils.getString(R.string.fantasy);
            categoryDescription = ResourcesUtils.getString(R.string.default_category_description, categoryName);
            category = new Category(categoryName, categoryDescription);
            categoryDAO.insert(category);
        });
    }
}
