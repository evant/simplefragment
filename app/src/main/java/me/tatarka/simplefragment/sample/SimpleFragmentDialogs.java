package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;

/**
 * Created by evan on 3/21/15.
 */
public class SimpleFragmentDialogs extends SimpleFragment<SimpleFragment.ViewHolder> {
    @Override
    public void onCreate(final Context context, @Nullable Bundle state) {
        SimpleFragmentDialog dialogFragment = (SimpleFragmentDialog) getSimpleFragmentDialogContainer().find("dialog");
        if (dialogFragment != null) {
            setDialogListener(dialogFragment);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final LayoutInflater inflater, final ViewGroup parent) {
        return new ViewHolder() {
            @Override
            public View getView() {
                View view = inflater.inflate(R.layout.fragment_dialogs, parent, false);
                view.findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SimpleFragmentDialog dialogFragment = getSimpleFragmentDialogContainer().add(new SimpleFragmentIntent<>(SimpleFragmentDialog.class), "dialog");
                        setDialogListener(dialogFragment);
                        dialogFragment.show();
                    }
                });
                return view;
            }
        };
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
