package learn.shendy.e_bookshop.db.base;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

import learn.shendy.e_bookshop.util.DateUtils;

public class BaseEntity extends BaseObservable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @TypeConverters(DateUtils.class)
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    private Date createdAt;

    @TypeConverters(DateUtils.class)
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    private Date updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
