package learn.shendy.ebookshop.db.base;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

import learn.shendy.ebookshop.utils.RoomConverters;

public class BaseEntity extends BaseObservable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @TypeConverters(RoomConverters.class)
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    private Date mCreatedAt;

    @TypeConverters(RoomConverters.class)
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    private Date mUpdatedAt;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.mCreatedAt = createdAt;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.mUpdatedAt = updatedAt;
    }
}
