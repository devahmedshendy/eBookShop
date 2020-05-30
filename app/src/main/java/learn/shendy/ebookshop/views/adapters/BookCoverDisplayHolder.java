package learn.shendy.ebookshop.views.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import learn.shendy.ebookshop.databinding.HolderBookCoverDisplayBinding;

public class BookCoverDisplayHolder extends RecyclerView.ViewHolder {

    public interface BookCoverClickHandler {
        void onBookCoverClick(long bookId);
        void onBookCoverLongClick(int containerId);
    }

    HolderBookCoverDisplayBinding mBinding;

    BookCoverDisplayHolder(@NonNull HolderBookCoverDisplayBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }
}
