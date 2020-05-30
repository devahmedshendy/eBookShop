package learn.shendy.ebookshop.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.util.Collections;
import java.util.List;

import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.databinding.HolderBookCoverDisplayBinding;
import learn.shendy.ebookshop.databinding.HolderEmptyListBinding;
import learn.shendy.ebookshop.db.books.Book;
import learn.shendy.ebookshop.views.adapters.BookCoverDisplayHolder.BookCoverClickHandler;
import learn.shendy.ebookshop.views.fragments.BaseFragment;

import static androidx.recyclerview.widget.RecyclerView.Adapter;
import static androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CategoryBookListAdapter extends Adapter<ViewHolder> {
    private static final String TAG = "CategoryBookListAdapter";
    
    private static int EMPTY_HOLDER = R.layout.holder_empty_list;
    private static int BOOK_DISPLAY_HOLDER = R.layout.holder_book_cover_display;
    private static String CONTENT_TYPE = "book";


    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private BookCoverClickHandler mClickHandler;

    private List<Book> mBookList = Collections.emptyList();

    public CategoryBookListAdapter(@NonNull Context context, BaseFragment parentFragment) {
        mContext = context;
        mClickHandler = (BookCoverClickHandler) parentFragment;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return mBookList.isEmpty() ? EMPTY_HOLDER : BOOK_DISPLAY_HOLDER;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EMPTY_HOLDER) {
            HolderEmptyListBinding binding = HolderEmptyListBinding
                    .inflate(mLayoutInflater, parent, false);

            return new EmptyRecyclerHolder(binding);
        }

        HolderBookCoverDisplayBinding binding = HolderBookCoverDisplayBinding
                .inflate(mLayoutInflater, parent, false);
        return new BookCoverDisplayHolder(binding);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == EMPTY_HOLDER) {
            EmptyRecyclerHolder holder = (EmptyRecyclerHolder) viewHolder;
            holder.mBinding.setContentType(CONTENT_TYPE);
            return;
        }

        BookCoverDisplayHolder holder = (BookCoverDisplayHolder) viewHolder;

        Book book = mBookList.get(position);

        holder.mBinding.setBook(book);

        if (position % 2 != 0) {
            holder.mBinding.setIsRightSide(true);
        }

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
    }

    @Override
    public int getItemCount() {
        return mBookList.isEmpty() ? 1 : mBookList.size();
    }

    public void update(@NonNull List<Book> bookList) {
        mBookList = bookList;
        notifyDataSetChanged();
    }
}
