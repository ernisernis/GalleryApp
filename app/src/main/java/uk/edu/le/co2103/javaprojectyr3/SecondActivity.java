package uk.edu.le.co2103.javaprojectyr3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.sqlcipher.database.SQLiteDatabase;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class SecondActivity extends AppCompatActivity {

    Button  btnAdd, btnUpdate,  btnDelete;
    EditText edtEmail;
    ListView lstEmails;

    String saveEmail = ""; // Save current email to update/ delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

//        text = getIntent().getExtras().getString("Text");
//        textreceived = findViewById(R.id.textReceived);
//        textreceived.setText(text);

        SQLiteDatabase.loadLibs(this);


        //Init view
        lstEmails = findViewById(R.id.lstEmails);
        lstEmails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) lstEmails.getItemAtPosition(position);
                edtEmail.setText(item);
                saveEmail = item;
            }
        });

        edtEmail = findViewById(R.id.edtEmail);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(SecondActivity.this).insertNewEmail(edtEmail.getText().toString());
                reloadEmails();

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(SecondActivity.this).updateEmail(saveEmail,edtEmail.getText().toString());
                reloadEmails();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(SecondActivity.this).deleteEmail(edtEmail.getText().toString());
                reloadEmails();
            }
        });
        reloadEmails();
    }

    private void reloadEmails() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1,DBHelper.getInstance(this).getAllEmail());
        lstEmails.setAdapter(adapter);
    }
}