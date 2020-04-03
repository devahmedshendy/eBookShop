package learn.shendy.e_bookshop.db.categories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import learn.shendy.e_bookshop.db.base.BaseDAO;

@Dao
public abstract class CategoryDAO extends BaseDAO<Category> {

    @Query("SELECT * FROM categories order by created_at desc")
    public abstract LiveData<List<Category>> findAll();

}
