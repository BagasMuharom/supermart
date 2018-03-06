package com.supermart.supermart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

public class SplashActivity extends AppCompatActivity {

    TelephonyManager telephonyManager;

    RequestQueue requestQueue;

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        requestQueue = Volley.newRequestQueue(this);
        this.checkPermission();
    }


    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Ijinkan aplikasi ini mengakses informasi telepon anda !", Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 200);

            return;
        }

//        Toast.makeText(this, telephonyManager.getDeviceId(), Toast.LENGTH_LONG).show();
        this.deviceId = telephonyManager.getDeviceId();
        Util.imei = this.deviceId;
        this.checkDeviceIdToServer();
    }

    private void checkDeviceIdToServer() {
        String url = "https://supermart.herokuapp.com/checkid";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getBoolean("ada")) {
                        Intent intent = new Intent(SplashActivity.this, DaftarKategoriActivity.class);
                        finish();
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                        intent.putExtra("imei", SplashActivity.this.deviceId);
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
                Toast.makeText(SplashActivity.this, "Terdapat gangguan, silahkan coba beberapa saat lagi !", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", SplashActivity.this.deviceId);
                return params;
            }
        };

        this.requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 200) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                this.deviceId = telephonyManager.getDeviceId();
                Util.imei = this.deviceId;
                this.checkDeviceIdToServer();
            }

        }
    }

}
