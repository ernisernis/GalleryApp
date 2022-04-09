package uk.edu.le.co2103.javaprojectyr3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddFolderActivity extends AppCompatActivity {

    EditText inputFolderName;
    Button createButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfolder);
        inputFolderName = findViewById(R.id.inputName);
        createButton = findViewById(R.id.createFolderButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get name of the folder and create DB table with PK as a folder name. Create 3 columns.
                Toast.makeText(AddFolderActivity.this, inputFolderName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
