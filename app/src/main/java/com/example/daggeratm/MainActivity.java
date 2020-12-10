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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textAmount = (TextView) findViewById(R.id.text_amount);
        editAction = (EditText) findViewById(R.id.edit_action);
    }

    public void action(View view) {
        String cmd = editAction.getText().toString();
        CommandRouterFactory commandRouterFactory = DaggerCommandRouterFactory.create();
        CommandRouter commandRouter = commandRouterFactory.router();  // Create a new instance of CommandRouter every time.
        commandRouter.route(cmd, textAmount);
    }
}
