package com.ncr.interns.codecatchers.incredicabs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    Button login;
    EditText user,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user=(EditText)findViewById(R.id.editText);
        pass=(EditText)findViewById(R.id.editText2);

        login = findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().toString().equals("user")&&pass.getText().toString().equals("pass")){
                    Intent intent = new Intent(Login.this,Dashboard.class);
                    startActivity(intent);}
                else
                {
                    Toast.makeText(Login.this, "Invalid", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
