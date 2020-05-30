package learn.shendy.ebookshop.db.categories;

import androidx.databinding.BaseObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import learn.shendy.ebookshop.db.books.Book;

public class CategoryBookListHolder extends BaseObservable implements Serializable {
    private static final String TAG = "DashboardCategory";

    private CategoryNameHolder mCategoryNameHolder;
    private List<Book> mBookList = new ArrayList<>();

    public CategoryNameHolder getCategoryNameHolder() {
        return mCategoryNameHolder;
    }

    public void setCategoryNameHolder(CategoryNameHolder categoryNameHolder) {
        this.mCategoryNameHolder = categoryNameHolder;
    }

    public void addBook(Book book) {
        this.mBookList.add(book);
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    @Override
    public String toString() {
        return "CategoryBookListHolder { " +
                "mCategoryNameHolder = " + mCategoryNameHolder +
                ", mBookList = " + mBookList +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryBookListHolder that = (CategoryBookListHolder) o;

        if (!mCategoryNameHolder.equals(that.mCategoryNameHolder)) return false;
        return mBookList.equals(that.mBookList);
    }

    @Override
    public int hashCode() {
        int result = mCategoryNameHolder.hashCode();
        result = 31 * result + mBookList.hashCode();
        return result;
    }
}
