package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.key.DialogKey;

/**
 * Created by evan on 3/21/15.
 */
public class SimpleFragmentDialogs extends SimpleFragment {
    private static final DialogKey DIALOG_KEY = DialogKey.of("dialog");

    @Override
    public void onCreate(final Context context, @Nullable Bundle state) {
        SimpleFragmentDialog dialogFragment = (SimpleFragmentDialog) getSimpleFragmentContainer().find(DIALOG_KEY);
        if (dialogFragment != null) {
            setDialogListener(dialogFragment);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_dialogs, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        view.findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleFragmentDialog dialogFragment = getSimpleFragmentContainer().add(SimpleFragmentIntent.of(SimpleFragmentDialog.class), DIALOG_KEY);
                setDialogListener(dialogFragment);
                dialogFragment.show();
            }
        });
    }

    private void setDialogListener(SimpleFragmentDialog fragment) {
        fragment.setOnAlertButtonClickedListener(new SimpleFragmentDialog.OnAlertButtonClickedListener() {
            @Override
            public void onOkClicked() {
                Toast.makeText(getSimpleFragmentManager().getContext(), "ok clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                Toast.makeText(getSimpleFragmentManager().getContext(), "cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
