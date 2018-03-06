package com.supermart.supermart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DaftarKategoriActivity extends AppCompatActivity {

    GridView gridView;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_kategori);

        this.gridView = (GridView) findViewById(R.id.gridview);
        this.getSupportActionBar().setElevation(0);

        this.queue = Volley.newRequestQueue(this);

        this.syncData();
    }

    private void syncData() {
        String url = "https://supermart.herokuapp.com/daftar/kategori";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    GridAdapter adapter = new GridAdapter(jsonArray);
                    gridView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

        queue.add(request);
    }

    private class GridAdapter extends BaseAdapter {

        JSONArray data;

        public GridAdapter(JSONArray jsonArray) {
            this.data = jsonArray;
        }

        @Override
        public int getCount() {
            return this.data.length();
        }

        @Override
        public Object getItem(int i) {
            try {
                return this.data.getJSONObject(i);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            try {
                return this.data.getJSONObject(i).getLong("id");
            } catch (JSONException e) {
                return -1;
            }
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if(view == null)
                view = getLayoutInflater().inflate(R.layout.kategori_detail, null);

            final JSONObject kategori = (JSONObject) this.getItem(i);
            TextView nama = (TextView) view.findViewById(R.id.nama);
            TextView jumlah = (TextView) view.findViewById(R.id.jumlah);

            try {
                nama.setText(kategori.getString("nama"));
                jumlah.setText(kategori.getString("jumlah") + " produk");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DaftarKategoriActivity.this, DaftarProdukActivity.class);
                    intent.putExtra("idkategori", GridAdapter.this.getItemId(i));
                    try {
                        intent.putExtra("namaKategori", kategori.getString("nama"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                    startActivity(intent);
                }

            });

            return view;
        }
    }

}
