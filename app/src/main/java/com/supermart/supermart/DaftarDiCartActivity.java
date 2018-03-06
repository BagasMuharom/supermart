package com.supermart.supermart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class DaftarDiCartActivity extends AppCompatActivity {

    GridView lv_daftar_produk;

    JSONArray daftar_produk;

    RequestQueue requestQueue;

    Button checkout_btn;

    TextView total_harga;

    long total_harga_real;

    boolean render_total_harga = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_di_cart);

        this.lv_daftar_produk = (GridView) findViewById(R.id.daftar_produk);
        this.checkout_btn = (Button) findViewById(R.id.checkout);
        this.total_harga = (TextView) findViewById(R.id.total_harga);

        this.requestQueue = Volley.newRequestQueue(this);

        this.checkout_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DaftarDiCartActivity.this, PilihAlamatActivity.class);
                startActivity(intent);
            }

        });

        this.initDaftarProduk();
    }

    private void initDaftarProduk() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://supermart.herokuapp.com/daftar/cart/" + Util.imei;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    DaftarDiCartActivity.this.daftar_produk = result.getJSONArray("daftar");
                    total_harga_real = result.getLong("harga");
                    total_harga.setText("Rp " + result.getLong("harga"));
                    DaftarDiCartActivity.DaftarProdukAdapter adapter = new DaftarProdukAdapter(DaftarDiCartActivity.this.daftar_produk);
                    lv_daftar_produk.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);

    }

    private class DaftarProdukAdapter extends BaseAdapter {

        JSONArray data;

        public DaftarProdukAdapter(JSONArray data) {
            this.data = data;
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
                return new JSONObject();
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
            view = LayoutInflater.from(DaftarDiCartActivity.this).inflate(R.layout.produk_detail, viewGroup, false);

            TextView nama = (TextView) view.findViewById(R.id.nama);
            TextView harga = (TextView) view.findViewById(R.id.harga);
            Button kurang = (Button) view.findViewById(R.id.kurang);
            Button tambah = (Button) view.findViewById(R.id.tambah);
            final EditText jumlah = (EditText) view.findViewById(R.id.jumlah);

            jumlah.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!render_total_harga)
                        return;

                    final int jumlah = Integer.parseInt(editable.toString());

                    String url = "https://supermart.herokuapp.com/tambah-ke-cart";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(jsonObject.getBoolean("success")) {
                                    // update total harga
                                    DaftarDiCartActivity.this.total_harga_real += (((JSONObject) getItem(i)).getLong("harga"));
                                    render_total_harga = false;
                                    total_harga.setText("Rp " + DaftarDiCartActivity.this.total_harga_real);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(DaftarDiCartActivity.this, "Gagal !", Toast.LENGTH_SHORT).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("konsumen", Util.imei);
                            params.put("produk", String.valueOf(getItemId(i)));
                            params.put("jumlah", String.valueOf(jumlah));
                            return params;
                        }

                    };

                    requestQueue.add(stringRequest);
                }

            });

            kurang.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int temp = Integer.parseInt(jumlah.getText().toString());
                    render_total_harga = true;
                    jumlah.setText(--temp + "");
                }

            });

            tambah.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int temp = Integer.parseInt(jumlah.getText().toString());
                    render_total_harga = true;
                    jumlah.setText(++temp + "");
                }

            });

            try {
                nama.setText(((JSONObject) this.getItem(i)).getString("nama"));
                harga.setText("Rp" + ((JSONObject) this.getItem(i)).getString("harga"));
                jumlah.setText(((JSONObject) this.getItem(i)).getString("jumlah"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

}
