package learn.shendy.ebookshop.views.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import learn.shendy.ebookshop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmDeleteDialog extends DialogFragment {
    public static final String TAG = "ConfirmDeleteDialog";

    // MARK: Dialog State Observable

    public static final PublishSubject<Boolean> sDialogState = PublishSubject.create();

    public static Observable<Boolean> sStateObservable() {
        return sDialogState
                .subscribeOn(Schedulers.computation());
    }

    // MARK: Constructor Methods

    public ConfirmDeleteDialog() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_delete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.confirm_delete_dialog_delete_btn)
                .setOnClickListener(__ -> sDialogState.onNext(true));

        view.findViewById(R.id.confirm_delete_dialog_cancel_btn)
                .setOnClickListener(__ -> sDialogState.onNext(false));

    }
}
