package learn.shendy.ebookshop.db.categories;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import learn.shendy.ebookshop.db.base.BaseDAO;

@Dao
public abstract class CategoryDAO extends BaseDAO<Category> {

    // MARK: Find Queries

    @Query("SELECT * FROM categories order by created_at desc")
    public abstract Single<List<Category>> findAll();

    @Query(
        "SELECT c.id as category_id, c.name as category_name, " +
                "b.id as book_id, b.name as book_name, b.price as book_price, " +
                "b.cover as book_cover " +
        "FROM " +
            "categories as c " +
        "LEFT JOIN books as b ON " +
            "c.id = b.category_id " +
        "ORDER BY " +
            "c.created_at desc, " +
            "c.id asc, " +
            "b.updated_at desc"
    )
    public abstract Single<List<CategoryLeftJoinBook>> leftJoinCategoryAndBook();

    @Query("SELECT id, name from categories WHERE name LIKE :searchText")
    public abstract Single<List<CategoryNameHolder>> findNameListLike(String searchText);

    public Single<Category> findDistinctOne(long entityId) {
        return findOne(entityId);
    }

    @Query("SELECT * FROM categories where id = :id")
    public abstract Single<Category> findOne(long id);

    // MARK: DELETE Queries

    @Query("DELETE FROM categories")
    public abstract void deleteAll();

    @Query("DELETE FROM categories WHERE id == :id")
    public abstract Completable deleteOne(long id);
}
