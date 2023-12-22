package com.example.myapp1;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        if (intent != null) {
            String receivedMessage = intent.getStringExtra("key");
            TextView textView = findViewById(R.id.textView2);
            textView.setText(String.format("Переданный параметр: %s", receivedMessage));
        }

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(v -> finish());
    }
}