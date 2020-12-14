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
    private CommandRouterFactory mCommandRouterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textAmount = (TextView) findViewById(R.id.text_amount);
        editAction = (EditText) findViewById(R.id.edit_action);
        mCommandRouterFactory = DaggerCommandRouterFactory.create();
    }

    public void action(View view) {
        String cmd = editAction.getText().toString();
        CommandRouter commandRouter = mCommandRouterFactory.router();  // Create a new instance of CommandRouter every time.
        commandRouter.route(cmd);
    }
}
