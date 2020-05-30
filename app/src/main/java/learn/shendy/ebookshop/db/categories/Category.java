package learn.shendy.ebookshop.db.categories;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import learn.shendy.ebookshop.BR;
import learn.shendy.ebookshop.db.base.BaseEntity;

@Entity(
        tableName = "categories",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class Category extends BaseEntity {

    // MARK: Properties

    @ColumnInfo(name = "name", collate = ColumnInfo.NOCASE)
    private String mName;

    @ColumnInfo(name = "description")
    private String mDescription;

    // MARK: Constructors

    public Category() {
        mName = "";
        mDescription = "";
    }

    @Ignore
    public Category(long id, @NonNull String name, String description) {
        setId(id);
        this.mName = name;
        this.mDescription = description;
    }

    @Ignore
    public Category(@NonNull String name, String description) {
        this.mName = name;
        this.mDescription = description;
    }

    // MARK: Getters/Setters

    @NonNull
    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        this.mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
        notifyPropertyChanged(BR.description);
    }

    // MARK: Overridden Methods

    @Override
    public String toString() {
        return "Category { " +
                "name = '" + mName + '\'' +
                ", description = '" + mDescription + '\'' +
                " } ";
    }


    public Category clone() {
        Category category = new Category();

        category.setId(getId());
        category.setName(getName());
        category.setDescription(getDescription());
        category.setCreatedAt(getCreatedAt());
        category.setUpdatedAt(getUpdatedAt());

        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!mName.equalsIgnoreCase(category.mName)) return false;
        return mDescription.equalsIgnoreCase(category.mDescription);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
