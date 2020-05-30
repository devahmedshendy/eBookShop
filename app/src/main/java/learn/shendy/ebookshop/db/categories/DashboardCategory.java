package learn.shendy.ebookshop.db.categories;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import learn.shendy.ebookshop.db.books.Book;

public class DashboardCategory extends BaseObservable implements Serializable {
    private static final String TAG = "DashboardCategory";

    private long mCategoryId;
    private String mCategoryName;
    private List<Book> mRecentBookList = new ArrayList<>();

    public DashboardCategory() {
    }

    @Bindable
    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        this.mCategoryId = categoryId;
    }

    @Bindable
    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public void addRecentBook(Book book) {
        this.mRecentBookList.add(book);
    }

    public List<Book> getRecentBookList() {
        return mRecentBookList;
    }

    @Override
    public String toString() {
        return "DashboardCategory { " +
                "mCategoryId = " + mCategoryId +
                ", mCategoryName = '" + mCategoryName + '\'' +
                ", mRecentBookList = " + mRecentBookList.size() +
                " } ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardCategory that = (DashboardCategory) o;

        if (mCategoryId != that.mCategoryId) return false;
        if (!mCategoryName.equals(that.mCategoryName)) return false;
        return mRecentBookList.equals(that.mRecentBookList);
    }

    @Override
    public int hashCode() {
        return (int) (mCategoryId ^ (mCategoryId >>> 32));
    }
}
