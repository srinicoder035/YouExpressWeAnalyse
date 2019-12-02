package com.example.sid.ennapannradhuneterila;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by S.I.D on 16/01/18.
 */

public class Homepage extends Activity {
    public String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button button = (Button)findViewById(R.id.button2);

        final TextView tv = (TextView)findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = tv.getText().toString();
                goToThirdActivity();
            }
        });
    }

    public void goToThirdActivity()
    {
        Intent intent = new Intent(this, TweetCount.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}