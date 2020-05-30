package learn.shendy.ebookshop.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.Collections;
import java.util.List;

import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.HolderDashboardCategoryBinding;
import learn.shendy.ebookshop.databinding.HolderEmptyListBinding;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.db.categories.CategoryBookListHolder;
import learn.shendy.ebookshop.db.categories.CategoryNameHolder;
import learn.shendy.ebookshop.views.adapters.BookCoverDisplayHolder.BookCoverClickHandler;
import learn.shendy.ebookshop.views.adapters.DashboardListAdapter.DashboardCategoryHolder.CategoryNameClickHandler;
import learn.shendy.ebookshop.views.fragments.BaseFragment;

public class DashboardListAdapter extends Adapter<ViewHolder> {
    private static final String TAG = "DashboardListAdapter";

    private static int EMPTY_HOLDER = R.layout.holder_empty_list;
    private static int DASHBOARD_CATEGORY_HOLDER = R.layout.holder_dashboard_category;
    private static String CONTENT_TYPE = "category";

    private Context mContext;
    private CategoryNameClickHandler mCategoryNameClickHandler;
    private BookCoverClickHandler mBookCoverClickHandler;
    private LayoutInflater mLayoutInflater;

    private List<CategoryBookListHolder> mDashboardList = Collections.emptyList();

    public DashboardListAdapter(
            Context context, BaseFragment parentFragment
    ) {
        mContext = context;
        mCategoryNameClickHandler = (CategoryNameClickHandler) parentFragment;
        mBookCoverClickHandler = (BookCoverClickHandler) parentFragment;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return mDashboardList.isEmpty() ? EMPTY_HOLDER : DASHBOARD_CATEGORY_HOLDER;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EMPTY_HOLDER) {
            HolderEmptyListBinding binding = HolderEmptyListBinding
                    .inflate(mLayoutInflater, parent, false);
            return new EmptyRecyclerHolder(binding);
        }

        HolderDashboardCategoryBinding binding = HolderDashboardCategoryBinding
                .inflate(mLayoutInflater, parent, false);
        return new DashboardCategoryHolder(binding);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == EMPTY_HOLDER) {
            EmptyRecyclerHolder holder = (EmptyRecyclerHolder) viewHolder;
            holder.mBinding.setContentType(CONTENT_TYPE);
            return;
        }

        CategoryBookListHolder categoryBookListHolder = mDashboardList.get(position);

        DashboardCategoryHolder holder = (DashboardCategoryHolder) viewHolder;

        CategoryNameHolder categoryNameHolder = categoryBookListHolder.getCategoryNameHolder();
        holder.mBinding.setCategoryName(categoryNameHolder.getName());

        List<Book> recentBooks = categoryBookListHolder.getBookList();

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, recentBooks.isEmpty() ? 1 : 2);
        RecentTwoBooksAdapter adapter = new RecentTwoBooksAdapter(mContext, mBookCoverClickHandler);
        holder.mBinding.holderDashboardCategoryTwoRecentBooksRecycler.setLayoutManager(layoutManager);
        holder.mBinding.holderDashboardCategoryTwoRecentBooksRecycler.setAdapter(adapter);
        adapter.setRecentBooks(recentBooks);

        holder.mBinding.holderDashboardCategoryName
                .setOnClickListener(v -> mCategoryNameClickHandler.onCategoryNameClick(categoryNameHolder));
        holder.mBinding.holderDashboardCategoryShowArrow
                .setOnClickListener(v -> mCategoryNameClickHandler.onCategoryNameClick(categoryNameHolder));
    }

    @Override
    public int getItemCount() {
        return mDashboardList.isEmpty() ? 1 : mDashboardList.size();
    }

    public void update(List<CategoryBookListHolder> dashboardList) {
        mDashboardList = dashboardList;
        notifyDataSetChanged();
    }

//    private void onCategoryNameClick(View v, CategoryNameHolder categoryNameOnly) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(BundleUtils.CATEGORY_NAME_ONLY, categoryNameOnly);
//
//        Navigation
//                .findNavController(v)
//                .navigate(
//                        R.id.action_dashboardFragment_to_categoryBookListFragment,
//                        bundle
//                );
//    }

    public static class DashboardCategoryHolder extends ViewHolder {

        public interface CategoryNameClickHandler {
            void onCategoryNameClick(CategoryNameHolder categoryNameHolder);
        }

        final HolderDashboardCategoryBinding mBinding;

        DashboardCategoryHolder(@NonNull HolderDashboardCategoryBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }
}
