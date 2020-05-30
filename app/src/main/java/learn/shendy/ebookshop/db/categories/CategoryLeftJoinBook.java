package learn.shendy.ebookshop.db.categories;

import androidx.room.ColumnInfo;

public class CategoryLeftJoinBook {
    @ColumnInfo(name = "category_id")
    private long mCategoryId;

    @ColumnInfo(name = "category_name")
    private String mCategoryName;

    @ColumnInfo(name = "book_id")
    private long mBookId;

    @ColumnInfo(name = "book_name")
    private String mBookName;

    @ColumnInfo(name = "book_price")
    private double mBookPrice;

    @ColumnInfo(name = "book_cover")
    private String mBookCoverURL;

    public CategoryLeftJoinBook() {
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long bookId) {
        mBookId = bookId;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        mBookName = bookName;
    }

    public double getBookPrice() {
        return mBookPrice;
    }

    public void setBookPrice(double bookPrice) {
        mBookPrice = bookPrice;
    }

    public String getBookCoverURL() {
        return mBookCoverURL;
    }

    public void setBookCoverURL(String bookCoverURL) {
        mBookCoverURL = bookCoverURL;
    }

    @Override
    public String toString() {
        return "CategoryLeftJoinBook {" +
                "mCategoryId = " + mCategoryId +
                ", mCategoryName = '" + mCategoryName + '\'' +
                ", mBookName = '" + mBookName + '\'' +
                ", mBookPrice = '" + mBookPrice + '\'' +
                ", mBookCoverURL = '" + mBookCoverURL + '\'' +
                " }";
    }
}
