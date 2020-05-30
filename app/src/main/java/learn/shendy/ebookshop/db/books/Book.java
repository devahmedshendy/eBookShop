package learn.shendy.ebookshop.db.books;

import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import learn.shendy.ebookshop.BR;
import learn.shendy.ebookshop.db.base.BaseEntity;
import learn.shendy.ebookshop.db.categories.Category;

import static androidx.room.ForeignKey.CASCADE;

//import learn.shendy.e_bookshop.BR;

@Entity(
        tableName = "books",
        indices = {@Index(value = {"name"}, unique = true)},
        foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "category_id", onDelete = CASCADE)
)
public class Book extends BaseEntity {
    private static final String TAG = "Book";

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "price")
    private double mPrice;

    @ColumnInfo(name = "cover")
    private String mCover;

    @ColumnInfo(name = "category_id")
    private long mCategoryId;

    public Book() {  }

    @Ignore
    public Book(String name, double price, String cover, long categoryId) {
        this.mName = name;
        this.mPrice = price;
        this.mCategoryId = categoryId;
        this.mCover = cover;
    }

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public double getPrice() {
        return mPrice;
    }

    public String getPriceAsString() {
        return String.format("%s$", mPrice);
    }

    public void setPrice(double price) {
        this.mPrice = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        this.mCover = cover;
        notifyPropertyChanged(BR.cover);
    }

    @Bindable
    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        this.mCategoryId = categoryId;
        notifyPropertyChanged(BR.categoryId);
    }

    @Override
    public String toString() {
        return "Book { " +
                " mName = '" + mName + '\'' +
                ", mPrice = " + mPrice +
                ", mCover = '" + mCover + '\'' +
                ", mCategoryId = " + mCategoryId +
                ", mCreatedAt = " + getCreatedAt() +
                ", mUpdatedAt = " + getUpdatedAt() +
                " } ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book that = (Book) o;

        if (Double.compare(that.mPrice, mPrice) != 0) return false;
        if (mCategoryId != that.mCategoryId) return false;
        if (!mName.equalsIgnoreCase(that.mName)) return false;
        return mCover.equals(that.mCover);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
