package com.supermart.supermart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button daftar;

    EditText nama, email, telp, alamat;

    TextView alertsuccess, alerterror;

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.daftar = (Button) findViewById(R.id.daftar_btn);
        this.nama = (EditText) findViewById(R.id.nama);
        this.email = (EditText) findViewById(R.id.email);
        this.alamat = (EditText) findViewById(R.id.alamat);
        this.telp = (EditText) findViewById(R.id.telp);
        alertsuccess = (TextView) findViewById(R.id.alertsuccess);
        alerterror = (TextView) findViewById(R.id.alerterror);

        this.deviceId = this.getIntent().getStringExtra("imei");

        this.daftar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                String url = "https://supermart.herokuapp.com/daftar";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("success")) {
                                Toast.makeText(RegisterActivity.this, "Berhasil mendaftar !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, DaftarProdukActivity.class);
                                finish();
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Gagal terhubung ke server !", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", RegisterActivity.this.deviceId);
                        params.put("nama", RegisterActivity.this.nama.getText().toString());
                        params.put("email", RegisterActivity.this.email.getText().toString());
                        params.put("telp", RegisterActivity.this.telp.getText().toString());
                        params.put("alamat", RegisterActivity.this.alamat.getText().toString());
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            }

        });
    }

}
