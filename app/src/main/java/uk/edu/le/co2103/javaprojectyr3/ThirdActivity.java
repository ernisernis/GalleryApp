package uk.edu.le.co2103.javaprojectyr3;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        goBack = findViewById(R.id.button);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThirdActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
