package learn.shendy.ebookshop.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.utils.ResourcesUtils;

public class CategoryNameListAdapter extends RecyclerView.Adapter<CategoryNameListAdapter.CategoryNameHolder> {

    private List<learn.shendy.ebookshop.db.categories.CategoryNameHolder> mCategoryNameHolderList = new ArrayList<>();

    private PublishSubject<learn.shendy.ebookshop.db.categories.CategoryNameHolder> mCategoryNameSubject;

    private LayoutInflater mLayoutInflater;

    public CategoryNameListAdapter(Context context, PublishSubject<learn.shendy.ebookshop.db.categories.CategoryNameHolder> categoryNameSubject) {
        mLayoutInflater = LayoutInflater.from(context);
        mCategoryNameSubject = categoryNameSubject;
    }

    @NonNull
    @Override
    public CategoryNameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.holder_category_name, parent, false);

        return new CategoryNameHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryNameHolder holder, int position) {
        learn.shendy.ebookshop.db.categories.CategoryNameHolder categoryNameHolder = mCategoryNameHolderList.get(position);

        holder.mCategoryNameTV.setText(categoryNameHolder.getName());

        holder.mCategoryNameTV.setOnClickListener(v -> {
            TextView textView = (TextView) v;
            textView.setTextColor(ResourcesUtils.getColor(R.color.bgGradientEndColor));
            textView.setBackgroundColor(ResourcesUtils.getColor(R.color.bgGradientStartColor));
            mCategoryNameSubject.onNext(categoryNameHolder);
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryNameHolderList.size();
    }

    public void update(List<learn.shendy.ebookshop.db.categories.CategoryNameHolder> categoryNameHolderList) {
        mCategoryNameHolderList = categoryNameHolderList;
        notifyDataSetChanged();
    }

    static class CategoryNameHolder extends RecyclerView.ViewHolder {
        TextView mCategoryNameTV;

        CategoryNameHolder(@NonNull View itemView) {
            super(itemView);

            mCategoryNameTV = itemView.findViewById(R.id.category_name_holder_tv);
        }
    }
}
