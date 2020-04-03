package learn.shendy.e_bookshop.db.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import learn.shendy.e_bookshop.db.AppDatabase;

public class CategoryRepository {

    private CategoryDAO mCategoryDAO;

    public CategoryRepository(@NonNull Application application) {
        mCategoryDAO = AppDatabase.getSingletonInstance(application).mCategoryDAO();
    }

    public LiveData<List<Category>> findAll() {
        return mCategoryDAO.findAll();
    }
}
