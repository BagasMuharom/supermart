package com.supermart.supermart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PilihAlamatActivity extends AppCompatActivity {

    ListView listView;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_alamat);

        this.listView = (ListView) findViewById(R.id.daftar_alamat);
        this.requestQueue = Volley.newRequestQueue(this);

        this.initDaftarAlamat();
    }

    private void initDaftarAlamat() {
        String url = "https://supermart.herokuapp.com/daftar/alamat/" + Util.imei;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    DaftarAlamatAdapter adapter = new DaftarAlamatAdapter(jsonArray, PilihAlamatActivity.this);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }

    private class DaftarAlamatAdapter extends BaseAdapter {

        JSONArray jsonArray;

        AppCompatActivity appCompatActivity;

        public DaftarAlamatAdapter(JSONArray jsonArray, AppCompatActivity context) {
            this.jsonArray = jsonArray;
            this.appCompatActivity = context;
        }

        @Override
        public int getCount() {
            return this.jsonArray.length();
        }

        @Override
        public Object getItem(int i) {
            try {
                return this.jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            try {
                return this.jsonArray.getJSONObject(i).getLong("id");
            } catch (JSONException e) {
                return -1;
            }
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if(view == null)
                view = appCompatActivity.getLayoutInflater().inflate(R.layout.alamat_detail_lv, viewGroup, false);

            final JSONObject alamat_json = (JSONObject) this.getItem(i);
            TextView nama = (TextView) view.findViewById(R.id.nama);
            TextView telp = (TextView) view.findViewById(R.id.telp);
            TextView alamat = (TextView) view.findViewById(R.id.alamat);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    long idalamat = DaftarAlamatAdapter.this.getItemId(i);
                    String url = "https://supermart.herokuapp.com/pesan/" + Util.imei + "/" + idalamat;
                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(jsonObject.getBoolean("success")) {
                                    Intent intent = new Intent(PilihAlamatActivity.this, DaftarPesananActivity.class);
                                    intent.putExtra("cleartask", true);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    requestQueue.add(request);
                }

            });

            try {
                nama.setText(alamat_json.getString("penerima"));
                telp.setText(alamat_json.getString("telp"));
                alamat.setText(alamat_json.getString("alamat"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }

    }

}
