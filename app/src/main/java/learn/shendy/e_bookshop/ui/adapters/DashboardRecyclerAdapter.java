package learn.shendy.e_bookshop.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.Collections;
import java.util.List;

import learn.shendy.e_bookshop.R;
import learn.shendy.e_bookshop.databinding.RecentCategoriesHolderBinding;
import learn.shendy.e_bookshop.db.categories.Category;

public class DashboardRecyclerAdapter extends Adapter<DashboardRecyclerAdapter.RecentCategoriesHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Category> mCategoryList = Collections.emptyList();

    public DashboardRecyclerAdapter(@NonNull Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecentCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentCategoriesHolderBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.recent_categories_holder, parent, false);
        return new RecentCategoriesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentCategoriesHolder holder, int position) {
        Category category = mCategoryList.get(position);

        holder.mBinding.setCategory(category);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void setCategoryList(List<Category> categoryList) {
        mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    static class RecentCategoriesHolder extends ViewHolder {

        final RecentCategoriesHolderBinding mBinding;

        RecentCategoriesHolder(@NonNull RecentCategoriesHolderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
