package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.content.Intent;
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
 * Created by evan on 6/13/15.
 */
public class FragmentChildActivityForResult extends SimpleFragment {
    private static final int REQUEST = 0;
    private static final String STATE_RESULT = "result";

    private int result = 0;

    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        if (state != null) {
            result = state.getInt(STATE_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_child_activity_for_result, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ResultActivity.class), REQUEST);
            }
        });

        if (result > 0) {
            showResult(view);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case ResultActivity.RESULT1:
                result = 1;
                break;
            case ResultActivity.RESULT2:
                result = 2;
                break;
        }

        View view = getView();
        if (view != null) {
            showResult(view);
        }
    }

    private void showResult(View view) {
        TextView resultText = (TextView) view.findViewById(R.id.result);
        resultText.setText("Result: " + result);
    }

    @Override
    public void onSave(@NonNull Bundle state) {
        state.putInt(STATE_RESULT, result);
    }
}
