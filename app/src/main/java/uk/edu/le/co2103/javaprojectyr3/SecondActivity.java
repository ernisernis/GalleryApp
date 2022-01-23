package uk.edu.le.co2103.javaprojectyr3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    TextView textreceived;
    Button button2;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        text = getIntent().getExtras().getString("Text");
        textreceived = findViewById(R.id.textReceived);
        textreceived.setText(text);

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}