package com.ncr.interns.codecatchers.incredicabs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.HEAD;

public class Login extends AppCompatActivity {

    Button login;
    EditText user, pass;

    String url = "http://192.168.0.141:8080/DemoProject/login/doLogin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user =  findViewById(R.id.editText);
        pass =  findViewById(R.id.editText2);

        login = findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Dashboard.class);
                startActivity(intent);
            }
        });
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//
//
//                //json for request
//                JSONObject jsonBodyRequest = new JSONObject();
//                try {
//                    jsonBodyRequest.put("qlid", user.getText().toString());
//                    jsonBodyRequest.put("password", pass.getText().toString());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST,
//                        url,
//                        jsonBodyRequest,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//
//                                Log.i("VOLLEY", "inside onResponse method:login");
//                                Log.i("VOLLEY", response.toString());
//
//                                try {
//                                    if (response.getString("status").equalsIgnoreCase("success")) {
//

//                                        String Emp_qlid = response.getString("Emp_qlid");
//                                        String Mgr_Qlid = response.getString("Mgr_Qlid");
//                                        String Emp_Address1 = response.getString("Emp_Address1");
//                                        getSharedPreferences(null, MODE_PRIVATE).edit().putString("Emp_qlid", Emp_qlid).apply();
//                                        getSharedPreferences(null, MODE_PRIVATE).edit().putString("Mgr_Qlid", Mgr_Qlid).apply();
//                                        getSharedPreferences(null, MODE_PRIVATE).edit().putString("Emp_Address1", Emp_Address1).apply();
//                                        //  Toast.makeText(Login.this, Emp_qlid+Mgr_Qlid+Emp_Address1, Toast.LENGTH_LONG).show();
//
//                                        Intent intent = new Intent(Login.this, Dashboard.class);

//                                        String Emp_qlid=response.getString( "Emp_qlid");
//                                        String Mgr_Qlid=response.getString( "Mgr_Qlid");
//                                        String Emp_Address1=response.getString( "Emp_Address1");
//                                        getSharedPreferences(null,MODE_PRIVATE).edit().putString("Emp_qlid",Emp_qlid).apply();
//                                        getSharedPreferences(null,MODE_PRIVATE).edit().putString("Mgr_Qlid",Mgr_Qlid).apply();
//                                        getSharedPreferences(null,MODE_PRIVATE).edit().putString("Emp_Address1",Emp_Address1).apply();
//                                      //  Toast.makeText(Login.this, Emp_qlid+Mgr_Qlid+Emp_Address1, Toast.LENGTH_LONG).show();
//
//                                        Intent intent = new Intent(Login.this,Dashboard.class);

//                                        startActivity(intent);
//                                    } else {
//                                        Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // Do something when error occurred
//                                Log.d("VOLLEY", "Something went wrong");
//                                error.printStackTrace();
//                            }
//                        });
//
//                RESTService.getInstance(Login.this).addToRequestQueue(jsonObjRequest);
//
//            }

    }
        @Override
        protected void onStop () {
            super.onStop();
            finish();
        }
    }


