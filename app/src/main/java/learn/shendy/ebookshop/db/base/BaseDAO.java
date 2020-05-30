package learn.shendy.ebookshop.db.base;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public abstract class BaseDAO<T extends BaseEntity> {

    // MARK: Insert Queries

    public long insertSync(T entity) {
        Date now = new Date();

        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return insertWithTimestamp(entity).blockingGet();
    }

    public Completable insertOneAsync(T entity) {
        Date now = new Date();

        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return insertWithTimestamp(entity).ignoreElement();
    }

    @Insert
    public abstract Single<Long> insertWithTimestamp(T entity);

    // MARK: Update Queries

    public Completable updateOne(T entity) {
        Date now = new Date();

        entity.setUpdatedAt(now);

        return updateWithTimestamp(entity);
    }

    @Update
    public abstract Completable updateWithTimestamp(T entity);

    // MARK: Delete Queries


}
