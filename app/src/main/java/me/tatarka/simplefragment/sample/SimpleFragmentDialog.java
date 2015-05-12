package me.tatarka.simplefragment.sample;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleDialogFragment;

/**
 * Created by evan on 3/22/15.
 */
public class SimpleFragmentDialog extends SimpleDialogFragment {
    private OnAlertButtonClickedListener listener;

    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        super.onCreate(context, state);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_dialog, parent, false);
    }

    @Override
    public Dialog onCreateDialog(View contentView) {
        return new AlertDialog.Builder(contentView.getContext())
                .setView(contentView)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onOkClicked();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onCancelClicked();
                        }
                    }
                })
                .create();
    }

    public void setOnAlertButtonClickedListener(OnAlertButtonClickedListener listener) {
        this.listener = listener;
    }

    public interface OnAlertButtonClickedListener {
        void onOkClicked();

        void onCancelClicked();
    }
}
