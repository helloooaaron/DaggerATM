package com.example.daggeratm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private TextView textAmount;
    private EditText editAction;
    private CommandProcessor mCommandProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textAmount = (TextView) findViewById(R.id.text_amount);
        editAction = (EditText) findViewById(R.id.edit_action);
        mCommandProcessor = DaggerCommandProcessorFactory.create().processor();
    }

    public void action(View view) {
        String cmd = editAction.getText().toString();
        mCommandProcessor.process(cmd);
    }
}
