package learn.shendy.ebookshop.views.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import learn.shendy.ebookshop.R;
import learn.shendy.ebookshop.views.dialogs.ConfirmDeleteDialog;
import learn.shendy.ebookshop.views.dialogs.DiscardChangesDialog;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    final CompositeDisposable sLongTermDisposables = new CompositeDisposable();
    final CompositeDisposable sShortTermDisposables = new CompositeDisposable();

    final Scheduler mSingleScheduler = Schedulers.single();

    NavController mNavController;
    private FragmentManager mFragmentManager;

    // MARK: Lifecycle Methods

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentManager = requireActivity().getSupportFragmentManager();
    }

    @Override
    public void onDestroyView() {
        sShortTermDisposables.clear();
        sLongTermDisposables.clear();
        super.onDestroyView();
    }

    // MARK: Setup Methods

    void setupOnBackPressedCallback(OnBackPressedCallback callback) {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), callback);
    }

    void openDialogFragment(int containerId, Fragment fragment, String tag) {
        hideKeyboard();

        mFragmentManager
                .beginTransaction()
                .add(containerId, fragment, tag)
                .commit();
    }

    void openDialogFragmentByTag(String tag) {
        hideKeyboard();

        Fragment fragment;
        switch (tag) {
            case DiscardChangesDialog.TAG:
                fragment = new DiscardChangesDialog();
                break;

            case ConfirmDeleteDialog.TAG:
                fragment = new ConfirmDeleteDialog();
                break;

            default:
                throw new UnsupportedOperationException();
        }

        mFragmentManager
                .beginTransaction()
                .add(R.id.main_dialog_container, fragment, tag)
                .commit();
    }

    void closeDialogFragmentByTag(String tag) {
        hideKeyboard();

        Fragment fragment = mFragmentManager.findFragmentByTag(tag);

        if (fragment != null) {
            mFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commit();

            sShortTermDisposables.clear();
        }
    }

//    void openDialogFragmentAnimated(
//            int containerId, Fragment fragment, String tag,
//            int enterAnimator, int leaveAnimator
//    ) {
//        hideKeyboard();
//
//        mFragmentManager
//                .beginTransaction()
//                .replace(containerId, fragment, tag)
//                .setCustomAnimations(enterAnimator, leaveAnimator)
//                .commit();
//    }

//    void closeWholeFragment() {
//        clearBackStack();
//        navigateBack();
//    }

//    void clearBackStack() {
//        onBackPressedConfirmDeleteDialogCallback.setEnabled(false);
//    }

    void hideKeyboard() {
        UIUtil.hideKeyboard(requireActivity());
    }

    void navigateBack() {
        hideKeyboard();
        requireActivity().onBackPressed();
    }
}
