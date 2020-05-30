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
public class DiscardChangesDialog extends DialogFragment {
    public static final String TAG = "DiscardChangesDialog";

    // MARK: Dialog State Observable

    private static PublishSubject<Boolean> sResult = PublishSubject.create();

    public static Observable<Boolean> sResultObservable() {
        return sResult
                .subscribeOn(Schedulers.single());
    }

    // MARK: Constructor Methods

    public DiscardChangesDialog() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_discard_changes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.discard_changes_dialog_save_btn)
                .setOnClickListener(__ -> sResult.onNext(true));

        view.findViewById(R.id.discard_changes_dialog_discard_btn)
                .setOnClickListener(__ -> sResult.onNext(false));
    }
}
