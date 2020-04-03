package learn.shendy.e_bookshop.db.base;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;

@Dao
public abstract class BaseDAO<T extends BaseEntity> {

    @Insert
    public abstract long insertWithTimestamp(T entity);

    public long insert(T entity) {
        Date now = new Date();

        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return insertWithTimestamp(entity);
    }

    @Query("DELETE FROM categories")
    public abstract void deleteAll();
}
