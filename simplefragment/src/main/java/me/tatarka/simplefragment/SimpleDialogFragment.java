package me.tatarka.simplefragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by evan on 3/25/15.
 */
public abstract class SimpleDialogFragment extends SimpleFragment {
    private static final String STATE = "me.tatarka.simplefragment.SimpleDialogFragment.STATE";

    private Dialog dialog;
    private boolean isDialogShown;

    public abstract Dialog onCreateDialog(View contentView);

    @Override
    @CallSuper
    public void onCreate(Context context, @Nullable Bundle state) {
        if (state != null) {
            State myState = state.getParcelable(STATE);
            if (myState != null) {
                isDialogShown = myState.isDialogShown;
            }
        }
    }

    @Override
    @CallSuper
    public void onSave(@NonNull Bundle state) {
        state.putParcelable(STATE, new State(isDialogShown));
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view) {
        dialog = onCreateDialog(view);
        if (isDialogShown) {
            dialog.show();
        }
    }

    @Override
    @CallSuper
    public void onViewDestroyed(@NonNull View view) {
        if (dialog != null) {
            // Clear the dismiss listener so that the fragment won't be destroyed when we dismiss it.
            dialog.setOnDismissListener(null);
            dialog.dismiss();
        }
    }

    @Nullable
    public final Dialog getDialog() {
        return dialog;
    }

    @CallSuper
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
        isDialogShown = true;
    }

    private static class State implements Parcelable {
        private boolean isDialogShown;

        State(boolean isDialogShown) {
            this.isDialogShown = isDialogShown;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(isDialogShown ? (byte) 1 : (byte) 0);
        }

        private State(Parcel in) {
            this.isDialogShown = in.readByte() != 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel source) {
                return new State(source);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }
}
