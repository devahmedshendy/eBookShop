package learn.shendy.ebookshop.db.books;

import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;

import learn.shendy.ebookshop.BR;
import learn.shendy.ebookshop.db.base.BaseEntity;

public class BookDetails extends BaseEntity {
    private static final String TAG = "BookDetails";

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "price")
    private String mPrice;

    @ColumnInfo(name = "cover")
    private String mCover;

    @ColumnInfo(name = "category_id")
    private long mCategoryId;

    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    public BookDetails() {
    }

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
        notifyPropertyChanged(BR.cover);
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
    }

    @Bindable
    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
        notifyPropertyChanged(BR.categoryName);
    }

    public BookDetails clone() {
        BookDetails bookDetails = new BookDetails();

        bookDetails.setId(getId());
        bookDetails.setName(getName());
        bookDetails.setPrice(getPrice());
        bookDetails.setCover(getCover());
        bookDetails.setCategoryId(getCategoryId());
        bookDetails.setCategoryName(getCategoryName());
        bookDetails.setCreatedAt(getCreatedAt());
        bookDetails.setUpdatedAt(getUpdatedAt());

        return bookDetails;
    }

    @Override
    public String toString() {
        return "BookDetails { " +
                "mId = '" + getId() + '\'' +
                ", mName = '" + mName + '\'' +
                ", mPrice = '" + mPrice + '\'' +
                ", mCover = '" + mCover + '\'' +
                ", mCategoryId = " + mCategoryId +
                ", mCategoryName = '" + mCategoryName + '\'' +
                ", mCreatedAt = " + getCreatedAt() +
                ", mUpdatedAt = '" + getUpdatedAt() + '\'' +
                " }";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        BookDetails that = (BookDetails) other;

        if (getId() != that.getId()) return false;
        if (mCategoryId != that.mCategoryId) return false;
        if (!mName.equals(that.mName)) return false;
        if (!mPrice.equals(that.mPrice)) return false;
        if (!mCover.equals(that.mCover)) return false;
        return mCategoryName.equals(that.mCategoryName);
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (int) (mCategoryId ^ (mCategoryId >>> 32));
        return result;
    }
}
