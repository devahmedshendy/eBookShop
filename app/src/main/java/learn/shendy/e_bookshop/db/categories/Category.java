package learn.shendy.e_bookshop.db.categories;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import learn.shendy.e_bookshop.BR;
import learn.shendy.e_bookshop.db.base.BaseEntity;

@Entity(tableName = "categories")
public class Category extends BaseEntity {

    // MARK: Properties

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    // MARK: Constructors

    public Category() {
        name = "";
        description = "";
    }

    @Ignore
    public Category(@NonNull String name, String description) {
        this.name = name;
        this.description = description;
    }

    // MARK: Getters/Setters

    @NonNull
    @Bindable
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    // MARK: Overridden Methods

    @Override
    public String toString() {
        return "Category { " +
                "name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                " } ";
    }
}
