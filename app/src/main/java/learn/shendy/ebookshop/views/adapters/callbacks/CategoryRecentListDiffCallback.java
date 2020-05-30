package learn.shendy.ebookshop.views.adapters.callbacks;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import learn.shendy.ebookshop.db.categories.DashboardCategory;

public class CategoryRecentListDiffCallback extends DiffUtil.Callback {

    private List<DashboardCategory> mOldList;
    private List<DashboardCategory> mNewList;

    public CategoryRecentListDiffCallback(List<DashboardCategory> oldList, List<DashboardCategory> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        DashboardCategory oldItem = mOldList.get(oldItemPosition);
        DashboardCategory newItem = mNewList.get(newItemPosition);

        return oldItem.getCategoryId() == newItem.getCategoryId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        DashboardCategory oldItem = mOldList.get(oldItemPosition);
        DashboardCategory newItem = mNewList.get(newItemPosition);

        return oldItem.equals(newItem);
    }
}
