package bus.driver.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;

import bus.driver.R;

/**
 * Created by Lilaoda on 2017/11/8.
 * Email:749948218@qq.com
 * <p>
 * 确认费用对话框
 */

public class ConfirmExpensesDialog extends BottomSheetDialogFragment {

    public static ConfirmExpensesDialog newInstance() {

        Bundle args = new Bundle();

        ConfirmExpensesDialog fragment = new ConfirmExpensesDialog();
        fragment.setArguments(args);
        return fragment;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_confirm_expenses, container,false);
//        final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(view);
//        view.measure(0,0);
//        bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//            }
//        });
//        return view;
//    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_expenses, null);
        contentView.measure(0,0);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(callback);
            ((BottomSheetBehavior) behavior).setPeekHeight(contentView.getMeasuredHeight());
        }
    }

    private BottomSheetBehavior.BottomSheetCallback callback =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }


                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {


                }
            };
}
