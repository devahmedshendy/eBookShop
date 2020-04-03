package learn.shendy.e_bookshop.ui.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import learn.shendy.e_bookshop.db.categories.Category;
import learn.shendy.e_bookshop.db.categories.CategoryRepository;

public class DashboardViewModel extends AndroidViewModel {

    private LiveData<List<Category>> mCategoryList;
//    private LiveData<List<C>>
    private CategoryRepository mCategoryRepository;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
        mCategoryList = mCategoryRepository.findAll();
    }

    public LiveData<List<Category>> findCategoryList() {
        return mCategoryList;
    }

//    public LiveData<List<Category>> findCategoriesJoinedWithTwoRecentBooks() {
//        return null;
//    }
}
