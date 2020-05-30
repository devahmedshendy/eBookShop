package learn.shendy.ebookshop.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.HolderBookCoverDisplayBinding;
import learn.shendy.ebookshop.databinding.HolderEmptyDashboardCategoryBinding;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.views.adapters.BookCoverDisplayHolder.BookCoverClickHandler;

public class RecentTwoBooksAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "RecentTwoBooksAdapter";

    private static int EMPTY_HOLDER = R.layout.holder_empty_dashboard_category;
    private static int BOOK_DISPLAY_HOLDER = R.layout.holder_book_cover_display;

    private Context mContext;
    private BookCoverClickHandler mClickHandler;
    private LayoutInflater mLayoutInflater;
    private List<Book> mRecentTwoBooks = new ArrayList<>();

    public RecentTwoBooksAdapter(Context context, BookCoverClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return mRecentTwoBooks.isEmpty() ? EMPTY_HOLDER : BOOK_DISPLAY_HOLDER;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EMPTY_HOLDER) {
            HolderEmptyDashboardCategoryBinding binding = HolderEmptyDashboardCategoryBinding
                .inflate(mLayoutInflater, parent, false);

            return new EmptyDashboardCategory(binding);
        }

        HolderBookCoverDisplayBinding binding = HolderBookCoverDisplayBinding
                .inflate(mLayoutInflater, parent, false);

        return new BookCoverDisplayHolder(binding);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() != EMPTY_HOLDER) {
            Book book = mRecentTwoBooks.get(position);

            BookCoverDisplayHolder holder = (BookCoverDisplayHolder) viewHolder;

            holder.mBinding.setBook(book);

            if (position % 2 != 0) {
                holder.mBinding.setIsRightSide(true);
            }


//            holder.mBinding.bookCoverIv.setOnClickListener(v -> {
//                AppViewModel
//                        .sCoverDisplayClickObservable
//                        .onNext(book.getId());
//            });

//            int containerId = ViewCompat.generateViewId();
//            holder.mBinding.holderBookCoverDisplayDialogContainer.setId(containerId);
//            holder.mBinding.holderBookCoverDisplayDialogContainer.setTag(book.getId());
//            holder.mBinding.bookCoverIv.setOnLongClickListener(v -> {
//                AppViewModel
//                        .sCoverDisplayLongClickObservable
//                        .onNext(containerId);
//                return true;
//            });

            holder.mBinding.bookCoverIv.setOnClickListener(v ->
                    mClickHandler.onBookCoverClick(book.getId())
            );

            int containerId = ViewCompat.generateViewId();
            holder.mBinding.holderBookCoverDisplayDialogContainer.setId(containerId);
            holder.mBinding.holderBookCoverDisplayDialogContainer.setTag(book.getId());
            holder.mBinding.bookCoverIv.setOnLongClickListener(v -> {
                mClickHandler.onBookCoverLongClick(containerId);
                return true;
            });

//            InternalStorageUtils
//                    .loadBookCover(book.getCover())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                            holder.mBinding.bookCoverIv::setImageBitmap,
//                            throwable -> {
//                                holder.mBinding.bookCoverIv.setImageResource(R.drawable.book_cover_unavailable_placeholder);
//                            }
//                    );
        }
    }

    @Override
    public int getItemCount() {
        return mRecentTwoBooks.isEmpty() ? 1 : mRecentTwoBooks.size();
    }

    void setRecentBooks(List<Book> books) {
        mRecentTwoBooks = books;
        notifyDataSetChanged();
    }

    static class EmptyDashboardCategory extends ViewHolder {
        HolderEmptyDashboardCategoryBinding mBinding;

        public EmptyDashboardCategory(HolderEmptyDashboardCategoryBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
