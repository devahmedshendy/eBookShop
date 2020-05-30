package learn.shendy.ebookshop.db.books;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import learn.shendy.ebookshop.db.base.BaseDAO;

@Dao
public abstract class BookDAO extends BaseDAO<Book> {

    // MARK: FIND Queries

//    @Query("select * from books order by created_at desc")
//    public abstract Observable<List<Book>> findAllObservable();
//
//    @Query("select * from books where category_id == :categoryId order by created_at desc limit 2")
//    public abstract List<Book> findTwoRecentBooksByCategoryId(long categoryId);

    @Query("select * from books where category_id == :categoryId order by created_at desc")
    public abstract Observable<List<Book>> findAllByCategoryId(long categoryId);

    @Query(
            "SELECT b.id as id, b.name as name, b.price as price, b.cover as cover, " +
                    "b.created_at as created_at, b.updated_at as updated_at, " +
                    "c.id as category_id, c.name as category_name " +
            "FROM " +
                    "books as b " +
            "INNER JOIN categories as c ON " +
                    "b.category_id == c.id " +
            "WHERE " +
                    "b.id == :id"
    )
    public abstract Single<BookDetails> findBookDetailsById(long id);

    @Query("SELECT * FROM books where id == :id")
    public abstract Single<Book> findOne(long id);

    @Query("SELECT cover FROM books where category_id == :categoryId")
    public abstract Single<List<String>> findAllBookCoverByCategoryId(long categoryId);

    // MARK: DELETE Queries

    @Query("DELETE FROM books")
    public abstract void deleteAll();

    @Delete
    public abstract Completable delete(Book book);
}
