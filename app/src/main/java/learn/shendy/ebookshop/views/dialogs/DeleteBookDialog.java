package learn.shendy.ebookshop.views.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.utils.ToastUtils;
import learn.shendy.ebookshop.views.models.AppViewModel;

public class DeleteBookDialog extends DialogFragment {
    public static final String TAG = "DeleteBookDialog";

    // MARK: Dialog State Observable

    private static PublishSubject<Boolean> sResult = PublishSubject.create();

    public static Observable<Boolean> sResultObservable() {
        return sResult
                .subscribeOn(Schedulers.single());
    }

    // MARK: Properties

    private Long mBookId;

    private ImageView mDeleteIV;
    private ImageView mConfirmIV;
    private ImageView mCancelIV;
    private Group mConfirmGroup;
    private ProgressBar mProgress;

    private CompositeDisposable mDeleteBookDisposable = new CompositeDisposable();

    private AppViewModel mViewModel;
    private CompositeDisposable mTimeoutDisposable = new CompositeDisposable();

    // MARK: Constructor Methods

    public DeleteBookDialog() {
        // Required empty public constructor
    }

    // MARK: Lifecycle Methods

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBookId = (Long) container.getTag();

        return inflater.inflate(R.layout.dialog_delete_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDialogViews(view, savedInstanceState);
        startDialogTimeout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimeoutDisposable.clear();
        mDeleteBookDisposable.clear();
    }

    @Nullable
    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return enter
                ? AnimatorInflater.loadAnimator(requireContext(), R.animator.enter_anticipate_overshoot)
                : AnimatorInflater.loadAnimator(requireContext(), R.animator.leave_anticipate_accelarator);
    }

    // MARK: Setup Methods

    private void setupDialogViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mDeleteIV = view.findViewById(R.id.delete_book_dialog_delete_iv);
        mConfirmIV = view.findViewById(R.id.delete_book_dialog_confirm_iv);
        mCancelIV = view.findViewById(R.id.delete_book_dialog_cancel_iv);
        mConfirmGroup = view.findViewById(R.id.book_delete_dialog_confirm_group);
        mProgress = view.findViewById(R.id.delete_book_dialog_progress);

        mDeleteIV.setOnClickListener(this::onDeleteClick);
        mConfirmIV.setOnClickListener(this::onConfirmClick);
        mCancelIV.setOnClickListener(this::onCancelClick);
    }

    private void onDeleteClick(View btn) {
        mDeleteIV.setVisibility(View.GONE);
        mConfirmGroup.setVisibility(View.VISIBLE);

        resetDialogTimeout();
    }

    @SuppressLint("CheckResult")
    private void onConfirmClick(View btn) {
        mConfirmGroup.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        Completable
                .fromAction(this::stopDialogTimeout)
                .andThen(Completable.defer(() -> mViewModel.deleteBook(mBookId)))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mDeleteBookDisposable::add)
                .subscribe(
                        this::onBookDeleted,
                        this::onDeleteBookFailed
                );
    }

    private void onCancelClick(View btn) {
        sResult.onNext(false);
    }

    private void startDialogTimeout() {
        Completable.timer(3000, TimeUnit.MILLISECONDS)
                .doOnSubscribe(mTimeoutDisposable::add)
                .doOnComplete(() -> sResult.onNext(false))
                .subscribe();

//        Observable.intervalRange(1, 500, 1, 10, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(i -> mProgress.incrementProgressBy(-1))
//                .subscribe();
    }

    private void stopDialogTimeout() {
        mTimeoutDisposable.clear();
    }

    private void resetDialogTimeout() {
        mTimeoutDisposable.clear();
        startDialogTimeout();
    }

    // MARK: Observer Listener

    private void onBookDeleted() {
        sResult.onNext(true);
    }

    private void onDeleteBookFailed(Throwable t) {
        ToastUtils.showUnhandledErrorToast(t);
        sResult.onNext(false);
    }
}
