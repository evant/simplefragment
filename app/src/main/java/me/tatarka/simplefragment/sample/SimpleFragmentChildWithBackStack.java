package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import me.tatarka.simplefragment.SimpleFragment;

/**
 * Created by evan on 2/2/15.
 */
public class SimpleFragmentChildWithBackStack extends SimpleFragment {
    public static final String ARG_VIEW_ID = "view_id";
    public static final String ARG_STACK_COUNT = "stack_count";

    private int stackCount;
    private int viewId;

    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        stackCount = getIntent().getIntExtra(ARG_STACK_COUNT, 0);
        viewId = getIntent().getIntExtra(ARG_VIEW_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_child_with_backstack, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        TextView countView = (TextView) view.findViewById(R.id.backstack_count);
        countView.setText("" + stackCount);

        Button addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onAdd(viewId, stackCount);
            }
        });

        Button removeButton = (Button) view.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stackCount > 0) {
                    getListener().onRemove(SimpleFragmentChildWithBackStack.this);
                }
            }
        });
    }

    private OnBackStackRequestListener getListener() {
        Object parent = getParent();
        if (parent instanceof OnBackStackRequestListener) {
            return (OnBackStackRequestListener) parent;
        } else {
            return EMPTY_LISTENER;
        }
    }

    public interface OnBackStackRequestListener {
        void onAdd(int viewId, int stackCount);

        void onRemove(SimpleFragmentChildWithBackStack fragment);
    }

    private static final OnBackStackRequestListener EMPTY_LISTENER = new OnBackStackRequestListener() {
        @Override
        public void onAdd(int viewId, int stackCount) {

        }

        @Override
        public void onRemove(SimpleFragmentChildWithBackStack fragment) {

        }
    };
}
