package me.tatarka.simplefragment.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by evan on 6/13/15.
 */
public class ResultActivity extends AppCompatActivity {
    public static final int RESULT1 = RESULT_FIRST_USER + 1;
    public static final int RESULT2 = RESULT_FIRST_USER + 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Button result1 = (Button) findViewById(R.id.result1);
        Button result2 = (Button) findViewById(R.id.result2);

        result1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT1);
                finish();
            }
        });

        result2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT2);
                finish();
            }
        });
    }
}
