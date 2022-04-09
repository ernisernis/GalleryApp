package uk.edu.le.co2103.javaprojectyr3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FolderActivity extends AppCompatActivity {

    Button addFolder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        addFolder = findViewById(R.id.addFolderButton);

        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FolderActivity.this,AddFolderActivity.class);
                startActivity(intent);
            }
        });
    }
}
