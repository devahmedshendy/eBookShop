package learn.shendy.ebookshop.views.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import learn.shendy.ebookshop.databinding.HolderEmptyListBinding;

public class EmptyRecyclerHolder extends RecyclerView.ViewHolder {

    HolderEmptyListBinding mBinding;

    public EmptyRecyclerHolder(@NonNull HolderEmptyListBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }
}
