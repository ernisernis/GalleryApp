package uk.edu.le.co2103.javaprojectyr3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText text;
    Button clickMe;
    String textHere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickMe = findViewById(R.id.button);
        text = findViewById(R.id.text);

        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textHere = text.getText().toString();
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("Text", textHere);
                startActivity(intent);
            }
        });
    }
}