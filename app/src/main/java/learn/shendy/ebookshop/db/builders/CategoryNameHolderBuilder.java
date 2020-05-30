package learn.shendy.ebookshop.db.builders;

import learn.shendy.ebookshop.db.categories.CategoryLeftJoinBook;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;

public class CategoryNameHolderBuilder {

    private long mId;
    private String mName;

    public static CategoryNameHolder from(CategoryLeftJoinBook o) {
        return new CategoryNameHolderBuilder()
                .setId(o.getCategoryId())
                .setName(o.getCategoryName())
                .build();
    }

    public CategoryNameHolderBuilder setId(long id) {
        mId = id;
        return this;
    }

    public CategoryNameHolderBuilder setName(String name) {
        mName = name;
        return this;
    }

    public CategoryNameHolder build() {
        CategoryNameHolder o = new CategoryNameHolder();

        o.setId(mId);
        o.setName(mName);

        return o;
    }
}
