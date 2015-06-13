package me.tatarka.simplefragment.key;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import me.tatarka.simplefragment.SimpleDialogFragment;
import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentStateManager;

/**
 * An implementation of {@code SimpleFragmentKey} used to show dialogs, it requires a unique tag.
 */
public class DialogKey implements SimpleFragmentContainerKey {
    private SimpleFragmentKey parent;
    private String tag;

    public static DialogKey of(@NonNull String tag) {
        return new DialogKey(null, tag);
    }

    private DialogKey(SimpleFragmentKey parent, @NonNull String tag) {
        this.parent = parent;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public void attach(final SimpleFragmentManager container, View rootView, final SimpleFragment fragment) {
        if (!(fragment instanceof SimpleDialogFragment)) {
            throw new IllegalStateException(fragment + " is not a SimpleDialogFragment");
        }
        SimpleFragmentStateManager stateManager = container.getStateManager();
        stateManager.createView(fragment, LayoutInflater.from(rootView.getContext()), null);
        Dialog dialog = ((SimpleDialogFragment) fragment).getDialog();
        if (dialog != null) {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    container.remove(fragment);
                }
            });
        }
    }

    @Override
    public void detach(SimpleFragmentManager container, View rootView, SimpleFragment fragment) {
        SimpleFragmentStateManager stateManager = container.getStateManager();
        stateManager.destroyView(fragment);
    }

    @Override
    public SimpleFragmentKey getParent() {
        return parent;
    }

    @Override
    public DialogKey withParent(SimpleFragmentKey parent) {
        if (this.parent == parent) {
            return this;
        } else {
            return new DialogKey(parent, tag);
        }
    }

    @Override
    public boolean matches(@Nullable SimpleFragmentContainerKey other) {
        return other instanceof DialogKey && ((DialogKey) other).tag.equals(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DialogKey dialogKey = (DialogKey) o;

        if (parent != null ? !parent.equals(dialogKey.parent) : dialogKey.parent != null)
            return false;
        return tag.equals(dialogKey.tag);

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + tag.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TagKey(");
        if (parent != null) {
            builder.append(parent).append(", ");
        }
        builder.append(tag).append(")");
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parent, 0);
        dest.writeString(this.tag);
    }

    private DialogKey(Parcel in) {
        this.parent = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
        this.tag = in.readString();
    }

    public static final Creator<DialogKey> CREATOR = new Creator<DialogKey>() {
        public DialogKey createFromParcel(Parcel source) {
            return new DialogKey(source);
        }

        public DialogKey[] newArray(int size) {
            return new DialogKey[size];
        }
    };
}
