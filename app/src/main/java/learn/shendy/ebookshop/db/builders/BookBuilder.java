package learn.shendy.ebookshop.db.builders;

import java.util.Date;

import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.books.BookDetails;

public class BookBuilder {
    private static final String TAG = "BookBuilder";

    private long mId;
    private String mName;
    private double mPrice;
    private String mCover;
    private long mCategoryId;
    private Date mCreatedAt;
    private Date mUpdatedAt;

    public Book from(BookDetails other) {
        return new BookBuilder()
                .setId(other.getId())
                .setName(other.getName())
                .setPrice(Double.parseDouble(other.getPrice()))
                .setCover(other.getCover())
                .setCategoryId(other.getCategoryId())
                .setCreatedAt(other.getCreatedAt())
                .setUpdatedAt(other.getUpdatedAt())
                .build();
    }

    public Book build() {
        Book book = new Book();

        book.setId(mId);
        book.setName(mName);
        book.setPrice(mPrice);
        book.setCover(mCover);
        book.setCategoryId(mCategoryId);
        book.setCreatedAt(mCreatedAt);
        book.setUpdatedAt(mUpdatedAt);

        return book;
    }

    public BookBuilder setId(long id) {
        mId = id;
        return this;
    }

    public BookBuilder setName(String name) {
        mName = name;
        return this;
    }

    public BookBuilder setPrice(double price) {
        mPrice = price;
        return this;
    }

    public BookBuilder setCover(String cover) {
        mCover = cover;
        return this;
    }

    public BookBuilder setCategoryId(long categoryId) {
        mCategoryId = categoryId;
        return this;
    }

    public BookBuilder setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
        return this;
    }

    public BookBuilder setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
        return this;
    }
}
